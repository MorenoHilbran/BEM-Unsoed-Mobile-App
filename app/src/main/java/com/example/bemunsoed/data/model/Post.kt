package com.example.bemunsoed.data.model

import com.google.firebase.firestore.PropertyName

data class Post(
    val id: String = "",
    val authorName: String = "",
    val authorId: String = "",
    val authorProfilePhotoId: String = "default", // New field for syncing profile photos
    val content: String = "",
    val createdAt: Long = 0L,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    @get:PropertyName("isLiked")
    @set:PropertyName("isLiked")
    var isLiked: Boolean = false,
    val imageUrls: List<String> = emptyList(),
    val likedBy: List<String> = emptyList(), // List of user IDs who liked this post
    val tags: List<String> = emptyList(), // For hashtags support
    val category: String = "general" // Post category
)
