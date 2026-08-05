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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smartcitizensystem.model.BottomNavItem
import com.example.smartcitizensystem.model.DrawerItem
import com.example.smartcitizensystem.ui.presentation.main.bottomnav.GlassBottomNavigation
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
    navController: NavHostController, // outer controller: only used for app-level actions (logout)
    onLogout: () -> Unit = {}
) {
    // Separate controller for everything INSIDE MainScreen (bottom tabs + drawer routes).
    // This must never be the same instance as the outer `navController`, or navigating a
    // tab will also navigate the outer NavHost and pop MainScreen off the back stack.
    val bottomNavController = rememberNavController()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Election,
        BottomNavItem.Emergency,
        BottomNavItem.Profile
    )

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    //  Drawer (Hamburger menu)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                navController = bottomNavController,
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
        //  Scaffold with TopBar and BottomBar
        Scaffold(
            containerColor = Color.White, // prevents the area behind/around bottomBar from
            // falling back to MaterialTheme.colorScheme.background, which is dark in your
            // app's theme — that's what was showing through the transparent padding around
            // GlassBottomNavigation and making it look black.
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = getTitleForRoute(currentRoute),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    //  Navigation Icon (Hamburger Menu)
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
            //  Bottom Navigation Bar (custom glass style)
            bottomBar = {
                GlassBottomNavigation(
                    navController = bottomNavController,
                    items = bottomNavItems
                )
            }
        ) { paddingValues ->
            // Content area - NavHost for bottom navigation, driven by bottomNavController
            NavHost(
                navController = bottomNavController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                composable(BottomNavItem.Home.route) {
                    // pass the INNER controller so quick actions switch tabs correctly
                    HomeScreen(navController = bottomNavController)
                }
                composable(BottomNavItem.Election.route) {
                    ElectionScreen()
                }
                composable(BottomNavItem.Emergency.route) {
                    EmergencyScreen()
                }
                composable(BottomNavItem.Profile.route) {
                    ProfileScreen()
                }
                composable("licences_screen") {
                    LicencesScreen()
                }
                composable("settings_screen") {
                    SettingsScreen()
                }
                composable("about_screen") {
                    AboutScreen()
                }
            }
        }
    }
}

//  Helper function for dynamic title
fun getTitleForRoute(route: String?): String {
    return when (route) {
        "home_screen" -> "Smart Citizen System"
        "election_screen" -> "Election"
        "emergency_screen" -> "Emergency"
        "profile_screen" -> "Profile"
        "licences_screen" -> "Licences"
        "settings_screen" -> "Settings"
        "about_screen" -> "About"
        else -> "Smart Citizen System"
    }
}

//  Drawer Content
@Composable
fun DrawerContent(
    navController: NavHostController, // expects the INNER (bottomNavController) instance
    onCloseDrawer: () -> Unit,
    onLogout: () -> Unit
) {
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