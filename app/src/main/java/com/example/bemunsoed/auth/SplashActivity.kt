package com.example.bemunsoed.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bemunsoed.MainActivity
import com.example.bemunsoed.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay untuk splash screen dan check authentication
        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthenticationAndNavigate()
        }, 2000) // 2 detik splash
    }

    private fun checkAuthenticationAndNavigate() {
        try {
            val authManager = AuthManager()

            if (authManager.isUserLoggedIn()) {
                // User sudah login, langsung ke MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // User belum login, ke LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        } catch (e: Exception) {
            // Jika Firebase error, tetap ke LoginActivity
            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
