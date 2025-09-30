package com.example.bemunsoed.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.example.bemunsoed.data.model.Event
import com.example.bemunsoed.data.model.Merch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

class FirebaseRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Simplified auth - only for collections that need it
    private suspend fun ensureAuthIfNeeded() {
        if (auth.currentUser == null) {
            try {
                Log.d("FirebaseRepository", "Signing in anonymously for data access...")
                auth.signInAnonymously().await()
                Log.d("FirebaseRepository", "Anonymous sign-in successful")
            } catch (e: Exception) {
                Log.w("FirebaseRepository", "Anonymous sign-in failed, continuing without auth", e)
                // Continue without auth for public data
            }
        }
    }

    // Get highlight events - public data
    suspend fun getHighlightEvents(): Result<List<Event>> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("FirebaseRepository", "Fetching highlight events...")
            val snapshot = firestore.collection("events")
                .whereEqualTo("isHighlight", true)
                .whereEqualTo("status", "published")
                .limit(5)
                .get()
                .await()

            val events = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Event::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e("FirebaseRepository", "Error parsing event document: ${doc.id}", e)
                    null
                }
            }
            Log.d("FirebaseRepository", "Found ${events.size} events")
            Result.success(events)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error fetching events", e)
            // Try with auth as fallback
            try {
                ensureAuthIfNeeded()
                val snapshot = firestore.collection("events")
                    .whereEqualTo("isHighlight", true)
                    .whereEqualTo("status", "published")
                    .limit(5)
                    .get()
                    .await()

                val events = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(Event::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                }
                Result.success(events)
            } catch (fallbackError: Exception) {
                Log.e("FirebaseRepository", "Fallback also failed", fallbackError)
                Result.success(emptyList()) // Return empty list instead of error
            }
        }
    }

    // Get all events - public data
    suspend fun getAllEvents(): Result<List<Event>> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("FirebaseRepository", "Fetching all events...")
            val snapshot = firestore.collection("events")
                .whereEqualTo("status", "published")
                .get()
                .await()

            val events = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Event::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    null
                }
            }
            Log.d("FirebaseRepository", "Found ${events.size} total events")
            Result.success(events)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error fetching all events", e)
            Result.success(emptyList())
        }
    }

    // Get merchandise - public data
    suspend fun getMerch(): Result<List<Merch>> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("FirebaseRepository", "Fetching merchandise...")
            val snapshot = firestore.collection("merch")
                .whereEqualTo("isActive", true)
                .limit(10)
                .get()
                .await()

            val merchList = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Merch::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    null
                }
            }
            Log.d("FirebaseRepository", "Found ${merchList.size} merchandise items")
            Result.success(merchList)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error fetching merchandise", e)
            Result.success(emptyList())
        }
    }

    // Get banners - public data
    suspend fun getBanners(): Result<List<Map<String, Any>>> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("FirebaseRepository", "Fetching banners...")
            val snapshot = firestore.collection("banners")
                .whereEqualTo("isActive", true)
                .get()
                .await()

            val banners = snapshot.documents.map { doc ->
                mapOf(
                    "id" to doc.id,
                    "title" to (doc.getString("title") ?: ""),
                    "imageUrl" to (doc.getString("imageUrl") ?: ""),
                    "linkUrl" to (doc.getString("linkUrl") ?: ""),
                    "order" to (doc.getLong("order") ?: 0)
                )
            }.sortedBy { it["order"] as Long }

            Log.d("FirebaseRepository", "Found ${banners.size} banners")
            Result.success(banners)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error fetching banners", e)
            Result.success(emptyList())
        }
    }
}
