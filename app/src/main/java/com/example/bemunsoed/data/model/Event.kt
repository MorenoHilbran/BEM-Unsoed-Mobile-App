package com.example.bemunsoed.data.model

import com.google.firebase.firestore.PropertyName

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val date: String = "",
    val location: String = "",
    @get:PropertyName("isHighlight") @set:PropertyName("isHighlight")
    var isHighlight: Boolean = false,
    val registrationUrl: String = "",
    val status: String = "published", // "draft", "published", "archived"
    val createdAt: Long = System.currentTimeMillis()
)
