package com.example.bemunsoed.data.model

data class PostComment(
    val id: String = "",
    val postId: String = "",
    val content: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorAvatar: String = "",
    val authorProfilePhotoId: String = "default", // New field for profile photo sync
    val createdAt: Long = 0L,
    val likeCount: Int = 0,
    val likedBy: List<String> = emptyList(),
    val parentCommentId: String = "" // For nested replies
)
