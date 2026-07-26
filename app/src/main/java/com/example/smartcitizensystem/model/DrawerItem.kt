package com.example.smartcitizensystem.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class DrawerItem(
    val label: String,
    val icon: ImageVector,
    val route: String
) {
    companion object {
        val Profile = DrawerItem("Profile", Icons.Default.Person, "profile_screen")
        val Settings = DrawerItem("Settings", Icons.Default.Settings, "settings_screen")
        val About = DrawerItem("About", Icons.Default.Info, "about_screen")
        val Logout = DrawerItem("Logout", Icons.Default.Logout, "logout")
    }
}