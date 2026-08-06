package com.example.smartcitizensystem.ui.presentation.main.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcitizensystem.data.models.User
import com.example.smartcitizensystem.data.repository.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

private const val TAG = "ProfileViewModel"

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {

    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> = _user

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    // ✅ Separate, lightweight loading flags for the two inline updates below, so saving
    // an address doesn't show the full-screen loading state and blow away the form.
    private val _isSavingAddress = mutableStateOf(false)
    val isSavingAddress: State<Boolean> = _isSavingAddress

    private val _isUploadingPhoto = mutableStateOf(false)
    val isUploadingPhoto: State<Boolean> = _isUploadingPhoto

    // ✅ Track face verification status
    private val _isFaceVerified = mutableStateOf(false)
    val isFaceVerified: State<Boolean> = _isFaceVerified

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = authRepository.getCurrentUserProfile()
                result.onSuccess { userProfile ->
                    _user.value = userProfile
                    _isFaceVerified.value = userProfile.isFaceVerified ?: false
                    Log.d(TAG, "User profile loaded: ${userProfile.name}")
                }.onFailure { error ->
                    _error.value = error.message ?: "Failed to load profile"
                    Log.e(TAG, "Failed to load profile: ${error.message}", error)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading profile"
                Log.e(TAG, "Error loading profile: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshProfile() {
        loadUserProfile()
    }

    /** Persists a new address and updates local state optimistically on success. */
    fun updateAddress(address: String) {
        val trimmed = address.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch {
            _isSavingAddress.value = true
            val result = authRepository.updateUserProfileFields(mapOf("address" to trimmed))
            result.onSuccess {
                _user.value = _user.value?.copy(address = trimmed)
                Log.d(TAG, "Address updated")
            }.onFailure { error ->
                _error.value = error.message ?: "Failed to update address"
                Log.e(TAG, "Failed to update address: ${error.message}", error)
            }
            _isSavingAddress.value = false
        }
    }

    /**
     * Persists the picked photo's URI string and updates local state optimistically.
     *
     * TODO: This currently stores the local content:// URI directly, which is enough to
     * show a preview but is NOT durable — it won't resolve on other devices, and Android
     * may revoke the URI permission after the app process dies. Before shipping, upload
     * the file bytes to Firebase Storage here and persist the resulting https:// download
     * URL instead (see the FirebaseAuthRepository doc comment on this being intentionally
     * deferred).
     */
    fun updateProfileImageUri(uri: String) {
        viewModelScope.launch {
            _isUploadingPhoto.value = true
            val result = authRepository.updateUserProfileFields(mapOf("profileImage" to uri))
            result.onSuccess {
                _user.value = _user.value?.copy(profileImage = uri)
                Log.d(TAG, "Profile photo updated")
            }.onFailure { error ->
                _error.value = error.message ?: "Failed to update profile photo"
                Log.e(TAG, "Failed to update profile photo: ${error.message}", error)
            }
            _isUploadingPhoto.value = false
        }
    }

    /**
     * Updates the face verification status after a successful face scan.
     * Called from FaceVerificationViewModel after verification completes.
     */
    fun updateFaceVerificationStatus(isVerified: Boolean) {
        viewModelScope.launch {
            val result = authRepository.updateUserProfileFields(mapOf("isFaceVerified" to isVerified))
            result.onSuccess {
                _user.value = _user.value?.copy(isFaceVerified = isVerified)
                _isFaceVerified.value = isVerified
                Log.d(TAG, "Face verification status updated: $isVerified")
            }.onFailure { error ->
                Log.e(TAG, "Failed to update face verification status: ${error.message}", error)
            }
        }
    }

    /**
     * Check if the user is fully verified (all 4 items completed).
     * Items: Name, Phone, Address, Profile Photo
     */
    fun isFullyVerified(): Boolean {
        val user = _user.value ?: return false
        return user.name.isNotBlank() &&
                user.phone.isNotBlank() &&
                user.address.isNotBlank() &&
                !user.profileImage.isNullOrBlank()
    }

    /**
     * Get the number of completed profile items.
     * Items: Name, Phone, Address, Profile Photo
     */
    fun getCompletedItemsCount(): Int {
        val user = _user.value ?: return 0
        var count = 0
        if (user.name.isNotBlank()) count++
        if (user.phone.isNotBlank()) count++
        if (user.address.isNotBlank()) count++
        if (!user.profileImage.isNullOrBlank()) count++
        return count
    }

    /**
     * Get profile completion percentage.
     */
    fun getCompletionPercentage(): Float {
        val totalItems = 4
        val completed = getCompletedItemsCount()
        return completed.toFloat() / totalItems.toFloat()
    }
}