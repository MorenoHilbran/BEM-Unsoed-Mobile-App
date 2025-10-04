package com.example.bemunsoed.data.model

data class Comment(
    val id: String = "",
    val postId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorUsername: String = "",
    val authorProfilePhotoId: String = "default", // New field for profile photo sync
    val content: String = "",
    val createdAt: Long = 0L,
    val likeCount: Int = 0,
    val isLiked: Boolean = false,
    val likedBy: List<String> = emptyList()
)
