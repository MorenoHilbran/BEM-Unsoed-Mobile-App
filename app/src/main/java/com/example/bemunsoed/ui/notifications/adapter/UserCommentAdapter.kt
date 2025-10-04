package com.example.bemunsoed.ui.notifications.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bemunsoed.R
import com.example.bemunsoed.data.model.Comment
import java.text.SimpleDateFormat
import java.util.*

class UserCommentAdapter(
    private val onItemClick: (Comment) -> Unit
) : ListAdapter<Comment, UserCommentAdapter.CommentViewHolder>(CommentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_comment_card, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPostTitle: TextView = itemView.findViewById(R.id.tv_post_title)
        private val tvCommentTime: TextView = itemView.findViewById(R.id.tv_comment_time)
        private val tvCommentContent: TextView = itemView.findViewById(R.id.tv_comment_content)
        private val tvLikeCount: TextView = itemView.findViewById(R.id.tv_like_count)

        fun bind(comment: Comment) {
            tvPostTitle.text = "On: Post"
            tvCommentContent.text = comment.content
            tvLikeCount.text = comment.likeCount.toString()

            // Format timestamp
            val timeText = try {
                if (comment.createdAt != 0L) {
                    val date = Date(comment.createdAt)
                    val now = Date()
                    val diffInMillis = now.time - date.time
                    val diffInMinutes = diffInMillis / (1000 * 60)

                    when {
                        diffInMinutes < 1 -> "Just now"
                        diffInMinutes < 60 -> "${diffInMinutes}m ago"
                        diffInMinutes < 1440 -> "${diffInMinutes / 60}h ago"
                        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
                    }
                } else {
                    "Unknown time"
                }
            } catch (e: Exception) {
                "Unknown time"
            }
            tvCommentTime.text = timeText

            itemView.setOnClickListener {
                onItemClick(comment)
            }
        }
    }

    private class CommentDiffCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }
    }
}
