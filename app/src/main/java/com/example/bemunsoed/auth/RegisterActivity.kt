package com.example.bemunsoed.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bemunsoed.MainActivity
import com.example.bemunsoed.databinding.ActivityRegisterBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import android.util.Log

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authManager: AuthManager

    private var selectedFakultas: String = ""
    private var selectedJurusan: String = ""
    private var selectedAngkatan: String = ""

    // Data fakultas dan jurusan
    private val fakultasJurusanMap = mapOf(
        "Fakultas Pertanian" to listOf(
            "Agroteknologi",
            "Agribisnis",
            "Teknik Pertanian",
            "Ilmu & Teknologi Pangan"
        ),
        "Fakultas Biologi" to listOf(
            "Biologi"
        ),
        "Fakultas Ekonomi dan Bisnis" to listOf(
            "Manajemen",
            "Ilmu Ekonomi & Studi Pembangunan",
            "Akuntansi",
            "Pendidikan Ekonomi"
        ),
        "Fakultas Peternakan" to listOf(
            "Peternakan"
        ),
        "Fakultas Hukum" to listOf(
            "Ilmu Hukum"
        ),
        "Fakultas Ilmu Sosial dan Ilmu Politik" to listOf(
            "Sosiologi",
            "Ilmu Administrasi Negara",
            "Ilmu Komunikasi",
            "Ilmu Politik",
            "Hubungan Internasional"
        ),
        "Fakultas Kedokteran" to listOf(
            "Pendidikan Dokter",
            "Pendidikan Dokter Gigi"
        ),
        "Fakultas Teknik" to listOf(
            "Teknik Elektro",
            "Teknik Sipil",
            "Teknik Geologi",
            "Teknik Informatika",
            "Teknik Industri",
            "Teknik Mesin",
            "Teknik Komputer"
        ),
        "Fakultas Ilmu-ilmu Kesehatan (FIKES)" to listOf(
            "Kesehatan Masyarakat",
            "Ilmu Keperawatan",
            "Farmasi",
            "Ilmu Gizi",
            "Pendidikan Jasmani, Kesehatan, dan Rekreasi"
        ),
        "Fakultas Ilmu Budaya (FIB)" to listOf(
            "Bahasa & Sastra Inggris",
            "Bahasa & Sastra Indonesia",
            "Bahasa & Sastra Jepang",
            "Pendidikan Bahasa & Sastra Indonesia"
        ),
        "Fakultas Matematika & Ilmu Pengetahuan Alam (MIPA)" to listOf(
            "Matematika",
            "Fisika",
            "Kimia"
        ),
        "Fakultas Perikanan & Ilmu Kelautan" to listOf(
            "Manajemen Sumberdaya Perairan",
            "Budidaya Perairan",
            "Ilmu Kelautan"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityRegisterBinding.inflate(layoutInflater)
            setContentView(binding.root)

            authManager = AuthManager()
            setupDropdowns()
            setupClickListeners()
        } catch (e: Exception) {
            Log.e("RegisterActivity", "Error in onCreate", e)
            Toast.makeText(this, "Error initializing: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupDropdowns() {
        try {
            // Setup Fakultas Dropdown
            val fakultasList = fakultasJurusanMap.keys.toList()
            val fakultasAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                fakultasList
            )

            binding.spinnerFakultas.setAdapter(fakultasAdapter)
            binding.spinnerFakultas.setOnItemClickListener { _, _, position, _ ->
                try {
                    selectedFakultas = fakultasList[position]
                    Log.d("RegisterActivity", "Selected fakultas: $selectedFakultas")

                    // Reset jurusan when fakultas changes
                    selectedJurusan = ""
                    binding.spinnerJurusan.setText("", false)
                    binding.spinnerJurusan.isEnabled = true

                    // Setup jurusan dropdown based on selected fakultas
                    val jurusanList = fakultasJurusanMap[selectedFakultas] ?: emptyList()
                    val jurusanAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        jurusanList
                    )
                    binding.spinnerJurusan.setAdapter(jurusanAdapter)
                } catch (e: Exception) {
                    Log.e("RegisterActivity", "Error in fakultas selection", e)
                    Toast.makeText(this, "Error selecting fakultas", Toast.LENGTH_SHORT).show()
                }
            }

            // Setup Jurusan Dropdown
            binding.spinnerJurusan.setOnItemClickListener { _, _, position, _ ->
                try {
                    val jurusanList = fakultasJurusanMap[selectedFakultas] ?: emptyList()
                    if (position < jurusanList.size) {
                        selectedJurusan = jurusanList[position]
                        Log.d("RegisterActivity", "Selected jurusan: $selectedJurusan")
                    }
                } catch (e: Exception) {
                    Log.e("RegisterActivity", "Error in jurusan selection", e)
                    Toast.makeText(this, "Error selecting jurusan", Toast.LENGTH_SHORT).show()
                }
            }

            // Setup Angkatan Dropdown
            val angkatanList = listOf("Alumni", "2022", "2023", "2024", "2025")
            val angkatanAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                angkatanList
            )
            binding.spinnerAngkatan.setAdapter(angkatanAdapter)
            binding.spinnerAngkatan.setOnItemClickListener { _, _, position, _ ->
                try {
                    selectedAngkatan = angkatanList[position]
                    Log.d("RegisterActivity", "Selected angkatan: $selectedAngkatan")
                } catch (e: Exception) {
                    Log.e("RegisterActivity", "Error in angkatan selection", e)
                    Toast.makeText(this, "Error selecting angkatan", Toast.LENGTH_SHORT).show()
                }
            }

            Log.d("RegisterActivity", "Dropdowns setup completed")
        } catch (e: Exception) {
            Log.e("RegisterActivity", "Error setting up dropdowns", e)
            Toast.makeText(this, "Error setting up form: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            try {
                validateAndShowConfirmation()
            } catch (e: Exception) {
                Log.e("RegisterActivity", "Error in register button", e)
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvLogin.setOnClickListener {
            finish() // Kembali ke LoginActivity
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun validateAndShowConfirmation() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // Validation
        if (fullName.isEmpty()) {
            binding.etFullName.error = "Nama lengkap harus diisi"
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

        if (selectedFakultas.isEmpty()) {
            Toast.makeText(this, "Silakan pilih fakultas", Toast.LENGTH_SHORT).show()
            binding.spinnerFakultas.requestFocus()
            return
        }

        if (selectedJurusan.isEmpty()) {
            Toast.makeText(this, "Silakan pilih jurusan", Toast.LENGTH_SHORT).show()
            binding.spinnerJurusan.requestFocus()
            return
        }

        if (selectedAngkatan.isEmpty()) {
            Toast.makeText(this, "Silakan pilih angkatan", Toast.LENGTH_SHORT).show()
            binding.spinnerAngkatan.requestFocus()
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

        // Show 2FA Confirmation Dialog
        show2FAConfirmationDialog(fullName, email, password)
    }

    private fun show2FAConfirmationDialog(fullName: String, email: String, password: String) {
        try {
            val message = """
                Apakah data berikut sudah sesuai?
                
                Nama: $fullName
                Email: $email
                Fakultas: $selectedFakultas
                Jurusan: $selectedJurusan
                Angkatan: $selectedAngkatan
                
                ⚠️ PERHATIAN: Data fakultas, jurusan, dan angkatan tidak dapat diubah setelah registrasi!
            """.trimIndent()

            MaterialAlertDialogBuilder(this)
                .setTitle("Konfirmasi Data Registrasi")
                .setMessage(message)
                .setPositiveButton("Ya, Sudah Benar") { _, _ ->
                    performRegister(fullName, email, password)
                }
                .setNegativeButton("Periksa Kembali", null)
                .setCancelable(false)
                .show()
        } catch (e: Exception) {
            Log.e("RegisterActivity", "Error showing dialog", e)
            Toast.makeText(this, "Error showing confirmation: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performRegister(fullName: String, email: String, password: String) {
        // Show loading
        setLoading(true)

        // Perform registration
        lifecycleScope.launch {
            try {
                Log.d("RegisterActivity", "Starting registration...")
                when (val result = authManager.registerWithEmail(
                    email = email,
                    password = password,
                    fullName = fullName,
                    fakultas = selectedFakultas,
                    jurusan = selectedJurusan,
                    angkatan = selectedAngkatan
                )) {
                    is AuthenticationResult.Success -> {
                        setLoading(false)
                        Log.d("RegisterActivity", "Registration successful")
                        Toast.makeText(this@RegisterActivity, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                        navigateToMain()
                    }
                    is AuthenticationResult.Error -> {
                        setLoading(false)
                        Log.e("RegisterActivity", "Registration failed: ${result.message}")
                        Toast.makeText(this@RegisterActivity, result.message, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                setLoading(false)
                Log.e("RegisterActivity", "Exception during registration", e)
                Toast.makeText(
                    this@RegisterActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
        binding.tvLogin.isEnabled = !isLoading
        binding.spinnerFakultas.isEnabled = !isLoading
        binding.spinnerJurusan.isEnabled = !isLoading && selectedFakultas.isNotEmpty()
        binding.spinnerAngkatan.isEnabled = !isLoading
        binding.etFullName.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.etConfirmPassword.isEnabled = !isLoading
    }

    private fun navigateToMain() {
        try {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } catch (e: Exception) {
            Log.e("RegisterActivity", "Error navigating to main", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
