package com.example.bemunsoed.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bemunsoed.data.model.Banner
import com.example.bemunsoed.data.model.Event
import com.example.bemunsoed.data.model.Merch
import com.example.bemunsoed.data.repository.BemRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = BemRepository()

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events

    private val _merch = MutableLiveData<List<Merch>>()
    val merch: LiveData<List<Merch>> = _merch

    private val _banners = MutableLiveData<List<Banner>>()
    val banners: LiveData<List<Banner>> = _banners

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        Log.d("HomeViewModel", "Starting to load home data...")
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Load banners
                Log.d("HomeViewModel", "Loading banners...")
                repository.getBanners().let { result ->
                    result.onSuccess { bannerList ->
                        Log.d("HomeViewModel", "Successfully loaded "+bannerList.size+" banners")
                        _banners.value = bannerList
                        if (bannerList.isEmpty()) {
                            Log.w("HomeViewModel", "No banners found in Firebase")
                        }
                    }.onFailure { exception ->
                        Log.e("HomeViewModel", "Failed to load banners", exception)
                        _banners.value = emptyList()
                        _errorMessage.value = "Gagal memuat banner: ${exception.message}"
                    }
                }

                // Load events
                Log.d("HomeViewModel", "Loading events...")
                repository.getHighlightEvents().let { result ->
                    result.onSuccess { eventList ->
                        Log.d("HomeViewModel", "Successfully loaded ${eventList.size} events")
                        _events.value = eventList
                        if (eventList.isEmpty()) {
                            Log.w("HomeViewModel", "No events found in Firebase")
                        }
                    }.onFailure { exception ->
                        Log.e("HomeViewModel", "Failed to load events", exception)
                        _events.value = emptyList()
                        _errorMessage.value = "Gagal memuat event: ${exception.message}"
                    }
                }

                // Load merch
                Log.d("HomeViewModel", "Loading merchandise...")
                repository.getMerch().let { result ->
                    result.onSuccess { merchList ->
                        Log.d("HomeViewModel", "Successfully loaded ${merchList.size} merchandise items")
                        _merch.value = merchList
                        if (merchList.isEmpty()) {
                            Log.w("HomeViewModel", "No merchandise found in Firebase")
                        }
                    }.onFailure { exception ->
                        Log.e("HomeViewModel", "Failed to load merchandise", exception)
                        _merch.value = emptyList()
                        _errorMessage.value = "Gagal memuat merchandise: ${exception.message}"
                    }
                }

                Log.d("HomeViewModel", "Finished loading all home data")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Unexpected error while loading data", e)
                _errorMessage.value = "Gagal memuat data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshData() {
        Log.d("HomeViewModel", "Refreshing data...")
        loadHomeData()
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
