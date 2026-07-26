package com.example.smartcitizensystem.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
) {
    companion object {
        val Home = BottomNavItem("Home", Icons.Default.Home, "home_screen")
        val Election = BottomNavItem("Election", Icons.Default.HowToVote, "election_screen")
        val Emergency = BottomNavItem("Emergency", Icons.Default.Warning, "emergency_screen")
        val Profile = BottomNavItem("Profile", Icons.Default.Person, "profile_screen")
    }
}