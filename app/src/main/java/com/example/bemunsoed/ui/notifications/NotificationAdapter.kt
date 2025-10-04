package com.example.bemunsoed.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bemunsoed.R
import com.example.bemunsoed.data.model.Notification
import com.example.bemunsoed.data.model.NotificationType
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(
    private val onItemClick: (Notification) -> Unit
) : ListAdapter<Notification, NotificationAdapter.NotificationViewHolder>(NotificationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val indicatorUnread: View = itemView.findViewById(R.id.indicator_unread)
        private val tvNotificationText: TextView = itemView.findViewById(R.id.tv_notification_text)
        private val tvPostPreview: TextView = itemView.findViewById(R.id.tv_post_preview)
        private val tvCommentContent: TextView = itemView.findViewById(R.id.tv_comment_content)
        private val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        private val ivTypeIcon: ImageView = itemView.findViewById(R.id.iv_type_icon)

        fun bind(notification: Notification) {
            // Hide unread indicator - removed the red dot but notification feature still works
            indicatorUnread.visibility = View.GONE
            // indicatorUnread.visibility = if (notification.isRead) View.INVISIBLE else View.VISIBLE

            // Set notification text based on type
            val notificationText = when (notification.type) {
                NotificationType.LIKE -> {
                    "${notification.actorName} liked your post"
                }
                NotificationType.COMMENT -> {
                    "${notification.actorName} commented on your post"
                }
                NotificationType.EVENT -> {
                    notification.actorName
                }
                NotificationType.ANNOUNCEMENT -> {
                    notification.actorName
                }
                NotificationType.SYSTEM -> {
                    notification.actorName
                }
            }
            tvNotificationText.text = notificationText

            // For comment notifications, show comment content instead of post preview
            if (notification.type == NotificationType.COMMENT && notification.commentContent.isNotEmpty()) {
                // Show comment content
                tvCommentContent.visibility = View.VISIBLE
                tvCommentContent.text = itemView.context.getString(R.string.comment_preview_format, notification.commentContent)
                // Hide post preview to avoid duplication
                tvPostPreview.visibility = View.GONE
            } else {
                // For other notifications (like LIKE), show post preview
                tvCommentContent.visibility = View.GONE
                if (notification.postContent.isNotEmpty()) {
                    tvPostPreview.visibility = View.VISIBLE
                    tvPostPreview.text = itemView.context.getString(R.string.post_preview_format, notification.postContent)
                } else {
                    tvPostPreview.visibility = View.GONE
                }
            }

            // Set type icon
            when (notification.type) {
                NotificationType.LIKE -> {
                    ivTypeIcon.setImageResource(R.drawable.ic_favorite_filled)
                    ivTypeIcon.setColorFilter(ContextCompat.getColor(itemView.context, android.R.color.holo_red_light))
                }
                NotificationType.COMMENT -> {
                    ivTypeIcon.setImageResource(R.drawable.ic_comment_outline)
                    ivTypeIcon.setColorFilter(ContextCompat.getColor(itemView.context, android.R.color.holo_blue_light))
                }
                else -> {
                    ivTypeIcon.setImageResource(R.drawable.ic_notification)
                    ivTypeIcon.clearColorFilter()
                }
            }

            // Format timestamp
            tvTime.text = formatTime(notification.createdAt)

            // Set click listener
            itemView.setOnClickListener {
                onItemClick(notification)
            }
        }

        private fun formatTime(timestamp: Long): String {
            return try {
                if (timestamp != 0L) {
                    val date = Date(timestamp)
                    val now = Date()
                    val diffInMillis = now.time - date.time
                    val diffInMinutes = diffInMillis / (1000 * 60)

                    when {
                        diffInMinutes < 1 -> "Just now"
                        diffInMinutes < 60 -> "${diffInMinutes}m ago"
                        diffInMinutes < 1440 -> "${diffInMinutes / 60}h ago"
                        diffInMinutes < 10080 -> "${diffInMinutes / 1440}d ago"
                        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
                    }
                } else {
                    "Unknown time"
                }
            } catch (_: Exception) {
                "Unknown time"
            }
        }
    }

    private class NotificationDiffCallback : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            // Use unique combination of fields to identify items
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            // Compare all relevant fields to detect content changes
            return oldItem.id == newItem.id &&
                    oldItem.type == newItem.type &&
                    oldItem.actorId == newItem.actorId &&
                    oldItem.postId == newItem.postId &&
                    oldItem.createdAt == newItem.createdAt &&
                    oldItem.isRead == newItem.isRead
        }
    }
}