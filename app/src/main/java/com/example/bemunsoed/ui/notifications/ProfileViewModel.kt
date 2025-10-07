package com.example.bemunsoed.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bemunsoed.data.repository.FirebaseRepository
import com.example.bemunsoed.data.model.Post
import com.example.bemunsoed.data.model.User
import com.example.bemunsoed.data.model.Comment
import kotlinx.coroutines.launch
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class ProfileViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser

    private val _userPosts = MutableLiveData<List<Post>>()
    val userPosts: LiveData<List<Post>> = _userPosts

    private val _userComments = MutableLiveData<List<Comment>>()
    val userComments: LiveData<List<Comment>> = _userComments

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _updateProfileResult = MutableLiveData<Result<Unit>?>()
    val updateProfileResult: LiveData<Result<Unit>?> = _updateProfileResult

    init {
        loadCurrentUserProfile()
    }

    fun loadCurrentUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("ProfileViewModel", "Loading current user profile...")
                val result = repository.getCurrentUserProfile()
                result.onSuccess { user ->
                    Log.d("ProfileViewModel", "Successfully loaded user profile: ${user.name}")
                    _currentUser.value = user
                    // Load user posts and comments after getting profile
                    loadUserPosts(user.id)
                    loadUserComments(user.id)
                }.onFailure { exception ->
                    Log.e("ProfileViewModel", "Failed to load user profile", exception)
                    _errorMessage.value = "Failed to load profile: ${exception.message}"
                    // Create a default user profile
                    createDefaultProfile()
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Exception loading user profile", e)
                _errorMessage.value = "Error loading profile: ${e.message}"
                createDefaultProfile()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun createDefaultProfile() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val defaultUser = User(
                id = currentUser.uid,
                name = "Jend-Z User",
                email = currentUser.email ?: "",
                fakultas = "",
                jurusan = "",
                angkatan = "",
                bio = "Gensoed Kece"
            )
            _currentUser.value = defaultUser
            // Save default profile to Firebase
            updateProfile(defaultUser)
            loadUserPosts(currentUser.uid)
            loadUserComments(currentUser.uid)
        }
    }

    private fun loadUserPosts(userId: String) {
        if (userId.isEmpty()) {
            Log.w("ProfileViewModel", "User ID is empty, cannot load posts")
            return
        }

        viewModelScope.launch {
            try {
                Log.d("ProfileViewModel", "Loading posts for user: $userId")
                val result = repository.getUserPosts(userId)
                result.onSuccess { posts ->
                    Log.d("ProfileViewModel", "Successfully loaded ${posts.size} user posts")
                    _userPosts.value = posts
                    _errorMessage.value = null
                }.onFailure { exception ->
                    Log.e("ProfileViewModel", "Failed to load user posts", exception)
                    _errorMessage.value = "Failed to load posts: ${exception.message}"
                    _userPosts.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Exception loading user posts", e)
                _errorMessage.value = "Error loading posts: ${e.message}"
                _userPosts.value = emptyList()
            }
        }
    }

    private fun loadUserComments(userId: String) {
        if (userId.isEmpty()) {
            Log.w("ProfileViewModel", "User ID is empty, cannot load comments")
            return
        }

        viewModelScope.launch {
            try {
                Log.d("ProfileViewModel", "Loading comments for user: $userId")
                val result = repository.getUserComments(userId)
                result.onSuccess { comments ->
                    Log.d("ProfileViewModel", "Successfully loaded ${comments.size} user comments")
                    _userComments.value = comments
                    _errorMessage.value = null
                }.onFailure { exception ->
                    Log.e("ProfileViewModel", "Failed to load user comments", exception)
                    _errorMessage.value = "Failed to load comments: ${exception.message}"
                    _userComments.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Exception loading user comments", e)
                _errorMessage.value = "Error loading comments: ${e.message}"
                _userComments.value = emptyList()
            }
        }
    }

    fun updateProfile(user: User) {
        viewModelScope.launch {
            try {
                Log.d("ProfileViewModel", "Updating user profile...")
                val result = repository.updateUserProfile(user)
                _updateProfileResult.value = result

                if (result.isSuccess) {
                    Log.d("ProfileViewModel", "Profile updated successfully")
                    _currentUser.value = user

                    // Update profile photo ID in all user's posts if it changed
                    val currentUserId = repository.getCurrentUserId()
                    if (currentUserId != null) {
                        updateUserPostsProfilePhoto(currentUserId, user.profilePhotoId)
                    }

                    // Refresh posts and comments after profile update to ensure authorName is correct
                    loadUserPosts(user.id)
                    loadUserComments(user.id)
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Exception updating profile", e)
                _updateProfileResult.value = Result.failure(e)
            }
        }
    }

    private fun updateUserPostsProfilePhoto(userId: String, newProfilePhotoId: String) {
        viewModelScope.launch {
            try {
                Log.d("ProfileViewModel", "Updating profile photo ID in user's posts and comments...")

                // Update profile photo in all posts
                repository.updateUserPostsProfilePhoto(userId, newProfilePhotoId)

                // Update profile photo in all comments
                repository.updateUserCommentsProfilePhoto(userId, newProfilePhotoId)

                Log.d("ProfileViewModel", "Profile photo ID updated in posts and comments")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating posts/comments profile photo", e)
            }
        }
    }

    fun refreshProfile() {
        Log.d("ProfileViewModel", "Refreshing profile and posts...")
        loadCurrentUserProfile()
    }

    fun refreshUserPosts() {
        Log.d("ProfileViewModel", "Refreshing user posts...")
        _currentUser.value?.let { user ->
            if (user.id.isNotEmpty()) {
                loadUserPosts(user.id)
            } else {
                Log.w("ProfileViewModel", "Cannot refresh posts - user ID is empty")
            }
        } ?: run {
            Log.w("ProfileViewModel", "Cannot refresh posts - current user is null")
            // Try to reload profile first
            loadCurrentUserProfile()
        }
    }

    fun refreshUserComments() {
        Log.d("ProfileViewModel", "Refreshing user comments...")
        _currentUser.value?.let { user ->
            if (user.id.isNotEmpty()) {
                loadUserComments(user.id)
            } else {
                Log.w("ProfileViewModel", "Cannot refresh comments - user ID is empty")
            }
        } ?: run {
            Log.w("ProfileViewModel", "Cannot refresh comments - current user is null")
            // Try to reload profile first
            loadCurrentUserProfile()
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearUpdateResult() {
        _updateProfileResult.value = null
    }

    fun getJoinDate(): String {
        // Format join date from Firebase Auth or user data
        return try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val creationTime = currentUser?.metadata?.creationTimestamp
            if (creationTime != null) {
                val date = java.util.Date(creationTime)
                java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault()).format(date)
            } else {
                "Recently"
            }
        } catch (e: Exception) {
            "Recently"
        }
    }

    fun getCurrentUserId(): String? {
        return repository.getCurrentUserId()
    }

    fun uploadImage(uri: android.net.Uri, storagePath: String, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("ProfileViewModel", "Uploading image to: $storagePath")
                val result = repository.uploadImage(uri, storagePath)
                result.onSuccess { downloadUrl ->
                    Log.d("ProfileViewModel", "Image uploaded successfully: $downloadUrl")
                    callback(true, downloadUrl)
                }.onFailure { exception ->
                    Log.e("ProfileViewModel", "Failed to upload image", exception)
                    callback(false, null)
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Exception uploading image", e)
                callback(false, null)
            }
        }
    }
}
