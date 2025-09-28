package com.example.bemunsoed.data.model

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val date: String = "",
    val location: String = "",
    val isHighlight: Boolean = false,
    val registrationUrl: String = ""
)
