package com.example.smartcitizensystem.ui.presentation.auth.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcitizensystem.data.repository.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

private const val TAG = "LoginViewModel"

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {

    private val _loginState = mutableStateOf<LoginUiState>(LoginUiState.Idle)
    val loginState: State<LoginUiState> = _loginState

    init {
        viewModelScope.launch {
            authRepository.authStateFlow.collectLatest { user ->
                Log.d(TAG, "Auth state changed: user = $user")
                if (user != null) {
                    _loginState.value = LoginUiState.Success(user)
                }
            }
        }
    }

    fun loginWithEmail(email: String, password: String) {
        Log.d(TAG, "loginWithEmail called with email: $email")

        if (email.isEmpty() || password.isEmpty()) {
            Log.w(TAG, "Email or password empty")
            _loginState.value = LoginUiState.Error("Please fill in all fields")
            return
        }

        viewModelScope.launch {
            Log.d(TAG, "Attempting login...")
            _loginState.value = LoginUiState.Loading
            try {
                val result = authRepository.loginWithEmail(email, password)
                Log.d(TAG, "Login result: $result")
                result.onSuccess { user ->
                    Log.d(TAG, "Login successful for user: ${user.email}")
                    _loginState.value = LoginUiState.Success(user)
                }.onFailure { error ->
                    Log.e(TAG, "Login failed: ${error.message}", error)
                    _loginState.value = LoginUiState.Error(
                        error.message ?: "Login failed. Please check your credentials."
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login exception: ${e.message}", e)
                _loginState.value = LoginUiState.Error(
                    "Network error: ${e.message}"
                )
            }
        }
    }

    fun onBiometricSuccess() {
        Log.d(TAG, "Biometric success")
        // Biometric success - will be handled by UI navigation
    }

    fun onBiometricError(message: String) {
        Log.e(TAG, "Biometric error: $message")
        // Handle biometric error if needed
    }

    fun onBiometricFailed() {
        Log.d(TAG, "Biometric failed")
    }

    fun resetState() {
        Log.d(TAG, "Resetting state")
        _loginState.value = LoginUiState.Idle
    }
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val user: FirebaseUser) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}