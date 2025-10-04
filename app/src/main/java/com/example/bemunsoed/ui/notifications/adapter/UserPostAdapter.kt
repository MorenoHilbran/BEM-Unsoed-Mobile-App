package com.example.bemunsoed.ui.notifications.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bemunsoed.R
import com.example.bemunsoed.ui.notifications.model.UserPost
import java.text.SimpleDateFormat
import java.util.*

class UserPostAdapter(
    private val onPostClick: (UserPost) -> Unit
) : RecyclerView.Adapter<UserPostAdapter.UserPostViewHolder>() {

    private var posts = listOf<UserPost>()

    fun updatePosts(newPosts: List<UserPost>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_post, parent, false)
        return UserPostViewHolder(view, onPostClick)
    }

    override fun onBindViewHolder(holder: UserPostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    class UserPostViewHolder(
        itemView: View,
        private val onPostClick: (UserPost) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvContent: TextView = itemView.findViewById(R.id.tv_content)
        private val tvTimestamp: TextView = itemView.findViewById(R.id.tv_timestamp)
        private val tvLikeCount: TextView = itemView.findViewById(R.id.tv_like_count)
        private val tvCommentCount: TextView = itemView.findViewById(R.id.tv_comment_count)

        fun bind(post: UserPost) {
            tvContent.text = post.content
            tvTimestamp.text = formatTimestamp(post.timestamp)
            tvLikeCount.text = "${post.likeCount} likes"
            tvCommentCount.text = "${post.commentCount} comments"

            itemView.setOnClickListener {
                onPostClick(post)
            }
        }

        private fun formatTimestamp(timestamp: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
                val date = inputFormat.parse(timestamp)
                date?.let { outputFormat.format(it) } ?: timestamp
            } catch (e: Exception) {
                timestamp
            }
        }
    }
}
