package com.example.bemunsoed.data.model

data class Post(
    val id: String = "",
    val authorName: String = "",
    val authorId: String = "",
    val content: String = "",
    val createdAt: String = "",
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val isLiked: Boolean = false,
    val imageUrls: List<String> = emptyList()
)
