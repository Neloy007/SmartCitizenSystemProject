package com.example.smartcitizensystem.ui.presentation.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.smartcitizensystem.ui.presentation.main.home.components.QuickActionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val posts = viewModel.posts.value
    val isLoading = viewModel.isLoading.value
    val error = viewModel.error.value
    var showTodos by remember { mutableStateOf(false) }

    // Fetch data when screen loads or toggle changes
    LaunchedEffect(showTodos) {
        if (showTodos) {
            viewModel.fetchTodos()
        } else {
            viewModel.fetchPosts()
        }
    }

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
                actions = {
                    // Toggle between Posts and Todos
                    IconButton(onClick = {
                        showTodos = !showTodos
                    }) {
                        Icon(
                            if (showTodos) Icons.Default.List else Icons.Default.Task,
                            contentDescription = if (showTodos) "Show Posts" else "Show Todos",
                            tint = if (showTodos) Color(0xFF7D7DFF) else Color.Gray
                        )
                    }
                    // Refresh button
                    IconButton(onClick = {
                        if (showTodos) {
                            viewModel.fetchTodos()
                        } else {
                            viewModel.fetchPosts()
                        }
                    }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color(0xFF7D7DFF)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .background(Color.White)
        ) {
            // Welcome section
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
                        text = "Your secure governance platform",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick actions grid
            Text(
                text = "Quick Actions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Row 1
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    QuickActionCard(
                        title = "Vote",
                        icon = Icons.Default.HowToVote,
                        backgroundColor = Color(0xFFE8E8FF),
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = "Emergency",
                        icon = Icons.Default.Warning,
                        backgroundColor = Color(0xFFFFE8E8),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    QuickActionCard(
                        title = "Licences",
                        icon = Icons.Default.DocumentScanner,
                        backgroundColor = Color(0xFFE8FFE8),
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = "Profile",
                        icon = Icons.Default.Person,
                        backgroundColor = Color(0xFFFFF0E8),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // API Data Section with Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showTodos) "Todos from JSONPlaceholder" else "Posts from JSONPlaceholder",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Toggle Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Posts",
                        fontSize = 12.sp,
                        color = if (!showTodos) Color(0xFF7D7DFF) else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = showTodos,
                        onCheckedChange = { showTodos = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF7D7DFF),
                            checkedTrackColor = Color(0xFF7D7DFF).copy(alpha = 0.5f),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Todos",
                        fontSize = 12.sp,
                        color = if (showTodos) Color(0xFF7D7DFF) else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Loading State
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF7D7DFF)
                    )
                }
            }

            // Error State
            error?.let { errorMessage ->
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

            // Data Display
            if (!isLoading && posts.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    items(posts) { post ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF7F7FF)
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 1.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (showTodos) "Todo #${post.id}" else "Post #${post.id}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF7D7DFF)
                                    )
                                    Text(
                                        text = "User ${post.userId}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = post.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                if (showTodos) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            if (post.body.contains("Completed")) Icons.Default.CheckCircle else Icons.Default.Pending,
                                            contentDescription = null,
                                            tint = if (post.body.contains("Completed")) Color.Green else Color.Blue,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = post.body,
                                            fontSize = 12.sp,
                                            color = if (post.body.contains("Completed")) Color.Green else Color.Blue,
                                            maxLines = 1
                                        )
                                    }
                                } else {
                                    Text(
                                        text = post.body,
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (!isLoading && posts.isEmpty() && error == null) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
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
                            text = if (showTodos) "No todos available" else "No posts available",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}