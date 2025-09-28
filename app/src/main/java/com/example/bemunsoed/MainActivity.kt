package com.example.bemunsoed

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.example.bemunsoed.databinding.ActivityMainBinding
import com.example.bemunsoed.auth.AuthManager
import com.example.bemunsoed.auth.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var authManager: AuthManager

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

        // Ensure status bar is visible and content doesn't overlap
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupNavigation()
        setupCustomBottomNavigation()
        setupNavbarClickListeners()
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
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
        // Search icon click
        findViewById<View>(R.id.search_icon)?.setOnClickListener {
            // TODO: Implement search functionality
        }

        // Notification icon click
        findViewById<View>(R.id.notification_icon)?.setOnClickListener {
            // TODO: Implement notification dropdown
        }

        // Profile icon click - show logout option
        findViewById<View>(R.id.profile_icon)?.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Apakah Anda yakin ingin keluar?")
        builder.setPositiveButton("Ya") { _, _ ->
            performLogout()
        }
        builder.setNegativeButton("Batal", null)
        builder.show()
    }

    private fun performLogout() {
        authManager.logout()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}