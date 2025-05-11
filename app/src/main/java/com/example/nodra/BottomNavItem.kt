package com.example.nodra

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.Accessibility
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Video : BottomNavItem("video", "Video", Icons.Default.PlayArrow)
    object Accessibility : BottomNavItem("accessibility", "Accessibility", Icons.Rounded.Accessibility)
    object Notifications : BottomNavItem("notifications", "Notification", Icons.Default.Notifications)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}