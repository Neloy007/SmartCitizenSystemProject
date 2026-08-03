package com.example.smartcitizensystem.ui.presentation.auth.signup

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcitizensystem.data.repository.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

private const val TAG = "SignupViewModel"

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {

    private val _signupState = mutableStateOf<SignupUiState>(SignupUiState.Idle)
    val signupState: State<SignupUiState> = _signupState

    fun signupWithEmail(
        name: String,
        email: String,
        phone: String,
        nid: String,
        password: String,
        confirmPassword: String
    ) {
        Log.d(TAG, "signupWithEmail called for: $email")

        // Validation
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() ||
            nid.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Log.w(TAG, "Empty fields")
            _signupState.value = SignupUiState.Error("Please fill in all fields")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Log.w(TAG, "Invalid email format: $email")
            _signupState.value = SignupUiState.Error("Please enter a valid email address")
            return
        }

        if (password != confirmPassword) {
            Log.w(TAG, "Passwords do not match")
            _signupState.value = SignupUiState.Error("Passwords do not match")
            return
        }

        if (password.length < 6) {
            Log.w(TAG, "Password too short")
            _signupState.value = SignupUiState.Error("Password must be at least 6 characters")
            return
        }

        if (phone.length < 10) {
            Log.w(TAG, "Invalid phone number")
            _signupState.value = SignupUiState.Error("Please enter a valid phone number")
            return
        }

        if (nid.length < 10) {
            Log.w(TAG, "Invalid NID number")
            _signupState.value = SignupUiState.Error("Please enter a valid NID number")
            return
        }

        viewModelScope.launch {
            Log.d(TAG, "Attempting signup...")
            _signupState.value = SignupUiState.Loading
            try {
                val result = authRepository.signUpWithEmail(
                    email = email,
                    password = password,
                    name = name,
                    phone = phone,
                    nid = nid
                )
                result.onSuccess { user ->
                    Log.d(TAG, "Signup successful for: ${user.email}")
                    _signupState.value = SignupUiState.Success(user)
                }.onFailure { error ->
                    Log.e(TAG, "Signup failed: ${error.message}", error)
                    _signupState.value = SignupUiState.Error(
                        error.message ?: "Signup failed. Please try again."
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Signup exception: ${e.message}", e)
                _signupState.value = SignupUiState.Error(
                    "Network error: ${e.message}"
                )
            }
        }
    }

    fun resetState() {
        Log.d(TAG, "Resetting state")
        _signupState.value = SignupUiState.Idle
    }
}

sealed class SignupUiState {
    object Idle : SignupUiState()
    object Loading : SignupUiState()
    data class Success(val user: FirebaseUser) : SignupUiState()
    data class Error(val message: String) : SignupUiState()
}