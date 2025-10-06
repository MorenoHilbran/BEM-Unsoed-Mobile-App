package com.example.bemunsoed.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bemunsoed.R
import com.example.bemunsoed.data.model.Post
import com.example.bemunsoed.databinding.FragmentDashboardBinding
import com.example.bemunsoed.ui.adapter.PostAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import android.util.Log
import android.widget.Toast

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
        dashboardViewModel = ViewModelProvider(requireActivity())[DashboardViewModel::class.java] // Gunakan requireActivity()
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        setupFAB() // Diubah
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

    // --- PERUBAHAN DI SINI ---
    private fun setupFAB() {
        binding.fabCreatePost.setOnClickListener {
            // Navigasi ke halaman CreatePostFragment, pastikan ID action sudah benar
            findNavController().navigate(R.id.action_navigation_dashboard_to_createPostFragment)
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

        // Observer ini tetap di sini untuk me-refresh data setelah post baru dibuat dari fragment lain
        dashboardViewModel.createPostResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    // Refresh sudah otomatis terpanggil saat onResume, jadi toast tidak perlu lagi di sini
                    // agar tidak muncul dua kali.
                }
                // Hapus result agar tidak trigger lagi saat kembali ke fragment ini
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
        dashboardViewModel.toggleLike(post.id)
    }

    private fun handleItemClick(post: Post) {
        Log.d("DashboardFragment", "Post clicked: ${post.id}")
        val bundle = Bundle().apply {
            putString("postId", post.id)
        }
        try {
            // Pastikan ID action ke post detail sudah benar
            findNavController().navigate(R.id.action_navigation_dashboard_to_postDetailFragment, bundle)
        } catch (e: Exception) {
            Log.e("DashboardFragment", "Navigation error", e)
            Toast.makeText(context, "Opening post details...", Toast.LENGTH_SHORT).show()
        }
    }

    // --- METHOD INI DIHAPUS ---
    // private fun showCreatePostDialog() { ... }

    private fun handleDeleteClick(post: Post) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this post? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                dashboardViewModel.deletePost(post.id)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        Log.d("DashboardFragment", "Fragment resumed, refreshing posts...")
        dashboardViewModel.refreshPosts()
    }
}