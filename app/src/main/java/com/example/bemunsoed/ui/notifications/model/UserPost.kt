package com.example.bemunsoed.ui.notifications.model

data class UserPost(
    val id: String = "",
    val content: String = "",
    val timestamp: String = "",
    val likeCount: Int = 0,
    val commentCount: Int = 0
)
