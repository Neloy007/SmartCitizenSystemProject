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
import com.example.smartcitizensystem.ui.presentation.main.facescan.FaceVerificationScreen

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
        // Auth screens
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
                    Log.d(TAG, "Signup successful, navigating to Home")
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                }
            )
        }

        // ✅ Main screen with bottom navigation (wrapper).
        // NOTE: BottomNavItem.Home/Election/Emergency/Profile and the drawer routes
        // ("licences_screen", "settings_screen", "about_screen") are intentionally
        // NOT registered here anymore. They now live exclusively inside MainScreen's
        // own internal NavHost (see MainScreen.kt / bottomNavController). Registering
        // them here too was the bug: MainScreen's bottom bar navigated using the SAME
        // NavHostController as this outer graph, so tapping a tab matched this outer
        // composable() instead of the nested one, popping MainScreen (and its Scaffold/
        // bottom bar) off the screen entirely.
        composable(Screen.Home.route) {
            MainScreen(
                navController = navController,
                onLogout = onLogout
            )
        }

        composable(Screen.FaceScan.route) {
            FaceVerificationScreen(
                navController = navController
            )
        }
    }
}