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
        nim: String,
        major: String
    ): AuthenticationResult {
        return try {
            val result: AuthResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                // Save additional user data to Firestore
                val userData = hashMapOf(
                    "uid" to user.uid,
                    "email" to email,
                    "fullName" to fullName,
                    "nim" to nim,
                    "major" to major,
                    "profilePictureUrl" to "",
                    "createdAt" to System.currentTimeMillis()
                )

                try {
                    firestore.collection("users").document(user.uid).set(userData).await()
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
                AuthenticationResult.Success(user)
            } else {
                AuthenticationResult.Error("Login failed")
            }
        } catch (e: Exception) {
            AuthenticationResult.Error(e.message ?: "Login failed")
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
