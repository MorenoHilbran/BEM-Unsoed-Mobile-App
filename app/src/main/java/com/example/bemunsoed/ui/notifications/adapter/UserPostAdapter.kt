package com.example.bemunsoed.ui.notifications.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bemunsoed.R
import com.example.bemunsoed.ui.notifications.model.UserPost

class UserPostAdapter(private val postsList: List<UserPost>) :
    RecyclerView.Adapter<UserPostAdapter.UserPostViewHolder>() {

    class UserPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivUserAvatar: ImageView = itemView.findViewById(R.id.iv_post_user_avatar)
        val tvUserName: TextView = itemView.findViewById(R.id.tv_post_user_name)
        val tvPostTime: TextView = itemView.findViewById(R.id.tv_post_time)
        val tvPostContent: TextView = itemView.findViewById(R.id.tv_post_content)
        val llLikeButton: LinearLayout = itemView.findViewById(R.id.ll_like_button)
        val tvLikeCount: TextView = itemView.findViewById(R.id.tv_like_count)
        val llCommentButton: LinearLayout = itemView.findViewById(R.id.ll_comment_button)
        val tvCommentCount: TextView = itemView.findViewById(R.id.tv_comment_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_post_card, parent, false)
        return UserPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserPostViewHolder, position: Int) {
        val post = postsList[position]

        holder.ivUserAvatar.setImageResource(post.userAvatar)
        holder.tvUserName.text = post.userName
        holder.tvPostTime.text = post.timeAgo
        holder.tvPostContent.text = post.content
        holder.tvLikeCount.text = post.likeCount.toString()
        holder.tvCommentCount.text = post.commentCount.toString()

        // Set click listeners for like and comment buttons
        holder.llLikeButton.setOnClickListener {
            // TODO: Handle like action
        }

        holder.llCommentButton.setOnClickListener {
            // TODO: Handle comment action
        }
    }

    override fun getItemCount(): Int = postsList.size
}
