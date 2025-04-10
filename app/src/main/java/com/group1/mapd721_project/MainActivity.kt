package com.group1.mapd721_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.group1.mapd721_project.ui.theme.MAPD721_ProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MAPD721_ProjectTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        /* Login Screen */
                        composable("login") {
                            LoginScreen(
                                onLoginClick = { email, password ->
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onSignUpClick = {
                                    navController.navigate("signup")
                                },
                                onForgotPasswordClick = { navController.navigate("forgot_password") }
                            )
                        }
                        /* Sign Up Screen */
                        composable("signup") {
                            SignUpScreen(
                                onSignUpClick = { email, password ->
                                    println("Register: $email")
                                    navController.popBackStack()
                                },
                                onLoginClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        /* Forgot Password Screen */
                        composable("forgot_password") {
                            ForgotPasswordScreen(
                                onBackToLogin = { navController.popBackStack() },
                                onSendResetEmail = { email ->
                                    println("Send reset email to $email")
                                }
                            )
                        }
                        /* Home Screen */
                        composable("home") {
                            HomeScreen(
                                onNavigate = { navController.navigate(it) },
                                currentRoute = "home"
                            )
                        }
                        /* Medication Screen */
                        composable("medication_list") {
                            MedicationListScreen(
                                onNavigate = { navController.navigate(it) },
                                onAddMedicationClick = { navController.navigate("add_medication") },
                                currentRoute = "medication_list"
                            )
                        }
                        composable("add_medication") {
                            AddMedicineScreen(

                            )
                        }
                        // Settings Screen
                        composable("settings") {
                            SettingsScreen(
                                onNavigate = { navController.navigate(it) },
                                onLogout = { navController.navigate("login") },
                                currentRoute = "settings"
                            )
                        }
                    }
                }
            }
        }
    }
}