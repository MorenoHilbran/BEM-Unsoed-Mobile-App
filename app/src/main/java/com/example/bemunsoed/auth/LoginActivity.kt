package com.example.bemunsoed.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bemunsoed.MainActivity
import com.example.bemunsoed.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authManager = AuthManager()

        // Check if user is already logged in
        if (authManager.isUserLoggedIn()) {
            navigateToMain()
            return
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Validation
        if (email.isEmpty()) {
            binding.etEmail.error = "Email harus diisi"
            return
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Password harus diisi"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Format email tidak valid"
            return
        }

        // Show loading
        setLoading(true)

        // Perform login
        lifecycleScope.launch {
            when (val result = authManager.loginWithEmail(email, password)) {
                is AuthenticationResult.Success -> {
                    setLoading(false)
                    Toast.makeText(this@LoginActivity, "Login berhasil!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                is AuthenticationResult.Error -> {
                    setLoading(false)
                    Toast.makeText(this@LoginActivity, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showForgotPasswordDialog() {
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            Toast.makeText(this, "Masukkan email terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            when (val result = authManager.resetPassword(email)) {
                is AuthenticationResult.Success -> {
                    Toast.makeText(this@LoginActivity, "Email reset password telah dikirim", Toast.LENGTH_LONG).show()
                }
                is AuthenticationResult.Error -> {
                    Toast.makeText(this@LoginActivity, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.tvRegister.isEnabled = !isLoading
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
