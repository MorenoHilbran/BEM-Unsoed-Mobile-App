package com.example.bemunsoed.ui.notifications.model

data class UserPost(
    val id: Int,
    val userName: String,
    val userAvatar: Int,
    val content: String,
    val timeAgo: String,
    val likeCount: Int,
    val commentCount: Int,
    val isLiked: Boolean = false
)
