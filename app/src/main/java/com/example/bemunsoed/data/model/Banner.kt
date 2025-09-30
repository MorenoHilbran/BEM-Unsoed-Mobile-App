package com.example.bemunsoed.data.model

data class Banner(
    val id: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val linkUrl: String = "",
    val isActive: Boolean = true,
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
