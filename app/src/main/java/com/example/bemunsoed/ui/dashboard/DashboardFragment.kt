package com.example.bemunsoed.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bemunsoed.databinding.FragmentDashboardBinding
import com.example.bemunsoed.ui.adapter.PostAdapter
import com.example.bemunsoed.data.model.Post
import com.example.bemunsoed.data.model.User
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*

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
        loadSampleData()

        return root
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(
            onItemClick = { post -> handleItemClick(post) },
            onLikeClick = { post -> handleLikeClick(post) },
            onCommentClick = { post -> handleCommentClick(post) }
        )

        binding.rvPosts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            refreshPosts()
        }
    }

    private fun setupFAB() {
        binding.fabCreatePost.setOnClickListener {
            showCreatePostDialog()
        }
    }

    private fun loadSampleData() {
        // Sample data untuk testing
        val samplePosts = listOf(
            Post(
                id = "1",
                authorName = "BEM Unsoed",
                content = "Selamat datang di Forum BEM Unsoed! Mari berdiskusi dengan positif dan konstruktif 🎉",
                createdAt = getCurrentTime(),
                likeCount = 15,
                commentCount = 3,
                isLiked = false
            ),
            Post(
                id = "2",
                authorName = "Andi Pratama",
                content = "Ada yang tau info beasiswa terbaru ga? Lagi butuh banget nih buat semester depan",
                createdAt = getTimeHoursAgo(2),
                likeCount = 8,
                commentCount = 12,
                isLiked = true
            ),
            Post(
                id = "3",
                authorName = "Sarah Widya",
                content = "Event workshop programming minggu depan jangan lupa daftar ya! Kuota terbatas",
                createdAt = getTimeHoursAgo(5),
                likeCount = 23,
                commentCount = 7,
                isLiked = false
            ),
            Post(
                id = "4",
                authorName = "Rizki Maulana",
                content = "Sharing pengalaman magang di startup tech. Ada yang mau tanya-tanya?",
                createdAt = getTimeHoursAgo(8),
                likeCount = 31,
                commentCount = 18,
                isLiked = true
            )
        )
        postAdapter.submitList(samplePosts)
        binding.swipeRefresh.isRefreshing = false
    }

    private fun handleItemClick(post: Post) {
        // TODO: Navigate to post detail page
        // For now, just show a placeholder
    }

    private fun handleLikeClick(post: Post) {
        // Toggle like status (mock implementation)
        val updatedPost = post.copy(
            isLiked = !post.isLiked,
            likeCount = if (post.isLiked) post.likeCount - 1 else post.likeCount + 1
        )

        // Update the list
        val currentList = postAdapter.currentList.toMutableList()
        val index = currentList.indexOfFirst { it.id == post.id }
        if (index != -1) {
            currentList[index] = updatedPost
            postAdapter.submitList(currentList)
        }
    }

    private fun handleCommentClick(post: Post) {
        // TODO: Implement comment functionality
        // For now, just show a placeholder
    }

    private fun handleMoreClick(post: Post) {
        // TODO: Implement more options (report, delete if own post, etc.)
        // For now, just show a placeholder
    }

    private fun showCreatePostDialog() {
        val createPostFragment = CreatePostBottomSheetFragment()
        createPostFragment.show(parentFragmentManager, "CreatePostBottomSheet")
    }

    private fun refreshPosts() {
        // Simulate refresh delay
        binding.root.postDelayed({
            loadSampleData()
        }, 1000)
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getTimeHoursAgo(hours: Int): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR_OF_DAY, -hours)
        return sdf.format(calendar.time)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}