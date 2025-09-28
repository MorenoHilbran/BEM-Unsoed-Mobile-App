package com.example.bemunsoed.data.model

data class PostComment(
    val id: Int,
    val postId: String,
    val content: String,
    val user: User,
    val createdAt: String
)
