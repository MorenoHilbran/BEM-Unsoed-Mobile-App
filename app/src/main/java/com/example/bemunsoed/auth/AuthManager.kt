package com.example.bemunsoed.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthManager {
    private val auth: FirebaseAuth
    private val firestore: FirebaseFirestore

    init {
        try {
            auth = FirebaseAuth.getInstance()
            firestore = FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            throw RuntimeException("Firebase initialization failed: ${e.message}")
        }
    }

    // Check if user is logged in
    fun isUserLoggedIn(): Boolean {
        return try {
            auth.currentUser != null
        } catch (e: Exception) {
            false
        }
    }

    // Get current user
    fun getCurrentUser(): FirebaseUser? {
        return try {
            auth.currentUser
        } catch (e: Exception) {
            null
        }
    }

    // Register with email and password
    suspend fun registerWithEmail(
        email: String,
        password: String,
        fullName: String,
        fakultas: String,
        jurusan: String,
        angkatan: String
    ): AuthenticationResult {
        return try {
            val result: AuthResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                // Save user data with correct field names matching User model
                val userData = hashMapOf(
                    "id" to user.uid,
                    "name" to fullName,
                    "email" to email,
                    "fakultas" to fakultas,
                    "jurusan" to jurusan,
                    "angkatan" to angkatan,
                    "avatarUrl" to "",
                    "bio" to "Mahasiswa Universitas Jenderal Soedirman",
                    "createdAt" to System.currentTimeMillis()
                )

                try {
                    firestore.collection("users").document(user.uid).set(userData).await()
                    println("User profile saved to Firestore: ${user.uid}")
                } catch (e: Exception) {
                    // Firestore error, but auth success - continue anyway
                    println("Firestore error: ${e.message}")
                }

                AuthenticationResult.Success(user)
            } else {
                AuthenticationResult.Error("Registration failed")
            }
        } catch (e: Exception) {
            AuthenticationResult.Error(e.message ?: "Registration failed")
        }
    }

    // Login with email and password
    suspend fun loginWithEmail(email: String, password: String): AuthenticationResult {
        return try {
            val result: AuthResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                // Check if user profile exists in Firestore
                try {
                    val userDoc = firestore.collection("users").document(user.uid).get().await()
                    if (!userDoc.exists()) {
                        // User dihapus dari Firestore - logout dan minta register ulang
                        auth.signOut()
                        return AuthenticationResult.Error(
                            "Akun Anda telah dihapus dari sistem. Silakan daftar ulang untuk menggunakan aplikasi."
                        )
                    }
                    // User profile exists - login successful
                } catch (e: Exception) {
                    println("Error checking user profile: ${e.message}")
                    // If error checking Firestore, still allow login but log the error
                }

                AuthenticationResult.Success(user)
            } else {
                AuthenticationResult.Error("Login failed")
            }
        } catch (e: Exception) {
            AuthenticationResult.Error(e.message ?: "Login failed")
        }
    }

    // Anonymous login untuk akses public data
    suspend fun signInAnonymously(): AuthenticationResult {
        return try {
            val result = auth.signInAnonymously().await()
            if (result.user != null) {
                AuthenticationResult.Success(result.user!!)
            } else {
                AuthenticationResult.Error("Anonymous login failed")
            }
        } catch (e: Exception) {
            AuthenticationResult.Error("Anonymous login error: ${e.message}")
        }
    }

    // Logout
    fun logout() {
        auth.signOut()
    }

    // Reset password
    suspend fun resetPassword(email: String): AuthenticationResult {
        return try {
            auth.sendPasswordResetEmail(email).await()
            AuthenticationResult.Success(null)
        } catch (e: Exception) {
            AuthenticationResult.Error(e.message ?: "Failed to send reset email")
        }
    }
}

// Result wrapper for authentication operations (renamed to avoid conflict with Firebase AuthResult)
sealed class AuthenticationResult {
    data class Success(val user: FirebaseUser?) : AuthenticationResult()
    data class Error(val message: String) : AuthenticationResult()
}
