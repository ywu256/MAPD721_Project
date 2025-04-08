package com.group1.mapd721_project

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    onBackToLogin: () -> Unit = {},
    onSendResetEmail: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesManager(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /* Logo */
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(180.dp)
                .padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Reset Password", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        /* Email */
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter your Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        /* New Password */
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Enter new Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val (storedEmail, _) = userPrefs.getUser()
                        if (email == storedEmail) {
                            userPrefs.updatePassword(password)

                            // Navigate back to Login screen after reset password successfully
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show()
                                onBackToLogin()
                            }
                        } else {
                            message = "Email not found or incorrect."
                        }
                    }
                } else {
                    message = "Please enter both Email and new Password."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset Password", style = MaterialTheme.typography.bodyLarge)
        }

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(message, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onBackToLogin) {
            Text("Back to Login", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordPreview() {
    ForgotPasswordScreen()
}