package com.example.smartcitizensystem.ui.presentation.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.smartcitizensystem.data.models.CustomMinistryPostsData
import com.example.smartcitizensystem.model.BottomNavItem
import com.example.smartcitizensystem.ui.presentation.main.home.components.CustomMinistryPostCard
import com.example.smartcitizensystem.ui.presentation.main.home.components.QuickActionCard
import com.example.smartcitizensystem.ui.presentation.main.home.components.SocialPostCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController? = null
) {
    val socialPosts = viewModel.socialPosts.value
    val isLoading = viewModel.isLoading.value
    val error = viewModel.error.value

    val customPosts = remember { CustomMinistryPostsData.getPosts() }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val showFab by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchSocialFeed()
    }

    val scrollToTop: () -> Unit = {
        coroutineScope.launch {
            listState.animateScrollToItem(0)
        }
    }

    // Navigation functions
    val navigateToElection: () -> Unit = {
        navController?.navigate(BottomNavItem.Election.route) {
            launchSingleTop = true
        } ?: Unit
    }

    val navigateToEmergency: () -> Unit = {
        navController?.navigate(BottomNavItem.Emergency.route) {
            launchSingleTop = true
        } ?: Unit
    }

    val navigateToLicences: () -> Unit = {
        navController?.navigate("licences_screen") {
            launchSingleTop = true
        } ?: Unit
    }

    val navigateToProfile: () -> Unit = {
        navController?.navigate(BottomNavItem.Profile.route) {
            launchSingleTop = true
        } ?: Unit
    }

    // ✅ Removed Scaffold - just render content directly
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Welcome section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF7F7FF)
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Welcome back, Citizen!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Stay updated with ministry news & community posts",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Quick Actions Section Title
            item {
                Text(
                    text = "Quick Actions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Quick actions grid - Row 1
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    QuickActionCard(
                        title = "Vote",
                        icon = Icons.Default.HowToVote,
                        backgroundColor = Color(0xFFE8E8FF),
                        modifier = Modifier.weight(1f),
                        onClick = navigateToElection
                    )
                    QuickActionCard(
                        title = "Emergency",
                        icon = Icons.Default.Warning,
                        backgroundColor = Color(0xFFFFE8E8),
                        modifier = Modifier.weight(1f),
                        onClick = navigateToEmergency
                    )
                }
            }

            // Quick actions grid - Row 2
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    QuickActionCard(
                        title = "Licences",
                        icon = Icons.Default.DocumentScanner,
                        backgroundColor = Color(0xFFE8FFE8),
                        modifier = Modifier.weight(1f),
                        onClick = navigateToLicences
                    )
                    QuickActionCard(
                        title = "Profile",
                        icon = Icons.Default.Person,
                        backgroundColor = Color(0xFFFFF0E8),
                        modifier = Modifier.weight(1f),
                        onClick = navigateToProfile
                    )
                }
            }

            // Custom Ministry Posts Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🏛️ Ministry Updates",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "🇧🇩 Official",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // Custom ministry posts
            items(customPosts) { post ->
                CustomMinistryPostCard(post = post)
            }

            // Divider
            item {
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color.LightGray,
                    thickness = 1.dp
                )
            }

            // JSONPlaceholder Feed Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📱 Community Feed",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "🌐 From JSONPlaceholder",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // Loading State
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF7D7DFF)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Loading community posts...",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Error State
            error?.let { errorMessage ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFE8E8)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = "Error",
                                tint = Color.Red
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // JSONPlaceholder Posts
            if (!isLoading && socialPosts.isNotEmpty()) {
                items(socialPosts) { post ->
                    SocialPostCard(post = post)
                }
            }

            // Empty state
            if (!isLoading && socialPosts.isEmpty() && error == null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "No data",
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No community posts available",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Bottom spacer
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // ✅ Floating Action Button inside Box
        if (showFab) {
            FloatingActionButton(
                onClick = scrollToTop,
                containerColor = Color(0xFF7D7DFF),
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowUp,
                    contentDescription = "Scroll to top",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}