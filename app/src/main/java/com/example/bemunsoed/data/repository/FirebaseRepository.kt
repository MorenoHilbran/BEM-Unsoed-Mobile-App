package com.example.bemunsoed.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import android.net.Uri
import com.example.bemunsoed.data.model.Event
import com.example.bemunsoed.data.model.Merch
import com.example.bemunsoed.data.model.Post
import com.example.bemunsoed.data.model.PostComment
import com.example.bemunsoed.data.model.Comment
import com.example.bemunsoed.data.model.Like
import com.example.bemunsoed.data.model.User
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class FirebaseRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

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

    // Get merchandise items - needs auth
    suspend fun getMerchandise(): Result<List<Merch>> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            Log.d("FirebaseRepository", "Fetching merchandise...")
            val snapshot = firestore.collection("merch")
                .whereEqualTo("isActive", true)
                .get()
                .await()

            val merchandise = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Merch::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e("FirebaseRepository", "Error parsing merch document: ${doc.id}", e)
                    null
                }
            }
            Log.d("FirebaseRepository", "Found ${merchandise.size} merchandise items")
            Result.success(merchandise)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error fetching merchandise", e)
            Result.success(emptyList())
        }
    }

    // FORUM/POSTS FUNCTIONS

    // Get all posts for forum
    suspend fun getPosts(): Result<List<Post>> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            Log.d("FirebaseRepository", "Fetching posts...")
            val snapshot = firestore.collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d("FirebaseRepository", "Raw snapshot size: ${snapshot.size()}")

            val posts = snapshot.documents.mapNotNull { doc ->
                try {
                    Log.d("FirebaseRepository", "Processing post doc: ${doc.id}")
                    val post = doc.toObject(Post::class.java)?.copy(id = doc.id)
                    if (post != null) {
                        Log.d("FirebaseRepository", "Post parsed: id=${post.id}, author=${post.authorName}, content=${post.content.take(50)}")
                    } else {
                        Log.w("FirebaseRepository", "Post is null for doc: ${doc.id}")
                    }
                    post
                } catch (e: Exception) {
                    Log.e("FirebaseRepository", "Error parsing post document: ${doc.id}", e)
                    null
                }
            }
            Log.d("FirebaseRepository", "Found ${posts.size} posts after parsing")
            Result.success(posts)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error fetching posts", e)
            Result.success(emptyList())
        }
    }

    // Get posts by user ID
    suspend fun getUserPosts(userId: String): Result<List<Post>> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            Log.d("FirebaseRepository", "Fetching posts for user: $userId")
            val snapshot = firestore.collection("posts")
                .whereEqualTo("authorId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val posts = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Post::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e("FirebaseRepository", "Error parsing post document: ${doc.id}", e)
                    null
                }
            }
            Log.d("FirebaseRepository", "Found ${posts.size} posts for user")
            Result.success(posts)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error fetching user posts", e)
            Result.success(emptyList())
        }
    }

    // Get comments by user ID
    suspend fun getUserComments(userId: String): Result<List<Comment>> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            Log.d("FirebaseRepository", "Fetching comments for user: $userId")

            // Temporarily remove orderBy to avoid composite index requirement
            // We'll sort in memory instead
            val snapshot = firestore.collection("comments")
                .whereEqualTo("authorId", userId)
                .limit(50) // Get more documents to sort them properly
                .get()
                .await()

            val comments = snapshot.documents.mapNotNull { doc ->
                try {
                    val postComment = doc.toObject(PostComment::class.java)?.copy(id = doc.id)
                    postComment?.let {
                        Comment(
                            id = it.id,
                            postId = it.postId,
                            authorId = it.authorId,
                            authorName = it.authorName,
                            authorUsername = it.authorName,
                            authorProfilePhotoId = it.authorProfilePhotoId, // Map profile photo ID
                            content = it.content,
                            createdAt = it.createdAt,
                            likeCount = it.likeCount,
                            isLiked = false,
                            likedBy = it.likedBy
                        )
                    }
                } catch (e: Exception) {
                    Log.e("FirebaseRepository", "Error parsing comment document: ${doc.id}", e)
                    null
                }
            }
            // Sort in memory by createdAt descending and take only 10 latest
            .sortedByDescending { it.createdAt }
            .take(10)

            Log.d("FirebaseRepository", "Found ${comments.size} comments for user")
            Result.success(comments)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error fetching user comments", e)
            Result.success(emptyList())
        }
    }

    // Create new post
    suspend fun createPost(post: Post): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.e("FirebaseRepository", "Cannot create post - user not authenticated")
                return@withContext Result.failure(Exception("Please sign in to create a post"))
            }

            // Get current user profile for author info
            Log.d("FirebaseRepository", "Fetching user profile for post creation...")
            val userProfile = getUserProfile(currentUser.uid).getOrNull()
            val authorName = userProfile?.name?.takeIf { it.isNotEmpty() } ?: currentUser.email?.substringBefore("@") ?: "BEM User"

            Log.d("FirebaseRepository", "Creating post with author: $authorName (userId: ${currentUser.uid})")

            val postData = post.copy(
                authorId = currentUser.uid,
                authorName = authorName,
                createdAt = System.currentTimeMillis(),
                likeCount = 0,
                commentCount = 0,
                isLiked = false,
                likedBy = emptyList()
            )

            val docRef = firestore.collection("posts").add(postData).await()
            Log.d("FirebaseRepository", "Post created successfully with ID: ${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error creating post", e)
            Result.failure(Exception("Failed to create post: ${e.message}"))
        }
    }

    // Update post like status
    suspend fun updatePostLike(postId: String, userId: String, isLiked: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            Log.d("FirebaseRepository", "Updating like status for post: $postId")

            val postRef = firestore.collection("posts").document(postId)
            val likeRef = firestore.collection("posts").document(postId)
                .collection("likes").document(userId)

            if (isLiked) {
                // Add like
                likeRef.set(mapOf("userId" to userId, "timestamp" to Timestamp.now())).await()
                // Increment like count
                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(postRef)
                    val currentLikes = snapshot.getLong("likeCount") ?: 0
                    transaction.update(postRef, "likeCount", currentLikes + 1)
                }.await()
            } else {
                // Remove like
                likeRef.delete().await()
                // Decrement like count
                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(postRef)
                    val currentLikes = snapshot.getLong("likeCount") ?: 0
                    transaction.update(postRef, "likeCount", maxOf(0, currentLikes - 1))
                }.await()
            }

            Log.d("FirebaseRepository", "Like status updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error updating like status", e)
            Result.failure(e)
        }
    }

    // USER PROFILE FUNCTIONS

    // Get user profile by ID
    suspend fun getUserProfile(userId: String): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            Log.d("FirebaseRepository", "Fetching user profile: $userId")
            val snapshot = firestore.collection("users").document(userId).get().await()

            if (snapshot.exists()) {
                val user = snapshot.toObject(User::class.java)?.copy(id = snapshot.id)
                if (user != null) {
                    Log.d("FirebaseRepository", "User profile found")
                    Result.success(user)
                } else {
                    Log.e("FirebaseRepository", "Failed to parse user data")
                    Result.failure(Exception("Failed to parse user data"))
                }
            } else {
                Log.e("FirebaseRepository", "User not found")
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error fetching user profile", e)
            Result.failure(e)
        }
    }

    // Get current user profile
    suspend fun getCurrentUserProfile(): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return@withContext Result.failure(Exception("User not authenticated"))
            }
            getUserProfile(currentUser.uid)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error getting current user profile", e)
            Result.failure(e)
        }
    }

    // Update user profile
    suspend fun updateUserProfile(user: User): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return@withContext Result.failure(Exception("User not authenticated"))
            }

            Log.d("FirebaseRepository", "Updating user profile: ${currentUser.uid}")
            firestore.collection("users").document(currentUser.uid).set(user).await()
            Log.d("FirebaseRepository", "User profile updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error updating user profile", e)
            Result.failure(e)
        }
    }

    // Update profile photo ID in all user's posts when profile photo is changed
    suspend fun updateUserPostsProfilePhoto(userId: String, newProfilePhotoId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("FirebaseRepository", "Updating profile photo ID in all posts for user: $userId")

            // Get all posts by this user
            val postsSnapshot = firestore.collection("posts")
                .whereEqualTo("authorId", userId)
                .get()
                .await()

            // Batch update all posts
            val batch = firestore.batch()
            for (document in postsSnapshot.documents) {
                batch.update(document.reference, "authorProfilePhotoId", newProfilePhotoId)
            }

            // Execute batch update
            batch.commit().await()

            Log.d("FirebaseRepository", "Successfully updated profile photo ID in ${postsSnapshot.size()} posts")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error updating profile photo ID in posts", e)
            Result.failure(e)
        }
    }

    // Update profile photo ID in all user's comments when profile photo is changed
    suspend fun updateUserCommentsProfilePhoto(userId: String, newProfilePhotoId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("FirebaseRepository", "Updating profile photo ID in all comments for user: $userId")

            // Get all comments by this user
            val commentsSnapshot = firestore.collection("comments")
                .whereEqualTo("authorId", userId)
                .get()
                .await()

            // Batch update all comments
            val batch = firestore.batch()
            for (document in commentsSnapshot.documents) {
                batch.update(document.reference, "authorProfilePhotoId", newProfilePhotoId)
            }

            // Execute batch update
            batch.commit().await()

            Log.d("FirebaseRepository", "Successfully updated profile photo ID in ${commentsSnapshot.size()} comments")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error updating profile photo ID in comments", e)
            Result.failure(e)
        }
    }

    // LIKE SYSTEM FUNCTIONS

    // Toggle like on a post
    suspend fun togglePostLike(postId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return@withContext Result.failure(Exception("User not authenticated"))
            }

            val userId = currentUser.uid
            Log.d("FirebaseRepository", "Toggling like for post: $postId, user: $userId")

            val postRef = firestore.collection("posts").document(postId)
            val likeRef = firestore.collection("likes").document("${postId}_${userId}")

            // Check if already liked
            val likeSnapshot = likeRef.get().await()
            val isCurrentlyLiked = likeSnapshot.exists()

            // Get post data for notification
            val postSnapshot = postRef.get().await()
            val post = postSnapshot.toObject(Post::class.java)?.copy(id = postSnapshot.id)

            firestore.runTransaction { transaction ->
                val postSnapshotTx = transaction.get(postRef)
                val currentLikeCount = postSnapshotTx.getLong("likeCount") ?: 0
                @Suppress("UNCHECKED_CAST")
                val currentLikedBy = (postSnapshotTx.get("likedBy") as? List<*>)?.filterIsInstance<String>() ?: emptyList()

                if (isCurrentlyLiked) {
                    // Remove like
                    transaction.delete(likeRef)
                    transaction.update(postRef, mapOf(
                        "likeCount" to maxOf(0, currentLikeCount - 1),
                        "likedBy" to currentLikedBy.filter { it != userId }
                    ))
                } else {
                    // Add like
                    val like = Like(
                        id = "${postId}_${userId}",
                        userId = userId,
                        postId = postId,
                        createdAt = System.currentTimeMillis()
                    )
                    transaction.set(likeRef, like)
                    transaction.update(postRef, mapOf(
                        "likeCount" to currentLikeCount + 1,
                        "likedBy" to currentLikedBy + userId
                    ))
                }
            }.await()

            val newLikedStatus = !isCurrentlyLiked

            // Notification creation is handled in DashboardViewModel to avoid duplication
            // Removed duplicate notification creation from here

            Log.d("FirebaseRepository", "Like toggled successfully. New status: $newLikedStatus")
            Result.success(newLikedStatus)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error toggling like", e)
            Result.failure(e)
        }
    }

    // Check if user liked a post
    suspend fun isPostLikedByUser(postId: String, userId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            val likeRef = firestore.collection("likes").document("${postId}_${userId}")
            val snapshot = likeRef.get().await()
            Result.success(snapshot.exists())
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error checking like status", e)
            Result.success(false)
        }
    }

    // COMMENT SYSTEM FUNCTIONS

    // Get comments for a post
    suspend fun getPostComments(postId: String): Result<List<PostComment>> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            Log.d("FirebaseRepository", "Fetching comments for post: $postId")

            val snapshot = firestore.collection("comments")
                .whereEqualTo("postId", postId)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .await()

            val comments = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(PostComment::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e("FirebaseRepository", "Error parsing comment document: ${doc.id}", e)
                    null
                }
            }

            Log.d("FirebaseRepository", "Found ${comments.size} comments")
            Result.success(comments)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error fetching comments", e)
            Result.success(emptyList())
        }
    }

    // Add comment to a post
    suspend fun addComment(postId: String, content: String): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return@withContext Result.failure(Exception("User not authenticated"))
            }

            // Get current user profile for author info including profile photo ID
            val userProfile = getUserProfile(currentUser.uid).getOrNull()
            val authorName = userProfile?.name ?: "Anonymous User"
            val authorAvatar = userProfile?.avatarUrl ?: ""
            val authorProfilePhotoId = userProfile?.profilePhotoId ?: "default"

            val comment = PostComment(
                postId = postId,
                content = content,
                authorId = currentUser.uid,
                authorName = authorName,
                authorAvatar = authorAvatar,
                authorProfilePhotoId = authorProfilePhotoId, // Include profile photo ID
                createdAt = System.currentTimeMillis()
            )

            Log.d("FirebaseRepository", "Adding comment to post: $postId")

            // Add comment and update post comment count in transaction
            val commentRef = firestore.collection("comments").document()
            val postRef = firestore.collection("posts").document(postId)

            firestore.runTransaction { transaction ->
                val postSnapshot = transaction.get(postRef)
                val currentCommentCount = postSnapshot.getLong("commentCount") ?: 0

                transaction.set(commentRef, comment.copy(id = commentRef.id))
                transaction.update(postRef, "commentCount", currentCommentCount + 1)
            }.await()

            Log.d("FirebaseRepository", "Comment added with ID: ${commentRef.id}")
            Result.success(commentRef.id)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error adding comment", e)
            Result.failure(e)
        }
    }

    // Toggle like on a comment
    suspend fun toggleCommentLike(commentId: String, postId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return@withContext Result.failure(Exception("User not authenticated"))
            }

            val userId = currentUser.uid
            Log.d("FirebaseRepository", "Toggling like for comment: $commentId, user: $userId")

            val commentRef = firestore.collection("comments").document(commentId)
            val likeRef = firestore.collection("likes").document("${commentId}_${userId}")

            // Check if already liked
            val likeSnapshot = likeRef.get().await()
            val isCurrentlyLiked = likeSnapshot.exists()

            firestore.runTransaction { transaction ->
                val commentSnapshot = transaction.get(commentRef)
                val currentLikeCount = commentSnapshot.getLong("likeCount") ?: 0
                @Suppress("UNCHECKED_CAST")
                val currentLikedBy = (commentSnapshot.get("likedBy") as? List<*>)?.filterIsInstance<String>() ?: emptyList()

                if (isCurrentlyLiked) {
                    // Remove like
                    transaction.delete(likeRef)
                    transaction.update(commentRef, mapOf(
                        "likeCount" to maxOf(0, currentLikeCount - 1),
                        "likedBy" to currentLikedBy.filter { it != userId }
                    ))
                } else {
                    // Add like
                    val like = Like(
                        id = "${commentId}_${userId}",
                        userId = userId,
                        commentId = commentId,
                        createdAt = System.currentTimeMillis()
                    )
                    transaction.set(likeRef, like)
                    transaction.update(commentRef, mapOf(
                        "likeCount" to currentLikeCount + 1,
                        "likedBy" to currentLikedBy + userId
                    ))
                }
            }.await()

            val newLikedStatus = !isCurrentlyLiked
            Log.d("FirebaseRepository", "Comment like toggled successfully. New status: $newLikedStatus")
            Result.success(newLikedStatus)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error toggling comment like", e)
            Result.failure(e)
        }
    }

    // Get posts with like status for current user
    suspend fun getPostsWithLikeStatus(): Result<List<Post>> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return@withContext Result.failure(Exception("User not authenticated"))
            }

            Log.d("FirebaseRepository", "Fetching posts with like status...")
            val postsResult = getPosts()
            val posts = postsResult.getOrNull() ?: return@withContext Result.success(emptyList())

            // Check like status for each post
            val postsWithLikeStatus = posts.map { post ->
                val isLiked = isPostLikedByUser(post.id, currentUser.uid).getOrNull() ?: false
                post.copy(isLiked = isLiked)
            }

            Log.d("FirebaseRepository", "Posts with like status retrieved: ${postsWithLikeStatus.size}")
            Result.success(postsWithLikeStatus)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error fetching posts with like status", e)
            Result.success(emptyList())
        }
    }

    // Get single post by ID with like status
    suspend fun getPostById(postId: String): Result<Post> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return@withContext Result.failure(Exception("User not authenticated"))
            }

            Log.d("FirebaseRepository", "Fetching post by ID: $postId")
            val snapshot = firestore.collection("posts").document(postId).get().await()

            if (snapshot.exists()) {
                val post = snapshot.toObject(Post::class.java)?.copy(id = snapshot.id)
                if (post != null) {
                    // Check if user liked this post
                    val isLiked = isPostLikedByUser(postId, currentUser.uid).getOrNull() ?: false
                    val postWithLikeStatus = post.copy(isLiked = isLiked)
                    Log.d("FirebaseRepository", "Post found: ${post.authorName}")
                    Result.success(postWithLikeStatus)
                } else {
                    Result.failure(Exception("Failed to parse post data"))
                }
            } else {
                Result.failure(Exception("Post not found"))
            }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error fetching post by ID", e)
            Result.failure(e)
        }
    }

    // Get comments for a post (using Comment model)
    suspend fun getComments(postId: String): Result<List<Comment>> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return@withContext Result.failure(Exception("User not authenticated"))
            }

            Log.d("FirebaseRepository", "Fetching comments for post: $postId")

            val snapshot = firestore.collection("comments")
                .whereEqualTo("postId", postId)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .await()

            val comments = snapshot.documents.mapNotNull { doc ->
                try {
                    val postComment = doc.toObject(PostComment::class.java)?.copy(id = doc.id)
                    postComment?.let {
                        // Convert PostComment to Comment and check like status
                        val isLiked = isCommentLikedByUser(it.id, currentUser.uid).getOrNull() ?: false
                        Comment(
                            id = it.id,
                            postId = it.postId,
                            authorId = it.authorId,
                            authorName = it.authorName,
                            authorUsername = it.authorName, // Use same as name for now
                            authorProfilePhotoId = it.authorProfilePhotoId, // Map profile photo ID
                            content = it.content,
                            createdAt = it.createdAt,
                            likeCount = it.likedBy?.size ?: 0,
                            isLiked = isLiked,
                            likedBy = it.likedBy
                        )
                    }
                } catch (e: Exception) {
                    Log.e("FirebaseRepository", "Error parsing comment document: ${doc.id}", e)
                    null
                }
            }

            Log.d("FirebaseRepository", "Found ${comments.size} comments")
            Result.success(comments)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error fetching comments", e)
            Result.success(emptyList())
        }
    }

    // Check if user liked a comment
    suspend fun isCommentLikedByUser(commentId: String, userId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            val likeRef = firestore.collection("likes").document("${commentId}_${userId}")
            val snapshot = likeRef.get().await()
            Result.success(snapshot.exists())
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error checking comment like status", e)
            Result.success(false)
        }
    }

    // Get current user ID
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // Create notification when someone likes or comments on a post
    suspend fun createNotification(notification: com.example.bemunsoed.data.model.Notification): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            Log.d("FirebaseRepository", "Creating notification for user: ${notification.recipientId}")

            // Don't create notification if user is acting on their own post
            if (notification.actorId == notification.recipientId) {
                Log.d("FirebaseRepository", "Skipping notification - user acting on own post")
                return@withContext Result.success("skipped")
            }

            val notificationRef = firestore.collection("notifications").document()
            val notificationData = notification.copy(id = notificationRef.id)

            notificationRef.set(notificationData).await()
            Log.d("FirebaseRepository", "Notification created with ID: ${notificationRef.id}")
            Result.success(notificationRef.id)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error creating notification", e)
            Result.failure(e)
        }
    }

    // Get notifications for current user
    suspend fun getNotifications(userId: String): Result<List<com.example.bemunsoed.data.model.Notification>> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            Log.d("FirebaseRepository", "Fetching notifications for user: $userId")

            val snapshot = firestore.collection("notifications")
                .whereEqualTo("recipientId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()

            val notifications = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(com.example.bemunsoed.data.model.Notification::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e("FirebaseRepository", "Error parsing notification document: ${doc.id}", e)
                    null
                }
            }.distinctBy { it.id } // Remove duplicates based on ID

            Log.d("FirebaseRepository", "Found ${notifications.size} unique notifications")
            Result.success(notifications)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error fetching notifications", e)
            Result.success(emptyList())
        }
    }

    // Get unread notification count
    suspend fun getUnreadNotificationCount(userId: String): Result<Int> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            val snapshot = firestore.collection("notifications")
                .whereEqualTo("recipientId", userId)
                .whereEqualTo("isRead", false)
                .get()
                .await()

            Result.success(snapshot.size())
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error fetching unread count", e)
            Result.success(0)
        }
    }

    // Mark notification as read
    suspend fun markNotificationAsRead(notificationId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            firestore.collection("notifications")
                .document(notificationId)
                .update("isRead", true)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error marking notification as read", e)
            Result.failure(e)
        }
    }

    // Mark all notifications as read
    suspend fun markAllNotificationsAsRead(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            Log.d("FirebaseRepository", "Fetching unread notifications for user: $userId")

            val snapshot = firestore.collection("notifications")
                .whereEqualTo("recipientId", userId)
                .whereEqualTo("isRead", false)
                .get()
                .await()

            Log.d("FirebaseRepository", "Found ${snapshot.documents.size} unread notifications to mark as read")

            if (snapshot.documents.isEmpty()) {
                Log.d("FirebaseRepository", "No notifications to mark as read")
                return@withContext Result.success(Unit)
            }

            // Update each notification individually to ensure they are all saved
            var successCount = 0
            var failCount = 0

            snapshot.documents.forEach { doc ->
                try {
                    Log.d("FirebaseRepository", "Updating notification ${doc.id} to isRead=true")
                    firestore.collection("notifications")
                        .document(doc.id)
                        .update("isRead", true)
                        .await()
                    successCount++
                    Log.d("FirebaseRepository", "Successfully updated notification ${doc.id}")
                } catch (e: Exception) {
                    failCount++
                    Log.e("FirebaseRepository", "Failed to update notification ${doc.id}", e)
                }
            }

            Log.d("FirebaseRepository", "Mark all as read completed: $successCount succeeded, $failCount failed")

            if (successCount > 0) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to update any notifications"))
            }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error marking all notifications as read", e)
            Result.failure(e)
        }
    }

    // Delete post (only owner can delete) - SIMPLIFIED: skip notifications
    suspend fun deletePost(postId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return@withContext Result.failure(Exception("User not authenticated"))
            }

            Log.d("FirebaseRepository", "Deleting post: $postId")

            // Get post to verify ownership
            val postRef = firestore.collection("posts").document(postId)
            val postSnapshot = postRef.get().await()

            if (!postSnapshot.exists()) {
                return@withContext Result.failure(Exception("Post not found"))
            }

            val post = postSnapshot.toObject(Post::class.java)
            if (post?.authorId != currentUser.uid) {
                return@withContext Result.failure(Exception("You can only delete your own posts"))
            }

            val batch = firestore.batch()

            // Delete all likes in subcollection posts/{postId}/likes
            try {
                val postLikesSnapshot = firestore.collection("posts")
                    .document(postId)
                    .collection("likes")
                    .get()
                    .await()

                postLikesSnapshot.documents.forEach { doc ->
                    batch.delete(doc.reference)
                }
                Log.d("FirebaseRepository", "Marked ${postLikesSnapshot.size()} post likes for deletion")
            } catch (e: Exception) {
                Log.w("FirebaseRepository", "Error deleting post likes subcollection", e)
            }

            // Delete all root-level likes for this post
            try {
                val rootLikesSnapshot = firestore.collection("likes")
                    .whereEqualTo("postId", postId)
                    .get()
                    .await()

                rootLikesSnapshot.documents.forEach { doc ->
                    batch.delete(doc.reference)
                }
                Log.d("FirebaseRepository", "Marked ${rootLikesSnapshot.size()} root likes for deletion")
            } catch (e: Exception) {
                Log.w("FirebaseRepository", "Error deleting root likes", e)
            }

            // TIDAK MENGHAPUS COMMENTS - biarkan komentar tetap ada sebagai standalone
            // Comments akan tetap bisa dilihat di Recent Comments section
            // User masih bisa menghapus komentar mereka sendiri jika mau
            Log.d("FirebaseRepository", "Comments will remain as standalone (not deleted with post)")

            // SKIP DELETE NOTIFICATIONS - akan otomatis jadi orphaned data
            // Notifications akan tetap ada tapi post sudah tidak ada
            // User bisa clear notifikasi mereka sendiri nanti
            Log.d("FirebaseRepository", "Skipping notifications cleanup (orphaned notifications will remain)")

            // Delete the post itself
            batch.delete(postRef)

            // Commit all deletes in one batch
            batch.commit().await()

            Log.d("FirebaseRepository", "Post deleted successfully (comments and notifications preserved)")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error deleting post", e)
            Result.failure(e)
        }
    }

    // Delete comment (owner comment atau owner post bisa delete)
    suspend fun deleteComment(commentId: String, postId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            ensureAuthIfNeeded()
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return@withContext Result.failure(Exception("User not authenticated"))
            }

            Log.d("FirebaseRepository", "Deleting comment: $commentId")

            // Get comment to verify ownership
            val commentRef = firestore.collection("comments").document(commentId)
            val commentSnapshot = commentRef.get().await()

            if (!commentSnapshot.exists()) {
                return@withContext Result.failure(Exception("Comment not found"))
            }

            val comment = commentSnapshot.toObject(PostComment::class.java)
            val isCommentOwner = comment?.authorId == currentUser.uid

            // Check if user is post owner
            val postRef = firestore.collection("posts").document(postId)
            val postSnapshot = postRef.get().await()
            val isPostOwner = postSnapshot.exists() &&
                             postSnapshot.toObject(Post::class.java)?.authorId == currentUser.uid

            // User harus comment owner ATAU post owner
            if (!isCommentOwner && !isPostOwner) {
                return@withContext Result.failure(Exception("You can only delete your own comments or comments on your post"))
            }

            // Delete comment
            commentRef.delete().await()

            // Update post comment count (jika post masih ada)
            try {
                if (postSnapshot.exists()) {
                    val currentCount = postSnapshot.getLong("commentCount") ?: 0
                    postRef.update("commentCount", maxOf(0, currentCount - 1)).await()
                }
            } catch (e: Exception) {
                Log.w("FirebaseRepository", "Failed to update comment count, post may not exist", e)
            }

            Log.d("FirebaseRepository", "Comment deleted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error deleting comment", e)
            Result.failure(e)
        }
    }

    // Upload image to Firebase Storage
    suspend fun uploadImage(uri: Uri, path: String): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.e("FirebaseRepository", "User not authenticated for image upload")
                return@withContext Result.failure(Exception("User not authenticated"))
            }

            Log.d("FirebaseRepository", "Starting image upload to path: $path")
            Log.d("FirebaseRepository", "Image URI: $uri")

            val storageRef = storage.reference.child(path)
            
            // Upload file with metadata
            val uploadTask = storageRef.putFile(uri)
            
            // Add listeners for upload progress
            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                Log.d("FirebaseRepository", "Upload progress: $progress%")
            }.addOnFailureListener { exception ->
                Log.e("FirebaseRepository", "Upload failed during transfer", exception)
            }
            
            // Wait for upload to complete
            val uploadResult = uploadTask.await()
            Log.d("FirebaseRepository", "Upload task completed successfully")

            // Get the download URL
            val downloadUrl = storageRef.downloadUrl.await().toString()
            Log.d("FirebaseRepository", "Download URL obtained: $downloadUrl")

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error uploading image: ${e.message}", e)
            Log.e("FirebaseRepository", "Exception type: ${e.javaClass.simpleName}")
            Result.failure(Exception("Failed to upload image: ${e.message}"))
        }
    }
}