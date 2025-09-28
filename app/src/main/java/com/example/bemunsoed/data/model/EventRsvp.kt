package com.example.bemunsoed.data.model

data class EventRsvp(
    val id: Int,
    val eventId: Int,
    val userId: String,
    val status: String, // "going", "interested", "not_going"
    val createdAt: String
)
