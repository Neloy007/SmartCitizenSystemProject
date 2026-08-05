package com.example.smartcitizensystem.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log
import com.example.smartcitizensystem.data.models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

private const val TAG = "FirebaseAuthRepo"

@Singleton
class FirebaseAuthRepository @Inject constructor() {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    // Auth State using Channel
    private val authStateChannel = Channel<FirebaseUser?>(Channel.UNLIMITED)

    init {
        Log.d(TAG, "FirebaseAuthRepository initialized")
        auth.addAuthStateListener { firebaseAuth ->
            Log.d(TAG, "Auth state changed: ${firebaseAuth.currentUser?.email}")
            authStateChannel.trySend(firebaseAuth.currentUser)
        }
    }

    val authStateFlow: Flow<FirebaseUser?> = authStateChannel.receiveAsFlow()

    suspend fun signUpWithEmail(
        email: String,
        password: String,
        name: String,
        phone: String,
        nid: String
    ): Result<FirebaseUser> {
        return try {
            Log.d(TAG, "Signing up user: $email")
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User creation failed")
            Log.d(TAG, "User created: ${user.uid}")

            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            user.updateProfile(profileUpdates).await()
            Log.d(TAG, "Profile updated")

            saveUserToFirestore(
                userId = user.uid,
                name = name,
                email = email,
                phone = phone,
                nid = nid
            )
            Log.d(TAG, "User saved to Firestore")

            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Signup failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun loginWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            Log.d(TAG, "Logging in user: $email")
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Login failed")
            Log.d(TAG, "Login successful: ${user.email}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Login failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            Log.d(TAG, "Signing out")
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Sign out failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            Log.d(TAG, "Sending password reset email to: $email")
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Password reset failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteAccount(): Result<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("No user logged in")
            Log.d(TAG, "Deleting account: ${user.uid}")
            firestore.collection("users").document(user.uid).delete().await()
            user.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Delete account failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    private suspend fun saveUserToFirestore(
        userId: String,
        name: String,
        email: String,
        phone: String,
        nid: String
    ) {
        val userData = mapOf(
            "userId" to userId,
            "name" to name,
            "email" to email,
            "phone" to phone,
            "nid" to nid,
            "address" to "",
            "createdAt" to System.currentTimeMillis(),
            "updatedAt" to System.currentTimeMillis(),
            "isEmailVerified" to false,
            "isFaceVerified" to false,
            "role" to "citizen"
        )

        firestore.collection("users").document(userId).set(userData).await()
    }

    suspend fun getUserData(userId: String): Result<Map<String, Any>> {
        return try {
            Log.d(TAG, "Getting user data for: $userId")
            val document = firestore.collection("users").document(userId).get().await()
            val data = document.data ?: throw Exception("User data not found")
            Result.success(data)
        } catch (e: Exception) {
            Log.e(TAG, "Get user data failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Add this method to get the current user's profile data using your User model
    suspend fun getCurrentUserProfile(): Result<User> {
        return try {
            val firebaseUser = auth.currentUser ?: throw Exception("No user logged in")
            Log.d(TAG, "Getting user profile for: ${firebaseUser.uid}")
            val document = firestore.collection("users").document(firebaseUser.uid).get().await()
            val data = document.data ?: throw Exception("User data not found")

            val user = User(
                id = data["id"] as? String ?: firebaseUser.uid,
                name = data["name"] as? String ?: firebaseUser.displayName ?: "",
                email = data["email"] as? String ?: firebaseUser.email ?: "",
                phone = data["phone"] as? String ?: "",
                nid = data["nid"] as? String ?: "",
                address = data["address"] as? String ?: "",
                profileImage = data["profileImage"] as? String,
                isBiometricEnabled = data["isBiometricEnabled"] as? Boolean ?: false,
                // ✅ New fields — read from Firestore, falling back sensibly if an older
                // document doesn't have them yet (pre-migration users).
                isEmailVerified = data["isEmailVerified"] as? Boolean ?: firebaseUser.isEmailVerified,
                isFaceVerified = data["isFaceVerified"] as? Boolean ?: false,
                createdAt = data["createdAt"] as? String ?: System.currentTimeMillis().toString(),
                updatedAt = data["updatedAt"] as? String ?: System.currentTimeMillis().toString()
            )
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Get user profile failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Generic partial update for the current user's Firestore document. Used to persist
     * profile-completion fields (address, profileImage) without needing a dedicated method
     * per field. `updatedAt` is stamped automatically.
     *
     * NOTE: `profileImage` here is expected to be a URL string. If you're passing a local
     * content:// URI (as the current photo picker does), it will persist but won't resolve
     * on other devices or after the OS revokes the URI grant — see the TODO in
     * ProfileViewModel.updateProfileImageUri for wiring real Firebase Storage upload.
     */
    suspend fun updateUserProfileFields(fields: Map<String, Any>): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("No user logged in")
            val updates = fields + mapOf("updatedAt" to System.currentTimeMillis())
            firestore.collection("users").document(uid).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Update profile fields failed: ${e.message}", e)
            Result.failure(e)
        }
    }
}