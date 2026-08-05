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
        val Home = BottomNavItem(
            label = "Home",
            icon = Icons.Default.Home,
            route = "home_screen"
        )
        val Election = BottomNavItem(
            label = "Election",
            icon = Icons.Default.HowToVote,
            route = "election_screen"
        )
        val Emergency = BottomNavItem(
            label = "Emergency",
            icon = Icons.Default.Warning,
            route = "emergency_screen"
        )
        val Profile = BottomNavItem(
            label = "Profile",
            icon = Icons.Default.Person,
            route = "profile_screen"
        )
    }
}