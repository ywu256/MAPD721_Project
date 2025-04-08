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
                                    println("Login: $email")
                                },
                                onSignUpClick = {
                                    navController.navigate("signup")
                                }
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
                    }
                }
            }
        }
    }
}