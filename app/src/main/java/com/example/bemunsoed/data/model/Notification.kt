package com.example.bemunsoed.data.model

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: String,
    val isRead: Boolean = false,
    val actionUrl: String? = null
)

enum class NotificationType {
    LIKE,
    COMMENT,
    EVENT,
    ANNOUNCEMENT,
    SYSTEM
}
