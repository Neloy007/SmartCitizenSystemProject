package com.example.smartcitizensystem.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Welcome : Screen("welcome_screen")
    object Login : Screen("login_screen")
    object Signup : Screen("signup_screen")
    object Home : Screen("home_screen")
    object Profile : Screen("profile_screen")
    object Election : Screen("election_screen")
    object Emergency : Screen("emergency_screen")
    object Licences : Screen("licences_screen")
    object Settings : Screen("settings_screen")
    object About : Screen("about_screen")
}