package com.example.bemunsoed.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bemunsoed.MainActivity
import com.example.bemunsoed.R
import com.example.bemunsoed.databinding.ActivityLoginBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import android.util.Log
import android.content.DialogInterface

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
        // Get current email from field or empty
        val currentEmail = binding.etEmail.text.toString().trim()

        // Create custom dialog with EditText
        val dialogView = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
        val emailEditText = dialogView.findViewById<EditText>(R.id.etEmailReset)
        emailEditText.setText(currentEmail)

        MaterialAlertDialogBuilder(this)
            .setTitle("Lupa Password")
            .setMessage("Masukkan email yang terdaftar. Kami akan mengirimkan link untuk reset password ke email Anda.")
            .setView(dialogView)
            .setPositiveButton("Kirim") { dialog: DialogInterface, which: Int ->
                val email = emailEditText.text.toString().trim()
                sendPasswordResetEmail(email)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun sendPasswordResetEmail(email: String) {
        // Validate email
        if (email.isEmpty()) {
            Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading
        setLoading(true)

        lifecycleScope.launch {
            try {
                Log.d("LoginActivity", "Sending password reset email to: $email")

                when (val result = authManager.resetPassword(email)) {
                    is AuthenticationResult.Success -> {
                        setLoading(false)
                        Log.d("LoginActivity", "Password reset email sent successfully")

                        // Show success dialog with instructions
                        MaterialAlertDialogBuilder(this@LoginActivity)
                            .setTitle("Email Terkirim!")
                            .setMessage(
                                "Link reset password telah dikirim ke:\n\n$email\n\n" +
                                "Silakan cek inbox atau folder spam email Anda. " +
                                "Link akan kedaluwarsa dalam 1 jam.\n\n" +
                                "Jika tidak menerima email setelah beberapa menit, pastikan:\n" +
                                "• Email yang dimasukkan sudah benar\n" +
                                "• Akun dengan email tersebut sudah terdaftar\n" +
                                "• Cek folder spam/junk"
                            )
                            .setPositiveButton("OK") { dialog: DialogInterface, _ ->
                                dialog.dismiss()
                            }
                            .setNeutralButton("Kirim Ulang") { _, _ ->
                                sendPasswordResetEmail(email)
                            }
                            .show()
                    }
                    is AuthenticationResult.Error -> {
                        setLoading(false)
                        Log.e("LoginActivity", "Failed to send reset email: ${result.message}")

                        // Show error with helpful message
                        val errorMessage = when {
                            result.message.contains("no user", ignoreCase = true) ||
                            result.message.contains("not found", ignoreCase = true) -> {
                                "Email tidak terdaftar. Pastikan email yang Anda masukkan sudah terdaftar di aplikasi."
                            }
                            result.message.contains("invalid email", ignoreCase = true) -> {
                                "Format email tidak valid. Periksa kembali email Anda."
                            }
                            result.message.contains("network", ignoreCase = true) -> {
                                "Tidak ada koneksi internet. Periksa koneksi Anda dan coba lagi."
                            }
                            else -> {
                                "Gagal mengirim email reset password: ${result.message}\n\nPastikan email sudah terdaftar dan koneksi internet stabil."
                            }
                        }

                        MaterialAlertDialogBuilder(this@LoginActivity)
                            .setTitle("Gagal Mengirim Email")
                            .setMessage(errorMessage)
                            .setPositiveButton("OK", null)
                            .setNeutralButton("Coba Lagi") { _, _ ->
                                showForgotPasswordDialog()
                            }
                            .show()
                    }
                }
            } catch (e: Exception) {
                setLoading(false)
                Log.e("LoginActivity", "Exception sending reset email", e)
                Toast.makeText(
                    this@LoginActivity,
                    "Terjadi kesalahan: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.tvRegister.isEnabled = !isLoading
        binding.tvForgotPassword.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
