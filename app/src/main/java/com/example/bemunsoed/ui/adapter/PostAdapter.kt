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
import com.example.bemunsoed.data.model.Post
import com.example.bemunsoed.util.ProfileOptionsManager
import java.text.SimpleDateFormat
import java.util.*

class PostAdapter(
    private val onItemClick: (Post) -> Unit,
    private val onLikeClick: (Post) -> Unit = {},
    private val onDeleteClick: (Post) -> Unit = {},
    private val currentUserId: String? = null
) : ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivUserAvatar: ImageView = itemView.findViewById(R.id.iv_user_avatar)
        private val tvUserName: TextView = itemView.findViewById(R.id.tv_user_name)
        private val tvPostTime: TextView = itemView.findViewById(R.id.tv_post_time)
        private val tvPostContent: TextView = itemView.findViewById(R.id.tv_post_content)
        private val btnLike: ImageView = itemView.findViewById(R.id.btn_like)
        private val tvLikeCount: TextView = itemView.findViewById(R.id.tv_like_count)
        private val tvCommentCount: TextView = itemView.findViewById(R.id.tv_comment_count)
        private val layoutLike: View = itemView.findViewById(R.id.layout_like)
        private val layoutComment: View = itemView.findViewById(R.id.layout_comment)
        private val btnDelete: ImageView? = itemView.findViewById(R.id.btn_delete)

        fun bind(post: Post) {
            // Set user avatar from predefined options
            val profilePhotoRes = if (!post.authorProfilePhotoId.isNullOrEmpty()) {
                ProfileOptionsManager.getProfilePhotoDrawable(post.authorProfilePhotoId)
            } else {
                ProfileOptionsManager.getProfilePhotoDrawable("default")
            }
            ivUserAvatar.setImageResource(profilePhotoRes)

            // Set user info
            tvUserName.text = post.authorName.ifEmpty { "Anonymous User" }

            // Format timestamp
            val timeText = try {
                if (post.createdAt != 0L) {
                    val date = Date(post.createdAt)
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
                if (post.createdAt.toString().contains("-")) {
                    // Old string format
                    post.createdAt.toString()
                } else {
                    "Unknown time"
                }
            }
            tvPostTime.text = timeText

            // Set post content
            tvPostContent.text = post.content

            // Set like button state and count
            val likeCount = post.likeCount
            btnLike.setImageResource(
                if (post.isLiked) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_outline
            )
            tvLikeCount.text = when {
                likeCount == 0 -> "0"
                else -> "$likeCount"
            }

            // Set comment count
            val commentCount = post.commentCount
            tvCommentCount.text = when {
                commentCount == 0 -> "0"
                else -> "$commentCount"
            }

            // Show delete button only for post owner
            btnDelete?.visibility = if (post.authorId == currentUserId) View.VISIBLE else View.GONE
            btnDelete?.setOnClickListener {
                onDeleteClick(post)
            }

            // Set click listeners
            itemView.setOnClickListener {
                onItemClick(post)
            }

            layoutLike.setOnClickListener {
                onLikeClick(post)
            }

            layoutComment.setOnClickListener {
                onItemClick(post)
            }
        }
    }

    private class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}
