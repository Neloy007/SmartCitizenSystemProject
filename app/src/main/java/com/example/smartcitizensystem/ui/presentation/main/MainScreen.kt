package com.example.smartcitizensystem.ui.presentation.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.smartcitizensystem.model.BottomNavItem
import com.example.smartcitizensystem.model.DrawerItem
import com.example.smartcitizensystem.ui.presentation.main.drawer.AboutScreen
import com.example.smartcitizensystem.ui.presentation.main.drawer.SettingsScreen
import com.example.smartcitizensystem.ui.presentation.main.election.ElectionScreen
import com.example.smartcitizensystem.ui.presentation.main.emergency.EmergencyScreen
import com.example.smartcitizensystem.ui.presentation.main.home.HomeScreen
import com.example.smartcitizensystem.ui.presentation.main.licences.LicencesScreen
import com.example.smartcitizensystem.ui.presentation.main.profile.ProfileScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    onLogout: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Election,
        BottomNavItem.Emergency,
        BottomNavItem.Profile
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                navController = navController,
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                },
                onLogout = {
                    scope.launch { drawerState.close() }
                    onLogout()
                }
            )
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Smart Citizen System",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Open Drawer"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Navigate to notifications */ }) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black,
                        actionIconContentColor = Color(0xFF7D7DFF),
                        navigationIconContentColor = Color(0xFF7D7DFF)
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    item.icon,
                                    contentDescription = item.label,
                                    tint = if (currentRoute == item.route) Color(0xFF7D7DFF) else Color.Gray
                                )
                            },
                            label = {
                                Text(
                                    item.label,
                                    color = if (currentRoute == item.route) Color(0xFF7D7DFF) else Color.Gray
                                )
                            },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF7D7DFF),
                                unselectedIconColor = Color.Gray,
                                selectedTextColor = Color(0xFF7D7DFF),
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            // Render content based on current route
            val currentRoute = navController.currentBackStackEntry?.destination?.route

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                when (currentRoute) {
                    BottomNavItem.Home.route -> HomeScreen()
                    BottomNavItem.Election.route -> ElectionScreen()
                    BottomNavItem.Emergency.route -> EmergencyScreen()
                    BottomNavItem.Profile.route -> ProfileScreen()
                    "licences_screen" -> LicencesScreen()
                    "settings_screen" -> SettingsScreen()
                    "about_screen" -> AboutScreen()
                    else -> HomeScreen()
                }
            }
        }
    }
}

@Composable
fun DrawerContent(
    navController: NavHostController,
    onCloseDrawer: () -> Unit,
    onLogout: () -> Unit
) {
    // Use the DrawerItem from your model
    val drawerItems = listOf(
        DrawerItem.Profile,
        DrawerItem.Settings,
        DrawerItem.About,
        DrawerItem.Logout
    )

    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        drawerShape = MaterialTheme.shapes.medium
    ) {
        // Drawer Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(bottom = 16.dp)
            ) {
                Column {
                    Text(
                        text = "Smart Citizen",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7D7DFF)
                    )
                    Text(
                        text = "citizen@example.com",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Divider(color = Color.LightGray)
        }

        // Drawer Items
        drawerItems.forEach { item ->
            NavigationDrawerItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        tint = if (item.label == "Logout") Color(0xFFFF4444) else Color(0xFF7D7DFF)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (item.label == "Logout") Color(0xFFFF4444) else Color.Black
                    )
                },
                selected = false,
                onClick = {
                    when (item.route) {
                        "logout" -> {
                            onLogout()
                        }
                        else -> {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                            }
                            onCloseDrawer()
                        }
                    }
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = Color(0xFFF7F7FF),
                    unselectedContainerColor = Color.Transparent
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // Footer
        Spacer(modifier = Modifier.weight(1f))
        Divider(color = Color.LightGray)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Version 1.0.0",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = "© 2025 Government Authority",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}