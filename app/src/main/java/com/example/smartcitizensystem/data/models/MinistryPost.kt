package com.example.smartcitizensystem.data.models

data class MinistryFeedResponse(
    val success: Boolean,
    val count: Int,
    val data: List<MinistryPost>
)

data class MinistryPost(
    val post_id: String,
    val ministry_key: String,
    val author: MinistryAuthor,
    val created_at: String,
    val content: MinistryContent,
    val metrics: MinistryMetrics
)

data class MinistryAuthor(
    val name: String,
    val username: String,
    val avatar: String,
    val is_verified: Boolean
)

data class MinistryContent(
    val title: String,
    val body: String,
    val attachment_url: String
)

data class MinistryMetrics(
    val likes: Int,
    val shares: Int
)