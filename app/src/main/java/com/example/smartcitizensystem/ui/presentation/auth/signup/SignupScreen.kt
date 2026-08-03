package com.example.smartcitizensystem.ui.presentation.auth.signup

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

private const val TAG = "SignupScreen"

@Composable
fun SignupScreen(
    viewModel: SignupViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},
    onSignupSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? AppCompatActivity

    val signupState = viewModel.signupState.value

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

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var nid by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var agreed by rememberSaveable { mutableStateOf(false) }
    var biometricResultMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isAuthenticating by rememberSaveable { mutableStateOf(false) }

    // Handle signup state changes
    LaunchedEffect(signupState) {
        try {
            when (signupState) {
                is SignupUiState.Success -> {
                    Log.d(TAG, "Signup successful, navigating to login")
                    onSignupSuccess()
                    viewModel.resetState()
                }
                is SignupUiState.Error -> {
                    Log.e(TAG, "Signup error: ${signupState.message}")
                }
                else -> Unit
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling signup state", e)
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
                        biometricResultMessage = "Biometric registration successful! ✅"
                        isAuthenticating = false
                    }
                    is BiometricResult.AuthenticationError -> {
                        biometricResultMessage = "Error: ${result.error}"
                        isAuthenticating = false
                    }
                    is BiometricResult.AuthenticationFailed -> {
                        biometricResultMessage = "Authentication failed. Please try again."
                        isAuthenticating = false
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
            .background(color = Color.White)
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
                text = "Citizen Signup",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Register using your national identity",
                fontSize = 15.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            InputField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                leadingIcon = Icons.Default.PermIdentity,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                value = email,
                onValueChange = { email = it },
                label = "Email Address",
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone Number",
                leadingIcon = Icons.Default.Phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                value = nid,
                onValueChange = { nid = it },
                label = "NID Number",
                leadingIcon = Icons.Default.Badge,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordInputField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                leadingIcon = Icons.Default.Password
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordInputField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                leadingIcon = Icons.Default.Password
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (signupState is SignupUiState.Error) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Text(
                        text = (signupState as SignupUiState.Error).message,
                        color = Color(0xFFC62828),
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { agreed = !agreed }
            ) {
                Checkbox(
                    checked = agreed,
                    onCheckedChange = { agreed = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF7D7DFF),
                        uncheckedColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "I confirm the information is accurate and agree to all terms.",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    try {
                        Log.d(TAG, "Create Account clicked for: $email")
                        viewModel.signupWithEmail(
                            name = name,
                            email = email,
                            phone = phone,
                            nid = nid,
                            password = password,
                            confirmPassword = confirmPassword
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Error during signup", e)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7D7DFF)
                ),
                enabled = agreed && signupState !is SignupUiState.Loading
            ) {
                if (signupState is SignupUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Create Account", fontSize = 16.sp)
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
                    if (agreed && !isAuthenticating && promptManager != null) {
                        isAuthenticating = true
                        biometricResultMessage = "Authenticating..."
                        try {
                            promptManager.showBiometricPrompt(
                                title = "Biometric Registration",
                                description = "Register your fingerprint for secure access"
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF7D7DFF)),
                enabled = agreed && !isAuthenticating && promptManager != null
            ) {
                if (isAuthenticating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFF7D7DFF),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        tint = Color(0xFF7D7DFF)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (isAuthenticating) "Registering..." else "Biometric Authentication",
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

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text("Already Registered? ")
                Text(
                    text = "Login",
                    color = Color(0xFF7D7DFF),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onLoginClick() }
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
private fun SignupScreenPreview() {
    SignupScreen()
}