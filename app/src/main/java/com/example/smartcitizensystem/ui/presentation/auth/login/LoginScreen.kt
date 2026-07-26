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
import com.example.smartcitizensystem.ui.presentation.auth.biometric.BiometricPromptManager
import com.example.smartcitizensystem.ui.presentation.auth.components.InputField
import com.example.smartcitizensystem.ui.presentation.auth.components.PasswordInputField
import com.example.smartcitizensystem.ui.presentation.auth.dao.BiometricResult
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},
    onSignupClick: () -> Unit = {},
    onForgotClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? AppCompatActivity
    val promptManager = remember { activity?.let { BiometricPromptManager(it) } }

    val enrollLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { /* handle enroll result if needed */ }

    var identity by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var biometricResultMessage by rememberSaveable { mutableStateOf<String?>(null) }

    // Collect biometric results
    LaunchedEffect(promptManager) {
        promptManager?.promptResults?.collectLatest { result ->
            when (result) {
                is BiometricResult.AuthenticationNotSet -> {
                    biometricResultMessage = "Authentication not set"
                    if (Build.VERSION.SDK_INT >= 30) {
                        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                            putExtra(
                                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                            )
                        }
                        enrollLauncher.launch(enrollIntent)
                    }
                }
                is BiometricResult.AuthenticationSuccess -> {
                    biometricResultMessage = "Authentication successful"
                    onLoginClick()
                }
                is BiometricResult.AuthenticationError -> {
                    biometricResultMessage = "Error: ${result.error}"
                }
                is BiometricResult.AuthenticationFailed -> {
                    biometricResultMessage = "Authentication failed"
                }
                is BiometricResult.FeatureUnavailable -> {
                    biometricResultMessage = "Feature unavailable"
                }
                is BiometricResult.HardwareUnavailable -> {
                    biometricResultMessage = "Hardware unavailable"
                }
            }
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
                label = "Email or Phone",
                leadingIcon = Icons.Filled.Email,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Text
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

            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7D7DFF))
            ) {
                Text("Login Securely", fontSize = 16.sp)
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
                    promptManager?.showBiometricPrompt(
                        title = "Device Authentication",
                        description = "Authenticate securely to login"
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF7D7DFF))
            ) {
                Icon(
                    Icons.Filled.Fingerprint,
                    contentDescription = null,
                    tint = Color(0xFF7D7DFF)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Login with Fingerprint", color = Color(0xFF7D7DFF))
            }

            Spacer(modifier = Modifier.height(12.dp))

            biometricResultMessage?.let { message ->
                Text(
                    text = message,
                    color = if (message.contains("successful")) Color.Green else Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Forgot credentials?",
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