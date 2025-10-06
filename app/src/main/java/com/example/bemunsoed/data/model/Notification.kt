package com.example.bemunsoed.data.model

import com.google.firebase.firestore.PropertyName

data class Notification(
    val id: String = "",
    val type: NotificationType = NotificationType.LIKE,
    val postId: String = "",
    val postContent: String = "", // Preview konten post
    val actorId: String = "", // User yang melakukan action (like/comment)
    val actorName: String = "",
    val actorAvatar: String = "",
    val actorProfilePhotoId: String = "", // ID foto profil user seperti di Post
    val commentContent: String = "", // Jika type = COMMENT
    val recipientId: String = "", // Owner post yang menerima notifikasi
    val createdAt: Long = 0L,
    @get:PropertyName("read")
    @set:PropertyName("read")
    var isRead: Boolean = false
)

enum class NotificationType {
    LIKE,
    COMMENT,
    EVENT,
    ANNOUNCEMENT,
    SYSTEM
}
