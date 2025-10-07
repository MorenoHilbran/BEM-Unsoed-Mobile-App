package com.example.bemunsoed

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.example.bemunsoed.auth.AuthManager
import com.example.bemunsoed.auth.LoginActivity
import com.example.bemunsoed.databinding.ActivityMainBinding
import com.example.bemunsoed.ui.notifications.NotificationViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var authManager: AuthManager
    private lateinit var notificationViewModel: NotificationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize AuthManager
        authManager = AuthManager()

        // Check if user is logged in
        if (!authManager.isUserLoggedIn()) {
            // Redirect to login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize NotificationViewModel
        notificationViewModel = ViewModelProvider(this)[NotificationViewModel::class.java]

        // Ensure status bar is visible and content doesn't overlap
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupNavigation()
        setupCustomBottomNavigation()
        setupNavbarClickListeners()
        // setupNotificationBadge() // Badge setup disabled
        // observeNotifications() // Notification observation disabled
    }

    private fun setupNavigation() {
        // Navigation is handled by custom bottom navigation
        // NavController setup can be added here if needed in the future
    }

    private fun setupCustomBottomNavigation() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Setup Home button
        findViewById<View>(R.id.nav_home).setOnClickListener {
            navController.navigate(R.id.navigation_home)
        }

        // Setup Forum button (center special button)
        findViewById<View>(R.id.nav_forum).setOnClickListener {
            navController.navigate(R.id.navigation_dashboard)
        }

        // Setup Profile button
        findViewById<View>(R.id.nav_profile).setOnClickListener {
            navController.navigate(R.id.navigation_notifications)
        }
    }

    private fun setupNavbarClickListeners() {
        // Notification icon click
        findViewById<View>(R.id.notification_icon)?.setOnClickListener {
            openNotificationList()
        }

        // Logout icon click
        findViewById<View>(R.id.logout_icon)?.setOnClickListener {
            showLogoutDialog()
        }
    }


    private fun openNotificationList() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        try {
            navController.navigate(R.id.notificationListFragment)
        } catch (e: Exception) {
            // If navigation fails, show toast
            android.widget.Toast.makeText(this, "Opening notifications...", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh notification count when activity resumes
        notificationViewModel.loadUnreadCount()
    }

    private fun showLogoutDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Apakah Anda yakin ingin keluar?")
        builder.setPositiveButton("Ya") { _, _ ->
            performLogout()
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun performLogout() {
        // Logout dari Firebase
        authManager.logout()

        // Redirect ke login activity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}