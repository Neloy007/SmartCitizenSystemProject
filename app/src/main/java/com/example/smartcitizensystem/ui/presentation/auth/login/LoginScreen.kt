package com.example.smartcitizensystem.ui.presentation.auth.login

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartcitizensystem.ui.presentation.auth.biometric.BiometricPromptManager
import com.example.smartcitizensystem.ui.presentation.auth.components.InputField
import com.example.smartcitizensystem.ui.presentation.auth.components.PasswordInputField
import com.example.smartcitizensystem.ui.presentation.auth.dao.BiometricResult
import kotlinx.coroutines.flow.collectLatest
import android.util.Log

private const val TAG = "LoginScreen"

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},
    onSignupClick: () -> Unit = {},
    onForgotClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? AppCompatActivity
    val loginState = viewModel.loginState.value

    val promptManager = remember {
        try {
            activity?.let { BiometricPromptManager(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create BiometricPromptManager", e)
            null
        }
    }

    val enrollLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { /* handle enroll result if needed */ }

    var identity by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var biometricResultMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isAuthenticating by rememberSaveable { mutableStateOf(false) }
    var hasNavigated by rememberSaveable { mutableStateOf(false) }

    // Handle login state changes - with safe navigation
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginUiState.Success -> {
                Log.d(TAG, "Login successful, navigating to home")
                if (!hasNavigated) {
                    hasNavigated = true
                    onLoginClick()
                    viewModel.resetState()
                }
            }
            is LoginUiState.Error -> {
                Log.e(TAG, "Login error: ${loginState.message}")
                // Error is displayed in UI
            }
            else -> Unit
        }
    }

    // Reset navigation flag when state changes to Idle
    LaunchedEffect(loginState) {
        if (loginState is LoginUiState.Idle) {
            hasNavigated = false
        }
    }

    // Collect biometric results
    LaunchedEffect(promptManager) {
        try {
            promptManager?.promptResults?.collectLatest { result ->
                when (result) {
                    is BiometricResult.AuthenticationNotSet -> {
                        biometricResultMessage = "Authentication not set. Please set up biometric."
                        isAuthenticating = false
                        if (Build.VERSION.SDK_INT >= 30) {
                            try {
                                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                    putExtra(
                                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                                    )
                                }
                                enrollLauncher.launch(enrollIntent)
                            } catch (e: Exception) {
                                biometricResultMessage = "Cannot open biometric settings"
                            }
                        }
                    }
                    is BiometricResult.AuthenticationSuccess -> {
                        biometricResultMessage = "Authentication successful! ✅"
                        isAuthenticating = false
                        viewModel.onBiometricSuccess()
                        if (!hasNavigated) {
                            hasNavigated = true
                            onLoginClick()
                        }
                    }
                    is BiometricResult.AuthenticationError -> {
                        biometricResultMessage = "Error: ${result.error}"
                        isAuthenticating = false
                        viewModel.onBiometricError(result.error)
                    }
                    is BiometricResult.AuthenticationFailed -> {
                        biometricResultMessage = "Authentication failed. Please try again."
                        isAuthenticating = false
                        viewModel.onBiometricFailed()
                    }
                    is BiometricResult.FeatureUnavailable -> {
                        biometricResultMessage = "Biometric feature unavailable on this device"
                        isAuthenticating = false
                    }
                    is BiometricResult.HardwareUnavailable -> {
                        biometricResultMessage = "Biometric hardware unavailable"
                        isAuthenticating = false
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error collecting biometric results", e)
            biometricResultMessage = "Error: ${e.message}"
            isAuthenticating = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Citizen Login",
                fontSize = 28.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Verify your identity to continue",
                fontSize = 15.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            InputField(
                value = identity,
                onValueChange = { identity = it },
                label = "Email",
                leadingIcon = Icons.Filled.Email,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Email
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            PasswordInputField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                leadingIcon = Icons.Filled.Password
            )
            Spacer(modifier = Modifier.height(28.dp))

            if (loginState is LoginUiState.Error) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Text(
                        text = (loginState as LoginUiState.Error).message,
                        color = Color(0xFFC62828),
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    Log.d(TAG, "Login button clicked with email: $identity")
                    viewModel.loginWithEmail(identity, password)
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7D7DFF)),
                enabled = loginState !is LoginUiState.Loading
            ) {
                if (loginState is LoginUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Login Securely", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(modifier = Modifier.weight(1f))
                Text("  OR  ", color = Color.Gray)
                Divider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = {
                    if (!isAuthenticating && promptManager != null) {
                        isAuthenticating = true
                        biometricResultMessage = "Authenticating..."
                        try {
                            promptManager.showBiometricPrompt(
                                title = "Device Authentication",
                                description = "Authenticate securely to login"
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Biometric error", e)
                            biometricResultMessage = "Error: ${e.message}"
                            isAuthenticating = false
                        }
                    } else if (promptManager == null) {
                        biometricResultMessage = "Biometric not available"
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF7D7DFF)),
                enabled = !isAuthenticating && promptManager != null
            ) {
                if (isAuthenticating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFF7D7DFF),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.Filled.Fingerprint,
                        contentDescription = null,
                        tint = Color(0xFF7D7DFF)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (isAuthenticating) "Authenticating..." else "Login with Fingerprint",
                    color = Color(0xFF7D7DFF)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            biometricResultMessage?.let { message ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            message.contains("successful") -> Color(0xFFE8F5E9)
                            message.contains("Error") || message.contains("failed") -> Color(0xFFFFEBEE)
                            else -> Color(0xFFF5F5F5)
                        }
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = message,
                        color = when {
                            message.contains("successful") -> Color(0xFF2E7D32)
                            message.contains("Error") || message.contains("failed") -> Color(0xFFC62828)
                            else -> Color.Black
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Forgot password?",
                color = Color(0xFF7D7DFF),
                modifier = Modifier.clickable { onForgotClick() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                Text("New citizen? ")
                Text(
                    text = "Sign Up",
                    color = Color(0xFF7D7DFF),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                    modifier = Modifier.clickable { onSignupClick() }
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "End-to-end encrypted by Mandroid",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen()
}