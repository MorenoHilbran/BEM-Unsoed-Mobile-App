package com.example.bemunsoed.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.bemunsoed.data.repository.FirebaseRepository
import com.example.bemunsoed.data.model.Post
import com.example.bemunsoed.data.model.Comment
import com.example.bemunsoed.data.model.Notification
import com.example.bemunsoed.data.model.NotificationType
import android.util.Log
import com.example.bemunsoed.data.model.User

class DashboardViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    // Posts data
    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    // Current post detail
    private val _currentPost = MutableLiveData<Post?>()
    val currentPost: LiveData<Post?> = _currentPost

    // Comments for current post
    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> = _comments

    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Error messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Create post result
    private val _createPostResult = MutableLiveData<Result<String>?>()
    val createPostResult: LiveData<Result<String>?> = _createPostResult

    // Like result
    private val _likeResult = MutableLiveData<Result<Boolean>?>()
    val likeResult: LiveData<Result<Boolean>?> = _likeResult

    // Comment result
    private val _commentResult = MutableLiveData<Result<String>?>()
    val commentResult: LiveData<Result<String>?> = _commentResult

    // Delete post result
    private val _deletePostResult = MutableLiveData<Result<Unit>?>()
    val deletePostResult: LiveData<Result<Unit>?> = _deletePostResult

    // Delete comment result
    private val _deleteCommentResult = MutableLiveData<Result<Unit>?>()
    val deleteCommentResult: LiveData<Result<Unit>?> = _deleteCommentResult

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("DashboardViewModel", "Loading posts...")

                val result = repository.getPostsWithLikeStatus()
                result.fold(
                    onSuccess = { posts ->
                        Log.d("DashboardViewModel", "Posts loaded successfully: ${posts.size}")
                        _posts.value = posts
                        _errorMessage.value = null
                    },
                    onFailure = { exception ->
                        Log.e("DashboardViewModel", "Failed to load posts", exception)
                        _errorMessage.value = "Failed to load posts: ${exception.message}"
                        _posts.value = emptyList()
                    }
                )
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error loading posts", e)
                _errorMessage.value = "Error loading posts: ${e.message}"
                _posts.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadPostDetail(postId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("DashboardViewModel", "Loading post detail: $postId")

                val result = repository.getPostById(postId)
                result.fold(
                    onSuccess = { post ->
                        Log.d("DashboardViewModel", "Post detail loaded successfully")
                        _currentPost.value = post
                    },
                    onFailure = { exception ->
                        Log.e("DashboardViewModel", "Failed to load post detail", exception)
                        _errorMessage.value = "Failed to load post: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error loading post detail", e)
                _errorMessage.value = "Error loading post: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadComments(postId: String) {
        viewModelScope.launch {
            try {
                Log.d("DashboardViewModel", "Loading comments for post: $postId")

                val result = repository.getComments(postId)
                result.fold(
                    onSuccess = { comments ->
                        Log.d("DashboardViewModel", "Comments loaded: ${comments.size}")
                        _comments.value = comments
                    },
                    onFailure = { exception ->
                        Log.e("DashboardViewModel", "Failed to load comments", exception)
                        _errorMessage.value = "Failed to load comments: ${exception.message}"
                        _comments.value = emptyList()
                    }
                )
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error loading comments", e)
                _errorMessage.value = "Error loading comments: ${e.message}"
                _comments.value = emptyList()
            }
        }
    }

    fun refreshPosts() {
        loadPosts()
    }

    fun createPost(content: String) {
        viewModelScope.launch {
            try {
                Log.d("DashboardViewModel", "Creating new post...")

                // Get current user info including profile photo ID
                val userResult = repository.getCurrentUserProfile()
                userResult.fold(
                    onSuccess = { user: User ->
                        val post = Post(
                            authorName = user.name.ifEmpty { "Anonymous User" },
                            authorId = repository.getCurrentUserId() ?: "",
                            authorProfilePhotoId = user.profilePhotoId, // Include profile photo ID
                            content = content,
                            createdAt = System.currentTimeMillis()
                        )

                        val result = repository.createPost(post)
                        _createPostResult.value = result

                        result.fold(
                            onSuccess = { postId: String ->
                                Log.d("DashboardViewModel", "Post created successfully with ID: $postId")
                                // Reload posts to show the new one
                                loadPosts()
                            },
                            onFailure = { exception: Throwable ->
                                Log.e("DashboardViewModel", "Failed to create post", exception)
                            }
                        )
                    },
                    onFailure = { exception: Throwable ->
                        Log.e("DashboardViewModel", "Failed to get user info for post creation", exception)
                        _createPostResult.value = Result.failure(exception)
                    }
                )
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error creating post", e)
                _createPostResult.value = Result.failure(e)
            }
        }
    }

    // Keep the original createPost function for backward compatibility
    fun createPost(post: Post) {
        viewModelScope.launch {
            try {
                Log.d("DashboardViewModel", "Creating new post...")
                val result = repository.createPost(post)
                _createPostResult.value = result

                result.fold(
                    onSuccess = { postId ->
                        Log.d("DashboardViewModel", "Post created successfully with ID: $postId")
                        // Reload posts to show the new one
                        loadPosts()
                    },
                    onFailure = { exception ->
                        Log.e("DashboardViewModel", "Failed to create post", exception)
                    }
                )
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error creating post", e)
                _createPostResult.value = Result.failure(e)
            }
        }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            try {
                Log.d("DashboardViewModel", "Toggling like for post: $postId")

                // Get current post to check author
                val currentPost = _currentPost.value ?: _posts.value?.find { it.id == postId }

                // Check if user is trying to like their own post
                val currentUserId = repository.getCurrentUserId()
                if (currentPost?.authorId == currentUserId) {
                    _errorMessage.value = "You cannot like your own post"
                    return@launch
                }

                // Optimistically update the UI first
                val currentPosts = _posts.value ?: emptyList()
                val updatedPosts = currentPosts.map { post ->
                    if (post.id == postId) {
                        val newLikeCount = if (post.isLiked) {
                            maxOf(0, post.likeCount - 1)
                        } else {
                            post.likeCount + 1
                        }
                        post.copy(
                            isLiked = !post.isLiked,
                            likeCount = newLikeCount
                        )
                    } else {
                        post
                    }
                }
                _posts.value = updatedPosts

                // Update current post if viewing detail
                _currentPost.value?.let { current ->
                    if (current.id == postId) {
                        val newLikeCount = if (current.isLiked) {
                            maxOf(0, current.likeCount - 1)
                        } else {
                            current.likeCount + 1
                        }
                        _currentPost.value = current.copy(
                            isLiked = !current.isLiked,
                            likeCount = newLikeCount
                        )
                    }
                }

                // Then perform the actual API call
                val result = repository.togglePostLike(postId)
                _likeResult.value = result

                result.fold(
                    onSuccess = { newLikeStatus ->
                        Log.d("DashboardViewModel", "Like toggled successfully. New status: $newLikeStatus")

                        // Create notification if user liked the post
                        if (newLikeStatus && currentPost != null && currentUserId != null) {
                            createLikeNotification(currentPost, currentUserId)
                        }
                    },
                    onFailure = { exception ->
                        Log.e("DashboardViewModel", "Failed to toggle like", exception)
                        // Revert the optimistic update
                        _posts.value = currentPosts
                    }
                )
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error toggling like", e)
                _likeResult.value = Result.failure(e)
            }
        }
    }

    fun toggleCommentLike(commentId: String, postId: String) {
        viewModelScope.launch {
            try {
                Log.d("DashboardViewModel", "Toggling like for comment: $commentId")

                // Optimistically update the UI
                val currentComments = _comments.value ?: emptyList()
                val updatedComments = currentComments.map { comment ->
                    if (comment.id == commentId) {
                        val newLikeCount = if (comment.isLiked) {
                            maxOf(0, comment.likeCount - 1)
                        } else {
                            comment.likeCount + 1
                        }
                        comment.copy(
                            isLiked = !comment.isLiked,
                            likeCount = newLikeCount
                        )
                    } else {
                        comment
                    }
                }
                _comments.value = updatedComments

                val result = repository.toggleCommentLike(commentId, postId)
                result.fold(
                    onSuccess = { _ ->
                        Log.d("DashboardViewModel", "Comment like toggled successfully")
                    },
                    onFailure = { exception ->
                        Log.e("DashboardViewModel", "Failed to toggle comment like", exception)
                        // Revert the optimistic update
                        _comments.value = currentComments
                    }
                )
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error toggling comment like", e)
            }
        }
    }

    fun addComment(postId: String, content: String) {
        viewModelScope.launch {
            try {
                Log.d("DashboardViewModel", "Adding comment to post: $postId")
                val result = repository.addComment(postId, content)
                _commentResult.value = result

                result.fold(
                    onSuccess = { commentId ->
                        Log.d("DashboardViewModel", "Comment added successfully with ID: $commentId")

                        // Update the comment count in posts list
                        val currentPosts = _posts.value ?: emptyList()
                        val updatedPosts = currentPosts.map { post ->
                            if (post.id == postId) {
                                post.copy(commentCount = post.commentCount + 1)
                            } else {
                                post
                            }
                        }
                        _posts.value = updatedPosts

                        // Update current post if viewing detail
                        _currentPost.value?.let { currentPost ->
                            if (currentPost.id == postId) {
                                _currentPost.value = currentPost.copy(
                                    commentCount = currentPost.commentCount + 1
                                )
                            }
                        }

                        // Create comment notification
                        val post = _currentPost.value ?: _posts.value?.find { it.id == postId }
                        val currentUserId = repository.getCurrentUserId()
                        if (post != null && currentUserId != null) {
                            createCommentNotification(post, currentUserId, content)
                        }

                        // Reload comments to show the new one
                        loadComments(postId)
                    },
                    onFailure = { exception ->
                        Log.e("DashboardViewModel", "Failed to add comment", exception)
                    }
                )
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error adding comment", e)
                _commentResult.value = Result.failure(e)
            }
        }
    }

    private fun createLikeNotification(post: Post, actorId: String) {
        viewModelScope.launch {
            try {
                val userProfile = repository.getUserProfile(actorId).getOrNull()
                val notification = Notification(
                    type = NotificationType.LIKE,
                    postId = post.id,
                    postContent = post.content.take(100),
                    actorId = actorId,
                    actorName = userProfile?.name ?: "Someone",
                    actorAvatar = userProfile?.avatarUrl ?: "",
                    actorProfilePhotoId = userProfile?.profilePhotoId ?: "default",
                    recipientId = post.authorId,
                    createdAt = System.currentTimeMillis()
                )
                repository.createNotification(notification)
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error creating like notification", e)
            }
        }
    }

    private fun createCommentNotification(post: Post, actorId: String, commentContent: String) {
        viewModelScope.launch {
            try {
                val userProfile = repository.getUserProfile(actorId).getOrNull()
                val notification = Notification(
                    type = NotificationType.COMMENT,
                    postId = post.id,
                    postContent = post.content.take(100),
                    actorId = actorId,
                    actorName = userProfile?.name ?: "Someone",
                    actorAvatar = userProfile?.avatarUrl ?: "",
                    actorProfilePhotoId = userProfile?.profilePhotoId ?: "default",
                    commentContent = commentContent.take(200),
                    recipientId = post.authorId,
                    createdAt = System.currentTimeMillis()
                )
                repository.createNotification(notification)
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error creating comment notification", e)
            }
        }
    }

    // Clear functions to avoid repeated notifications
    fun clearError() {
        _errorMessage.value = null
    }

    fun clearCreatePostResult() {
        _createPostResult.value = null
    }

    fun clearLikeResult() {
        _likeResult.value = null
    }

    fun clearCommentResult() {
        _commentResult.value = null
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                Log.d("DashboardViewModel", "Deleting post: $postId")
                _isLoading.value = true

                val result = repository.deletePost(postId)
                _deletePostResult.value = result

                result.fold(
                    onSuccess = {
                        Log.d("DashboardViewModel", "Post deleted successfully")
                        // Remove post from list
                        val currentPosts = _posts.value ?: emptyList()
                        _posts.value = currentPosts.filter { it.id != postId }

                        // Clear current post if it's the deleted one
                        if (_currentPost.value?.id == postId) {
                            _currentPost.value = null
                        }
                    },
                    onFailure = { exception ->
                        Log.e("DashboardViewModel", "Failed to delete post", exception)
                        _errorMessage.value = exception.message
                    }
                )
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error deleting post", e)
                _deletePostResult.value = Result.failure(e)
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteComment(commentId: String, postId: String) {
        viewModelScope.launch {
            try {
                Log.d("DashboardViewModel", "Deleting comment: $commentId")

                val result = repository.deleteComment(commentId, postId)
                _deleteCommentResult.value = result

                result.fold(
                    onSuccess = {
                        Log.d("DashboardViewModel", "Comment deleted successfully")
                        // Remove comment from list
                        val currentComments = _comments.value ?: emptyList()
                        _comments.value = currentComments.filter { it.id != commentId }

                        // Update comment count in posts
                        val currentPosts = _posts.value ?: emptyList()
                        _posts.value = currentPosts.map { post ->
                            if (post.id == postId) {
                                post.copy(commentCount = maxOf(0, post.commentCount - 1))
                            } else {
                                post
                            }
                        }

                        // Update current post comment count
                        _currentPost.value?.let { currentPost ->
                            if (currentPost.id == postId) {
                                _currentPost.value = currentPost.copy(
                                    commentCount = maxOf(0, currentPost.commentCount - 1)
                                )
                            }
                        }
                    },
                    onFailure = { exception ->
                        Log.e("DashboardViewModel", "Failed to delete comment", exception)
                        _errorMessage.value = exception.message
                    }
                )
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error deleting comment", e)
                _deleteCommentResult.value = Result.failure(e)
                _errorMessage.value = e.message
            }
        }
    }

    fun clearDeletePostResult() {
        _deletePostResult.value = null
    }

    fun clearDeleteCommentResult() {
        _deleteCommentResult.value = null
    }

    fun getCurrentUserId(): String? {
        return repository.getCurrentUserId()
    }
}