package com.example.bemunsoed.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bemunsoed.MainActivity
import com.example.bemunsoed.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authManager = AuthManager()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            performRegister()
        }

        binding.tvLogin.setOnClickListener {
            finish() // Kembali ke LoginActivity
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun performRegister() {
        val fullName = binding.etFullName.text.toString().trim()
        val nim = binding.etNim.text.toString().trim()
        val major = binding.etMajor.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // Validation
        if (fullName.isEmpty()) {
            binding.etFullName.error = "Nama lengkap harus diisi"
            return
        }

        if (nim.isEmpty()) {
            binding.etNim.error = "NIM harus diisi"
            return
        }

        if (major.isEmpty()) {
            binding.etMajor.error = "Jurusan harus diisi"
            return
        }

        if (email.isEmpty()) {
            binding.etEmail.error = "Email harus diisi"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Format email tidak valid"
            return
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Password harus diisi"
            return
        }

        if (password.length < 6) {
            binding.etPassword.error = "Password minimal 6 karakter"
            return
        }

        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Password tidak sama"
            return
        }

        // Show loading
        setLoading(true)

        // Perform registration
        lifecycleScope.launch {
            when (val result = authManager.registerWithEmail(email, password, fullName, nim, major)) {
                is AuthenticationResult.Success -> {
                    setLoading(false)
                    Toast.makeText(this@RegisterActivity, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                is AuthenticationResult.Error -> {
                    setLoading(false)
                    Toast.makeText(this@RegisterActivity, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
        binding.tvLogin.isEnabled = !isLoading
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
