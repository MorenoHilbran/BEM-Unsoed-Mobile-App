package com.example.bemunsoed.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bemunsoed.databinding.FragmentDashboardBinding
import com.example.bemunsoed.ui.adapter.PostAdapter
import com.example.bemunsoed.data.model.Post
import com.google.android.material.snackbar.Snackbar
import android.util.Log
import android.widget.Toast
import com.example.bemunsoed.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var postAdapter: PostAdapter
    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        setupFAB()
        setupSwipeRefresh()
        observeViewModel()

        // Load posts when fragment is created
        dashboardViewModel.loadPosts()

        return root
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(
            onItemClick = { post -> handleItemClick(post) },
            onLikeClick = { post -> handleLikeClick(post) },
            onDeleteClick = { post -> handleDeleteClick(post) },
            currentUserId = dashboardViewModel.getCurrentUserId()
        )

        binding.rvPosts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            dashboardViewModel.refreshPosts()
        }
    }

    private fun setupFAB() {
        binding.fabCreatePost.setOnClickListener {
            showCreatePostDialog()
        }
    }

    private fun observeViewModel() {
        // Observe posts
        dashboardViewModel.posts.observe(viewLifecycleOwner) { posts ->
            Log.d("DashboardFragment", "Posts updated: ${posts.size} items")
            postAdapter.submitList(posts)

            // Show/hide empty state
            if (posts.isEmpty()) {
                binding.tvNoPosts.visibility = View.VISIBLE
                binding.rvPosts.visibility = View.GONE
            } else {
                binding.tvNoPosts.visibility = View.GONE
                binding.rvPosts.visibility = View.VISIBLE
            }
        }

        // Observe loading state
        dashboardViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d("DashboardFragment", "Loading state: $isLoading")
            binding.swipeRefresh.isRefreshing = isLoading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe error messages
        dashboardViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Log.w("DashboardFragment", "Error: $errorMessage")
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
                dashboardViewModel.clearError()
            }
        }

        // Observe create post result
        dashboardViewModel.createPostResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(context, "Post created successfully!", Toast.LENGTH_SHORT).show()
                    dashboardViewModel.refreshPosts() // Refresh to show new post
                } else {
                    val error = it.exceptionOrNull()?.message ?: "Failed to create post"
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
                dashboardViewModel.clearCreatePostResult()
            }
        }

        // Observe like result
        dashboardViewModel.likeResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isFailure) {
                    val error = it.exceptionOrNull()?.message ?: "Failed to update like"
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
                dashboardViewModel.clearLikeResult()
            }
        }

        // Observe comment result
        dashboardViewModel.commentResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(context, "Comment added!", Toast.LENGTH_SHORT).show()
                    dashboardViewModel.refreshPosts() // Refresh to show updated comment count
                } else {
                    val error = it.exceptionOrNull()?.message ?: "Failed to add comment"
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
                dashboardViewModel.clearCommentResult()
            }
        }

        // Observe delete post result
        dashboardViewModel.deletePostResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(context, "Post deleted successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    val error = it.exceptionOrNull()?.message ?: "Failed to delete post"
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
                dashboardViewModel.clearDeletePostResult()
            }
        }
    }

    private fun handleLikeClick(post: Post) {
        // Toggle like
        dashboardViewModel.toggleLike(post.id)
    }

    private fun handleItemClick(post: Post) {
        // Navigate to post detail with comments
        Log.d("DashboardFragment", "Post clicked: ${post.id}")
        val bundle = Bundle().apply {
            putString("postId", post.id)
        }
        try {
            findNavController().navigate(R.id.postDetailFragment, bundle)
        } catch (e: Exception) {
            Log.e("DashboardFragment", "Navigation error", e)
            Toast.makeText(context, "Opening post details...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCreatePostDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_post, null)
        val etPostContent = dialogView.findViewById<EditText>(R.id.et_post_content)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Create New Post")
            .setView(dialogView)
            .setPositiveButton("Post") { _, _ ->
                val content = etPostContent.text.toString().trim()
                if (content.isNotEmpty()) {
                    // Use the new createPost function that automatically includes profile photo ID
                    dashboardViewModel.createPost(content)
                } else {
                    Toast.makeText(context, "Please enter some content", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun handleDeleteClick(post: Post) {
        // Show confirmation dialog
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this post? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                dashboardViewModel.deletePost(post.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        // Refresh posts when fragment becomes visible to ensure we show latest data
        Log.d("DashboardFragment", "Fragment resumed, refreshing posts...")
        dashboardViewModel.refreshPosts()
    }
}
