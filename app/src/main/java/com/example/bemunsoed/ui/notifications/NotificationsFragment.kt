package com.example.bemunsoed.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.bemunsoed.R
import com.example.bemunsoed.ui.notifications.adapter.UserPostAdapter
import com.example.bemunsoed.ui.notifications.model.UserPost

class NotificationsFragment : Fragment() {

    // Views
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var ivProfileBackground: ImageView
    private lateinit var ivProfilePhoto: ImageView
    private lateinit var btnEditProfile: Button
    private lateinit var tvUserName: TextView
    private lateinit var tvUserTitle: TextView
    private lateinit var tvPostsCount: TextView
    private lateinit var tvJoinedDate: TextView
    private lateinit var tvViewAllPosts: TextView
    private lateinit var rvUserPosts: RecyclerView

    // Adapter
    private lateinit var userPostAdapter: UserPostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupUserProfile()
        setupUserPosts()
        setupClickListeners()
    }

    private fun initViews(view: View) {
        swipeRefresh = view.findViewById(R.id.swipe_refresh)
        ivProfileBackground = view.findViewById(R.id.iv_profile_background)
        ivProfilePhoto = view.findViewById(R.id.iv_profile_photo)
        btnEditProfile = view.findViewById(R.id.btn_edit_profile)
        tvUserName = view.findViewById(R.id.tv_user_name)
        tvUserTitle = view.findViewById(R.id.tv_user_title)
        tvPostsCount = view.findViewById(R.id.tv_posts_count)
        tvJoinedDate = view.findViewById(R.id.tv_joined_date)
        tvViewAllPosts = view.findViewById(R.id.tv_view_all_posts)
        rvUserPosts = view.findViewById(R.id.rv_user_posts)
    }

    private fun setupUserProfile() {
        // Set profile images (using your uploaded images)
        ivProfileBackground.setImageResource(R.drawable.test1)
        ivProfilePhoto.setImageResource(R.drawable.profil)

        // Set user information
        tvUserName.text = "John Doe"
        tvUserTitle.text = "Mahasiswa Teknik Informatika | Content Creator"
        tvPostsCount.text = "42 Posts"
        tvJoinedDate.text = "Joined September 2024"
    }

    private fun setupUserPosts() {
        // Create sample posts data
        val samplePosts = listOf(
            UserPost(
                id = 1,
                userName = "John Doe",
                userAvatar = R.drawable.profil,
                content = "Baru aja selesai ngerjain tugas algoritma, cape banget tapi alhamdulillah selesai juga 🙏",
                timeAgo = "2h",
                likeCount = 12,
                commentCount = 3
            ),
            UserPost(
                id = 2,
                userName = "John Doe",
                userAvatar = R.drawable.profil,
                content = "Siapa yang mau ikut study group buat persiapan UTS? Drop di komen ya!",
                timeAgo = "5h",
                likeCount = 24,
                commentCount = 8
            ),
            UserPost(
                id = 3,
                userName = "John Doe",
                userAvatar = R.drawable.profil,
                content = "Tips belajar coding: jangan lupa istirahat dan minum air putih yang cukup 💻💪",
                timeAgo = "1d",
                likeCount = 18,
                commentCount = 5
            ),
            UserPost(
                id = 4,
                userName = "John Doe",
                userAvatar = R.drawable.profil,
                content = "Lagi nyoba bikin aplikasi Android nih, doain lancar ya teman-teman!",
                timeAgo = "2d",
                likeCount = 31,
                commentCount = 12
            )
        )

        // Setup RecyclerView
        userPostAdapter = UserPostAdapter(samplePosts)
        rvUserPosts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userPostAdapter
        }
    }

    private fun setupClickListeners() {
        // Edit Profile button
        btnEditProfile.setOnClickListener {
            Toast.makeText(context, "Edit Profile akan segera hadir!", Toast.LENGTH_SHORT).show()
        }

        // View All Posts
        tvViewAllPosts.setOnClickListener {
            Toast.makeText(context, "View All Posts akan segera hadir!", Toast.LENGTH_SHORT).show()
        }

        // Swipe to refresh
        swipeRefresh.setOnRefreshListener {
            // Simulate refresh
            swipeRefresh.isRefreshing = false
            Toast.makeText(context, "Profile refreshed!", Toast.LENGTH_SHORT).show()
        }
    }
}