package com.example.smartcitizensystem.ui.presentation.main.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smartcitizensystem.data.models.SocialPost

@Composable
fun SocialPostCard(
    post: SocialPost,
    modifier: Modifier = Modifier
) {
    var isLiked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableStateOf((post.id * 7) % 100) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // User Info Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // User Avatar
                AsyncImage(
                    model = post.userAvatar ?: "https://i.pravatar.cc/150?img=${post.userId}",
                    contentDescription = "User avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = null, // Optional: add placeholder
                    error = null // Optional: add error image
                )

                Spacer(modifier = Modifier.width(12.dp))

                // User Name and Time
                Column {
                    Text(
                        text = post.userName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "2 hours ago",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Post Title
            Text(
                text = post.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Post Body
            Text(
                text = post.body,
                fontSize = 14.sp,
                color = Color.DarkGray,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Post Image
            post.imageUrl?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop,
                    placeholder = null,
                    error = null
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons (Like, Comment, Share)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Like Button
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            isLiked = !isLiked
                            if (isLiked) likeCount++ else likeCount--
                        }
                    ) {
                        Icon(
                            if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) Color.Red else Color.Gray
                        )
                    }
                    Text(
                        text = likeCount.toString(),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Comment Button
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* Handle comment */ }) {
                        Icon(
                            Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comment",
                            tint = Color.Gray
                        )
                    }
                    Text(
                        text = "${(post.id * 3) % 50}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Share Button
                IconButton(onClick = { /* Handle share */ }) {
                    Icon(
                        Icons.Filled.Share,
                        contentDescription = "Share",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}