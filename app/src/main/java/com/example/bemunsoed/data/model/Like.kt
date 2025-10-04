package com.example.bemunsoed.data.model

data class Like(
    val id: String = "",
    val userId: String = "",
    val postId: String = "",
    val commentId: String = "", // Empty if it's a post like, filled if it's a comment like
    val createdAt: Long = 0L
)
