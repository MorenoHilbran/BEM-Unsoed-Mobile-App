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

class PostAdapter(
    private val onItemClick: (Post) -> Unit,
    private val onLikeClick: (Post) -> Unit,
    private val onCommentClick: (Post) -> Unit
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
        private val tvUserName: TextView = itemView.findViewById(R.id.tv_user_name)
        private val tvPostTime: TextView = itemView.findViewById(R.id.tv_post_time)
        private val tvPostContent: TextView = itemView.findViewById(R.id.tv_post_content)
        private val tvLikeCount: TextView = itemView.findViewById(R.id.tv_like_count)
        private val tvCommentCount: TextView = itemView.findViewById(R.id.tv_comment_count)
        private val btnLike: ImageView = itemView.findViewById(R.id.btn_like)
        private val btnComment: ImageView = itemView.findViewById(R.id.btn_comment)

        fun bind(post: Post) {
            tvUserName.text = post.authorName
            tvPostTime.text = post.createdAt
            tvPostContent.text = post.content
            tvLikeCount.text = "${post.likeCount} likes"
            tvCommentCount.text = "${post.commentCount} comments"

            // Set like button state
            btnLike.setImageResource(
                if (post.isLiked) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_outline
            )

            btnLike.setOnClickListener {
                onLikeClick(post)
            }

            btnComment.setOnClickListener {
                onCommentClick(post)
            }

            itemView.setOnClickListener {
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
