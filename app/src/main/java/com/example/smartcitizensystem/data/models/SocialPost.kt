package com.example.smartcitizensystem.data.models

data class SocialPost(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
    val imageUrl: String? = null,
    val thumbnailUrl: String? = null,
    val userName: String = "User $userId",
    val userAvatar: String? = null
)

data class PostWithImage(
    val post: Post,
    val photo: Photo?
)
