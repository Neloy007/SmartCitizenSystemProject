package com.example.smartcitizensystem.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.smartcitizensystem.ui.presentation.auth.login.LoginScreen
import com.example.smartcitizensystem.ui.presentation.auth.signup.SignupScreen
import com.example.smartcitizensystem.ui.presentation.main.MainScreen
import com.example.smartcitizensystem.ui.presentation.splash.SplashScreen
import com.example.smartcitizensystem.ui.presentation.welcome.WelcomeScreen
import android.util.Log

private const val TAG = "SetupNavGraph"

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    onLogout: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onLoginClick = {
                    Log.d(TAG, "Navigating to Login")
                    navController.navigate(Screen.Login.route)
                },
                onSignupClick = {
                    Log.d(TAG, "Navigating to Signup")
                    navController.navigate(Screen.Signup.route)
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    Log.d(TAG, "Login successful, navigating to Home")
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSignupClick = {
                    Log.d(TAG, "Navigating to Signup from Login")
                    navController.navigate(Screen.Signup.route)
                }
            )
        }

        composable(Screen.Signup.route) {
            SignupScreen(
                onLoginClick = {
                    Log.d(TAG, "Navigating to Login from Signup")
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                },
                onSignupSuccess = {
                    Log.d(TAG, "Signup successful, navigating to Login")
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            MainScreen(
                navController = navController,
                onLogout = onLogout
            )
        }
    }
}