package com.example.bemunsoed.ui.notifications

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.bemunsoed.R
import com.example.bemunsoed.ui.notifications.adapter.UserPostAdapter
import com.example.bemunsoed.ui.notifications.adapter.UserCommentAdapter
import com.example.bemunsoed.ui.notifications.model.UserPost
import com.example.bemunsoed.data.model.Post
import com.example.bemunsoed.data.model.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import android.util.Log
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.example.bemunsoed.ui.adapter.ProfileOptionAdapter
import com.example.bemunsoed.util.ProfileOptionsManager

class NotificationsFragment : Fragment() {

    // Views
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var ivProfileBackground: ImageView
    private lateinit var ivProfilePhoto: ImageView
    private lateinit var btnEditProfile: Button
    private lateinit var tvUserName: TextView
    private lateinit var tvUserTitle: TextView
    private lateinit var tvUserBio: TextView
    private lateinit var tvPostsCount: TextView
    private lateinit var tvJoinedDate: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    // ViewModel
    private lateinit var profileViewModel: ProfileViewModel

    // Adapters for tabs
    private lateinit var userPostAdapter: UserPostAdapter
    private lateinit var userCommentAdapter: UserCommentAdapter

    // RecyclerViews for tabs
    private var postsRecyclerView: RecyclerView? = null
    private var commentsRecyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        initViews(view)
        setupAdapters()
        setupTabsAndViewPager()
        setupClickListeners()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        Log.d("NotificationsFragment", "Fragment resumed, refreshing data...")
        profileViewModel.refreshProfile()
    }

    private fun initViews(view: View) {
        swipeRefresh = view.findViewById(R.id.swipe_refresh)
        ivProfileBackground = view.findViewById(R.id.iv_profile_background)
        ivProfilePhoto = view.findViewById(R.id.iv_profile_photo)
        btnEditProfile = view.findViewById(R.id.btn_edit_profile)
        tvUserName = view.findViewById(R.id.tv_user_name)
        tvUserTitle = view.findViewById(R.id.tv_user_title)
        tvUserBio = view.findViewById<TextView?>(R.id.tv_user_bio) ?: createBioTextView(view)
        tvPostsCount = view.findViewById(R.id.tv_posts_count)
        tvJoinedDate = view.findViewById(R.id.tv_joined_date)
        tabLayout = view.findViewById(R.id.tab_layout)
        viewPager = view.findViewById(R.id.view_pager)

        Log.d("NotificationsFragment", "All views initialized successfully")
    }

    private fun createBioTextView(rootView: View): TextView {
        val bioTextView = TextView(requireContext()).apply {
            id = R.id.tv_user_bio
            textSize = 14f
            setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            maxLines = 3
            text = "Bio akan tampil di sini..."
        }

        val userInfoLayout = rootView.findViewById<ViewGroup>(R.id.ll_user_info)
        val titleIndex = userInfoLayout.indexOfChild(tvUserTitle)
        if (titleIndex >= 0) {
            val layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.topMargin = 8
            userInfoLayout.addView(bioTextView, titleIndex + 1, layoutParams)
        }

        return bioTextView
    }

    private fun setupAdapters() {
        userPostAdapter = UserPostAdapter { post ->
            handlePostClick(post)
        }

        userCommentAdapter = UserCommentAdapter { comment ->
            handleCommentClick(comment)
        }

        Log.d("NotificationsFragment", "Adapters setup completed")
    }

    private fun setupTabsAndViewPager() {
        // Setup ViewPager with tabs
        val adapter = ProfilePagerAdapter(this)
        viewPager.adapter = adapter

        // Connect TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Posts"
                1 -> "Comments"
                else -> ""
            }
        }.attach()

        // Get RecyclerViews after ViewPager is set up
        viewPager.post {
            setupRecyclerViewsInTabs()
        }

        Log.d("NotificationsFragment", "Tabs and ViewPager setup completed")
    }

    private fun setupRecyclerViewsInTabs() {
        try {
            // Get the RecyclerView from Posts tab
            val postsFragment = childFragmentManager.findFragmentByTag("f0")
            postsFragment?.view?.let { postsView ->
                postsRecyclerView = postsView.findViewById(R.id.rv_posts)
                postsRecyclerView?.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = userPostAdapter
                    isNestedScrollingEnabled = false
                }
            }

            // Get the RecyclerView from Comments tab
            val commentsFragment = childFragmentManager.findFragmentByTag("f1")
            commentsFragment?.view?.let { commentsView ->
                commentsRecyclerView = commentsView.findViewById(R.id.rv_comments)
                commentsRecyclerView?.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = userCommentAdapter
                    isNestedScrollingEnabled = false
                }
            }

            Log.d("NotificationsFragment", "RecyclerViews in tabs setup completed")
        } catch (e: Exception) {
            Log.e("NotificationsFragment", "Error setting up RecyclerViews", e)
        }
    }

    private fun setupClickListeners() {
        swipeRefresh.setOnRefreshListener {
            profileViewModel.refreshProfile()
        }

        btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        ivProfilePhoto.setOnClickListener {
            showProfilePhotoPickerDialog()
        }

        ivProfileBackground.setOnClickListener {
            showBackgroundPickerDialog()
        }

        Log.d("NotificationsFragment", "Click listeners setup completed")
    }

    private fun observeViewModel() {
        // Observe current user profile
        profileViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            Log.d("NotificationsFragment", "User profile updated: ${user.name}")
            updateUserProfile(user)
        }

        // Observe user posts
        profileViewModel.userPosts.observe(viewLifecycleOwner) { posts ->
            Log.d("NotificationsFragment", "User posts updated: ${posts.size} posts")
            updateUserPosts(posts)
            updatePostsCount(posts.size)
        }

        // Observe user comments
        profileViewModel.userComments.observe(viewLifecycleOwner) { comments ->
            Log.d("NotificationsFragment", "User comments updated: ${comments.size} comments")
            updateUserComments(comments)
        }

        // Observe loading state
        profileViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d("NotificationsFragment", "Loading state: $isLoading")
            swipeRefresh.isRefreshing = isLoading
        }

        // Observe error messages
        profileViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Log.w("NotificationsFragment", "Error: $errorMessage")
                Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_LONG).show()
                profileViewModel.clearError()
            }
        }

        // Observe update profile result
        profileViewModel.updateProfileResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    val error = it.exceptionOrNull()?.message ?: "Failed to update profile"
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
                profileViewModel.clearUpdateResult()
            }
        }
    }

    private fun updateUserProfile(user: User) {
        tvUserName.text = user.name.ifEmpty { "BEM Unsoed User" }

        // Display fakultas, jurusan, and angkatan
        tvUserTitle.text = if (user.jurusan.isNotEmpty() && user.angkatan.isNotEmpty()) {
            "${user.jurusan} - ${user.angkatan}"
        } else {
            "Gensoed kece"
        }

        tvUserBio.text = if (!user.bio.isNullOrEmpty()) {
            user.bio
        } else {
            "Bio belum diisi. Tap 'Edit Profile' untuk menambahkan bio."
        }

        tvJoinedDate.text = "Bergabung ${profileViewModel.getJoinDate()}"

        // Set profile photo from predefined options
        val profilePhotoRes = ProfileOptionsManager.getProfilePhotoDrawable(user.profilePhotoId)
        ivProfilePhoto.setImageResource(profilePhotoRes)

        // Set background from predefined options
        val backgroundRes = ProfileOptionsManager.getBackgroundDrawable(user.backgroundId)
        ivProfileBackground.setImageResource(backgroundRes)
    }

    private fun updateUserPosts(posts: List<Post>) {
        val userPosts = posts.take(20).map { post ->
            UserPost(
                id = post.id,
                content = post.content,
                timestamp = formatTimestamp(post.createdAt),
                likeCount = post.likeCount,
                commentCount = post.commentCount
            )
        }

        userPostAdapter.updatePosts(userPosts)

        // Show/hide empty state
        postsRecyclerView?.let { rv ->
            val emptyView = rv.parent?.let { (it as? ViewGroup)?.findViewById<TextView>(R.id.tv_empty_posts) }
            if (posts.isEmpty()) {
                rv.visibility = View.GONE
                emptyView?.visibility = View.VISIBLE
            } else {
                rv.visibility = View.VISIBLE
                emptyView?.visibility = View.GONE
            }
        }
    }

    private fun updateUserComments(comments: List<com.example.bemunsoed.data.model.Comment>) {
        userCommentAdapter.submitList(comments)

        // Show/hide empty state
        commentsRecyclerView?.let { rv ->
            val emptyView = rv.parent?.let { (it as? ViewGroup)?.findViewById<TextView>(R.id.tv_empty_comments) }
            if (comments.isEmpty()) {
                rv.visibility = View.GONE
                emptyView?.visibility = View.VISIBLE
            } else {
                rv.visibility = View.VISIBLE
                emptyView?.visibility = View.GONE
            }
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        return try {
            if (timestamp != 0L) {
                val date = java.util.Date(timestamp)
                val now = java.util.Date()
                val diffInMillis = now.time - date.time
                val diffInMinutes = diffInMillis / (1000 * 60)

                when {
                    diffInMinutes < 1 -> "Just now"
                    diffInMinutes < 60 -> "${diffInMinutes}m ago"
                    diffInMinutes < 1440 -> "${diffInMinutes / 60}h ago"
                    else -> java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault()).format(date)
                }
            } else {
                "Unknown time"
            }
        } catch (e: Exception) {
            "Unknown time"
        }
    }

    private fun updatePostsCount(count: Int) {
        tvPostsCount.text = "$count Posts"
    }

    private fun handlePostClick(userPost: UserPost) {
        Log.d("NotificationsFragment", "Post clicked: ${userPost.id}")
        // Navigate to post detail
        try {
            val bundle = Bundle().apply {
                putString("postId", userPost.id)
            }
            findNavController().navigate(R.id.action_navigation_notifications_to_postDetail, bundle)
        } catch (e: Exception) {
            Log.e("NotificationsFragment", "Navigation error", e)
            Toast.makeText(context, "Membuka detail postingan...", Toast.LENGTH_SHORT).show()
            // Fallback: try alternative navigation
            try {
                val bundle = Bundle().apply {
                    putString("postId", userPost.id)
                }
                findNavController().navigate(R.id.postDetailFragment, bundle)
            } catch (e2: Exception) {
                Log.e("NotificationsFragment", "Alternative navigation also failed", e2)
            }
        }
    }

    private fun handleCommentClick(comment: com.example.bemunsoed.data.model.Comment) {
        Log.d("NotificationsFragment", "Comment clicked: ${comment.id}, navigating to post: ${comment.postId}")
        // Navigate to the post where this comment was made
        try {
            val bundle = Bundle().apply {
                putString("postId", comment.postId)
            }
            findNavController().navigate(R.id.action_navigation_notifications_to_postDetail, bundle)
        } catch (e: Exception) {
            Log.e("NotificationsFragment", "Navigation error", e)
            Toast.makeText(context, "Membuka postingan...", Toast.LENGTH_SHORT).show()
            // Fallback: try alternative navigation
            try {
                val bundle = Bundle().apply {
                    putString("postId", comment.postId)
                }
                findNavController().navigate(R.id.postDetailFragment, bundle)
            } catch (e2: Exception) {
                Log.e("NotificationsFragment", "Alternative navigation also failed", e2)
            }
        }
    }

    private fun showEditProfileDialog() {
        val currentUser = profileViewModel.currentUser.value ?: return

        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val etName = dialogView.findViewById<EditText>(R.id.et_name)
        val etBio = dialogView.findViewById<EditText>(R.id.et_bio)

        // Fill current values
        etName.setText(currentUser.name)
        etBio.setText(currentUser.bio ?: "")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val updatedUser = currentUser.copy(
                    name = etName.text.toString().trim(),
                    bio = etBio.text.toString().trim()
                )
                profileViewModel.updateProfile(updatedUser)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showProfilePhotoPickerDialog() {
        val currentUser = profileViewModel.currentUser.value ?: return

        val dialogView = layoutInflater.inflate(R.layout.dialog_profile_picker, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rv_profile_options)

        val options = ProfileOptionsManager.getProfilePhotoOptions()
        val adapter = ProfileOptionAdapter(options) { selectedOption ->
            // Update user profile with new photo ID
            val updatedUser = currentUser.copy(profilePhotoId = selectedOption.id)
            profileViewModel.updateProfile(updatedUser)
            Toast.makeText(context, "Foto profil berhasil diubah!", Toast.LENGTH_SHORT).show()
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // Set current selection
        val currentPosition = ProfileOptionsManager.getProfilePhotoPosition(currentUser.profilePhotoId)
        adapter.setSelectedPosition(currentPosition)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Pilih Foto Profil")
            .setView(dialogView)
            .setNegativeButton("Batal", null)
            .create()

        dialog.show()
    }

    private fun showBackgroundPickerDialog() {
        val currentUser = profileViewModel.currentUser.value ?: return

        val dialogView = layoutInflater.inflate(R.layout.dialog_profile_picker, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rv_profile_options)

        val options = ProfileOptionsManager.getBackgroundOptions()
        val adapter = ProfileOptionAdapter(options) { selectedOption ->
            // Update user profile with new background ID
            val updatedUser = currentUser.copy(backgroundId = selectedOption.id)
            profileViewModel.updateProfile(updatedUser)
            Toast.makeText(context, "Background berhasil diubah!", Toast.LENGTH_SHORT).show()
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // Set current selection
        val currentPosition = ProfileOptionsManager.getBackgroundPosition(currentUser.backgroundId)
        adapter.setSelectedPosition(currentPosition)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Pilih Background")
            .setView(dialogView)
            .setNegativeButton("Batal", null)
            .create()

        dialog.show()
    }

    // Inner class for ViewPager adapter
    inner class ProfilePagerAdapter(fragment: Fragment) : androidx.viewpager2.adapter.FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> UserPostsTabFragment.newInstance(userPostAdapter)
                1 -> UserCommentsTabFragment.newInstance(userCommentAdapter)
                else -> throw IllegalStateException("Invalid position")
            }
        }
    }
}
