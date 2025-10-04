package com.example.bemunsoed.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.bemunsoed.data.repository.FirebaseRepository
import com.example.bemunsoed.data.model.Notification
import android.util.Log

class NotificationViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> = _notifications

    private val _unreadCount = MutableLiveData<Int>()
    val unreadCount: LiveData<Int> = _unreadCount

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Track last mark as read time to prevent immediate reload
    private var lastMarkAllAsReadTime = 0L

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = repository.getCurrentUserId()

                if (userId == null) {
                    _errorMessage.value = "User not logged in"
                    _notifications.value = emptyList()
                    return@launch
                }

                Log.d("NotificationViewModel", "Loading notifications for user: $userId")

                val result = repository.getNotifications(userId)
                result.fold(
                    onSuccess = { notificationList ->
                        Log.d("NotificationViewModel", "Loaded ${notificationList.size} notifications")
                        _notifications.value = notificationList

                        // Update unread count
                        val unread = notificationList.count { !it.isRead }
                        _unreadCount.value = unread
                    },
                    onFailure = { exception ->
                        Log.e("NotificationViewModel", "Failed to load notifications", exception)
                        _errorMessage.value = "Failed to load notifications: ${exception.message}"
                        _notifications.value = emptyList()
                    }
                )
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error loading notifications", e)
                _errorMessage.value = "Error: ${e.message}"
                _notifications.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUnreadCount() {
        // Skip if recently marked as read to prevent race condition
        val timeSinceLastMarkAsRead = System.currentTimeMillis() - lastMarkAllAsReadTime
        if (timeSinceLastMarkAsRead < 3000) {
            Log.d("NotificationViewModel", "Skipping loadUnreadCount - recently marked as read ($timeSinceLastMarkAsRead ms ago)")
            return
        }

        viewModelScope.launch {
            try {
                val userId = repository.getCurrentUserId() ?: return@launch

                Log.d("NotificationViewModel", "Loading unread count from database...")
                val result = repository.getUnreadNotificationCount(userId)
                result.fold(
                    onSuccess = { count ->
                        Log.d("NotificationViewModel", "Unread count from database: $count")
                        _unreadCount.value = count
                    },
                    onFailure = { exception ->
                        Log.e("NotificationViewModel", "Failed to load unread count", exception)
                    }
                )
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error loading unread count", e)
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                val result = repository.markNotificationAsRead(notificationId)
                result.fold(
                    onSuccess = {
                        Log.d("NotificationViewModel", "Notification marked as read")
                        // Update the local list
                        val updatedList = _notifications.value?.map { notification ->
                            if (notification.id == notificationId) {
                                notification.copy(isRead = true)
                            } else {
                                notification
                            }
                        }
                        _notifications.value = updatedList ?: emptyList()

                        // Update unread count
                        val unread = updatedList?.count { !it.isRead } ?: 0
                        _unreadCount.value = unread
                    },
                    onFailure = { exception ->
                        Log.e("NotificationViewModel", "Failed to mark as read", exception)
                    }
                )
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error marking as read", e)
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                val userId = repository.getCurrentUserId() ?: return@launch

                // Prevent double-clicking
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastMarkAllAsReadTime < 2000) {
                    Log.d("NotificationViewModel", "Skipping markAllAsRead - last mark time too recent")
                    return@launch
                }
                lastMarkAllAsReadTime = currentTime

                Log.d("NotificationViewModel", "Starting markAllAsRead process...")

                // Update database first
                val result = repository.markAllNotificationsAsRead(userId)
                result.fold(
                    onSuccess = {
                        Log.d("NotificationViewModel", "Database update successful - all notifications marked as read")

                        // Update the local list
                        val updatedList = _notifications.value?.map { it.copy(isRead = true) }
                        _notifications.value = updatedList ?: emptyList()

                        // Force update unread count to 0
                        _unreadCount.value = 0

                        Log.d("NotificationViewModel", "Local state updated - unread count set to 0")
                    },
                    onFailure = { exception ->
                        Log.e("NotificationViewModel", "Failed to mark all as read: ${exception.message}", exception)
                        _errorMessage.value = "Failed to mark all as read: ${exception.message}"
                        // Reset the timestamp on failure so user can retry
                        lastMarkAllAsReadTime = 0L
                    }
                )
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error marking all as read", e)
                _errorMessage.value = "Error: ${e.message}"
                lastMarkAllAsReadTime = 0L
            }
        }
    }

    fun refreshNotifications() {
        loadNotifications()
    }
}
