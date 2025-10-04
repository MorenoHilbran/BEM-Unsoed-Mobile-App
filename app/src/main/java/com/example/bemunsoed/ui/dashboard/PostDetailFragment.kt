package com.example.bemunsoed.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bemunsoed.R
import com.example.bemunsoed.databinding.FragmentPostDetailBinding
import com.example.bemunsoed.ui.adapter.CommentAdapter
import com.example.bemunsoed.data.model.Post
import com.example.bemunsoed.util.ProfileOptionsManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var commentAdapter: CommentAdapter
    private lateinit var viewModel: DashboardViewModel

    private var postId: String = ""
    private var currentUserId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)

        // Get post ID from arguments
        postId = arguments?.getString("postId") ?: ""

        setupUI()
        setupRecyclerView()
        observeViewModel()

        if (postId.isNotEmpty()) {
            viewModel.loadPostDetail(postId)
            viewModel.loadComments(postId)
        }

        return binding.root
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSendComment.setOnClickListener {
            val comment = binding.etComment.text.toString().trim()
            if (comment.isNotEmpty()) {
                viewModel.addComment(postId, comment)
                binding.etComment.text?.clear()
            } else {
                Toast.makeText(context, "Please enter a comment", Toast.LENGTH_SHORT).show()
            }
        }

        // Delete post button click listener
        binding.btnDeletePost.setOnClickListener {
            handleDeletePost()
        }
    }

    private fun setupRecyclerView() {
        commentAdapter = CommentAdapter(
            onLikeClick = { comment ->
                viewModel.toggleCommentLike(comment.id, postId)
            },
            onDeleteClick = { comment ->
                handleDeleteComment(comment)
            },
            currentUserId = viewModel.getCurrentUserId() ?: ""
        )

        binding.rvComments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = commentAdapter
        }
    }

    private fun observeViewModel() {
        currentUserId = viewModel.getCurrentUserId() ?: ""

        // Observe post detail
        viewModel.currentPost.observe(viewLifecycleOwner) { post: Post? ->
            if (post != null) {
                // Post masih ada - tampilkan normal
                binding.layoutPostContent.visibility = View.VISIBLE
                binding.layoutPostDeleted.visibility = View.GONE

                // Set profile photo from predefined options
                val profilePhotoRes = ProfileOptionsManager.getProfilePhotoDrawable(post.authorProfilePhotoId)
                binding.ivUserAvatar.setImageResource(profilePhotoRes)

                binding.tvUserName.text = post.authorName
                binding.tvPostTime.text = formatTime(post.createdAt)
                binding.tvPostContent.text = post.content

                // Show delete button only if current user is the author
                if (post.authorId == currentUserId) {
                    binding.btnDeletePost.visibility = View.VISIBLE
                } else {
                    binding.btnDeletePost.visibility = View.GONE
                }

                // Like button
                binding.btnLike.setImageResource(
                    if (post.isLiked) R.drawable.ic_favorite_filled
                    else R.drawable.ic_favorite_outline
                )
                binding.tvLikeCount.text = when {
                    post.likeCount == 0 -> "Like"
                    post.likeCount == 1 -> "1 like"
                    else -> "${post.likeCount} likes"
                }

                // Comment count
                binding.tvCommentCount.text = when {
                    post.commentCount == 0 -> "No comments yet"
                    post.commentCount == 1 -> "1 comment"
                    else -> "${post.commentCount} comments"
                }

                binding.layoutLike.setOnClickListener { _ ->
                    viewModel.toggleLike(postId)
                }
            } else {
                // Post sudah dihapus - tampilkan placeholder
                binding.layoutPostContent.visibility = View.GONE
                binding.layoutPostDeleted.visibility = View.VISIBLE
            }
        }

        // Observe comments
        viewModel.comments.observe(viewLifecycleOwner) { comments ->
            commentAdapter.submitList(comments)

            if (comments.isEmpty()) {
                binding.tvNoComments.visibility = View.VISIBLE
                binding.rvComments.visibility = View.GONE
            } else {
                binding.tvNoComments.visibility = View.GONE
                binding.rvComments.visibility = View.VISIBLE
            }
        }

        // Observe loading
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading: Boolean ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe comment result
        viewModel.commentResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(context, "Comment added!", Toast.LENGTH_SHORT).show()
                    viewModel.loadComments(postId)
                    viewModel.loadPostDetail(postId) // Refresh to update comment count
                } else {
                    Toast.makeText(
                        context,
                        "Failed to add comment: ${it.exceptionOrNull()?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                viewModel.clearCommentResult()
            }
        }

        // Observe delete comment result
        viewModel.deleteCommentResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(context, "Comment deleted successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    val error = it.exceptionOrNull()?.message ?: "Failed to delete comment"
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
                viewModel.clearDeleteCommentResult()
            }
        }

        // Observe delete post result
        viewModel.deletePostResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(context, "Post deleted successfully!", Toast.LENGTH_SHORT).show()
                    // Navigate back to dashboard after successful delete
                    findNavController().navigateUp()
                } else {
                    val error = it.exceptionOrNull()?.message ?: "Failed to delete post"
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
                viewModel.clearDeletePostResult()
            }
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
                    else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
                }
            } else {
                "Unknown time"
            }
        } catch (_: Exception) {
            "Unknown time"
        }
    }

    private fun handleDeletePost() {
        // Show confirmation dialog
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this post? Comments will still be visible in recent activity.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deletePost(postId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun handleDeleteComment(comment: com.example.bemunsoed.data.model.Comment) {
        // Show confirmation dialog
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Comment")
            .setMessage("Are you sure you want to delete this comment?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteComment(comment.id, postId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
