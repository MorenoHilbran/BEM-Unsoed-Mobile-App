package com.example.bemunsoed.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bemunsoed.R
import com.example.bemunsoed.data.model.Comment
import com.example.bemunsoed.util.ProfileOptionsManager
import java.text.SimpleDateFormat
import java.util.*

class CommentAdapter(
    private val onLikeClick: (Comment) -> Unit,
    private val onDeleteClick: (Comment) -> Unit = {},
    private val currentUserId: String? = null
) : ListAdapter<Comment, CommentAdapter.CommentViewHolder>(CommentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivUserAvatar: ImageView = itemView.findViewById(R.id.iv_comment_user_avatar)
        private val tvUserName: TextView = itemView.findViewById(R.id.tv_comment_user_name)
        private val tvCommentTime: TextView = itemView.findViewById(R.id.tv_comment_time)
        private val tvCommentContent: TextView = itemView.findViewById(R.id.tv_comment_content)
        private val btnLike: ImageView = itemView.findViewById(R.id.btn_comment_like)
        private val tvLikeCount: TextView = itemView.findViewById(R.id.tv_comment_like_count)
        private val btnDelete: ImageView? = try {
            itemView.findViewById(R.id.btn_comment_delete)
        } catch (_: Exception) {
            null
        }

        fun bind(comment: Comment) {
            // Set profile photo from predefined options
            val profilePhotoRes = ProfileOptionsManager.getProfilePhotoDrawable(comment.authorProfilePhotoId)
            ivUserAvatar.setImageResource(profilePhotoRes)

            tvUserName.text = comment.authorName.ifEmpty { "Anonymous" }
            tvCommentContent.text = comment.content

            // Format time
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
            } catch (_: Exception) {
                "Unknown time"
            }
            tvCommentTime.text = timeText

            // Like button
            btnLike.setImageResource(
                if (comment.isLiked) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_outline
            )

            tvLikeCount.text = if (comment.likeCount > 0) "${comment.likeCount}" else ""

            btnLike.setOnClickListener {
                onLikeClick(comment)
            }

            // Show delete button only for comment owner
            btnDelete?.let { deleteBtn ->
                deleteBtn.visibility = if (comment.authorId == currentUserId) View.VISIBLE else View.GONE
                deleteBtn.setOnClickListener {
                    onDeleteClick(comment)
                }
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
