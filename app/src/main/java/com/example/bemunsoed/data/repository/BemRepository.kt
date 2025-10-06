package com.example.bemunsoed.data.repository

import com.example.bemunsoed.data.model.Banner
import com.example.bemunsoed.data.model.Event
import com.example.bemunsoed.data.model.Merch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BemRepository {
    private val firebaseRepository = FirebaseRepository()

    suspend fun getHighlightEvents(): Result<List<Event>> = withContext(Dispatchers.IO) {
        return@withContext firebaseRepository.getHighlightEvents()
    }

    suspend fun getAllEvents(): Result<List<Event>> = withContext(Dispatchers.IO) {
        return@withContext firebaseRepository.getAllEvents()
    }

    suspend fun getMerch(): Result<List<Merch>> = withContext(Dispatchers.IO) {
        return@withContext firebaseRepository.getMerch()
    }

    suspend fun getBanners(): Result<List<Banner>> = withContext(Dispatchers.IO) {
        return@withContext firebaseRepository.getBanners()
    }
}
