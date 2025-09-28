package com.example.bemunsoed.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bemunsoed.data.model.Event
import com.example.bemunsoed.data.model.Merch
import com.example.bemunsoed.di.ApiClient
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = ApiClient.repository

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events

    private val _merch = MutableLiveData<List<Merch>>()
    val merch: LiveData<List<Merch>> = _merch

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load events
                repository.getHighlightEvents().let { result ->
                    result.onSuccess { eventList ->
                        _events.value = eventList
                    }.onFailure { exception ->
                        _errorMessage.value = "Gagal memuat event: ${exception.message}"
                    }
                }

                // Load merch
                repository.getMerch().let { result ->
                    result.onSuccess { merchList ->
                        _merch.value = merchList
                    }.onFailure { exception ->
                        _errorMessage.value = "Gagal memuat merchandise: ${exception.message}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    fun refreshData() {
        loadHomeData()
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
