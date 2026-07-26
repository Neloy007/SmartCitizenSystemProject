package com.example.smartcitizensystem.ui.presentation.auth.signup

import androidx.appcompat.app.AppCompatActivity
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
import com.example.smartcitizensystem.ui.presentation.auth.biometric.BiometricPromptManager
import com.example.smartcitizensystem.ui.presentation.auth.components.InputField

@Composable
fun SignupScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},
    onFingerprintClick: () -> Unit = {},
    onSignupClick: () -> Unit = {},
    onForgotClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? AppCompatActivity  // Changed to AppCompatActivity
    val promptManager = remember { activity?.let { BiometricPromptManager(it) } }

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

            var name by rememberSaveable { mutableStateOf("") }
            InputField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                leadingIcon = Icons.Default.PermIdentity,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(modifier = Modifier.height(16.dp))

            var email by rememberSaveable { mutableStateOf("") }
            InputField(
                value = email,
                onValueChange = { email = it },
                label = "Enter your email",
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(modifier = Modifier.height(16.dp))

            var phone by rememberSaveable { mutableStateOf("") }
            InputField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone Number",
                leadingIcon = Icons.Default.Phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            var nid by rememberSaveable { mutableStateOf("") }
            InputField(
                value = nid,
                onValueChange = { nid = it },
                label = "NID Number",
                leadingIcon = Icons.Default.Badge,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(24.dp))

            var agreed by rememberSaveable { mutableStateOf(false) }

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

            OutlinedButton(
                onClick = {
                    if (agreed) {
                        promptManager?.showBiometricPrompt(
                            title = "Biometric Registration",
                            description = "Register your fingerprint for secure access"
                        )
                        onFingerprintClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF7D7DFF)),
                enabled = agreed
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    tint = Color(0xFF7D7DFF)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Biometric Authentication", color = Color(0xFF7D7DFF))
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