package com.example.bemunsoed.data.repository

import com.example.bemunsoed.data.api.ApiService
import com.example.bemunsoed.data.model.Event
import com.example.bemunsoed.data.model.Merch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BemRepository(private val apiService: ApiService? = null) {

    suspend fun getHighlightEvents(): Result<List<Event>> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Try API first if available
            if (apiService != null) {
                val response = apiService.getEvents(highlight = 1, limit = 5)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    // Fallback to mock data if API fails
                    Result.success(getMockEvents())
                }
            } else {
                // Use mock data if no API service
                Result.success(getMockEvents())
            }
        } catch (e: Exception) {
            // Fallback to mock data on any error
            Result.success(getMockEvents())
        }
    }

    suspend fun getMerch(): Result<List<Merch>> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Try API first if available
            if (apiService != null) {
                val response = apiService.getMerch(limit = 10)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    // Fallback to mock data if API fails
                    Result.success(getMockMerch())
                }
            } else {
                // Use mock data if no API service
                Result.success(getMockMerch())
            }
        } catch (e: Exception) {
            // Fallback to mock data on any error
            Result.success(getMockMerch())
        }
    }

    private fun getMockEvents(): List<Event> {
        return listOf(
            Event(
                id = "1",
                title = "Welcome Event BEM",
                description = "Event penyambutan mahasiswa baru",
                imageUrl = "",
                date = "2024-01-15",
                location = "Gedung Rektorat",
                isHighlight = true,
                registrationUrl = "https://bem-unsoed.com/event1"
            ),
            Event(
                id = "2",
                title = "Workshop Leadership",
                description = "Workshop pengembangan kepemimpinan",
                imageUrl = "",
                date = "2024-01-20",
                location = "Aula BEM",
                isHighlight = true,
                registrationUrl = "https://bem-unsoed.com/event2"
            )
        )
    }

    private fun getMockMerch(): List<Merch> {
        return listOf(
            Merch(
                id = "1",
                name = "Kaos BEM Unsoed",
                description = "Kaos official BEM Universitas Soedirman",
                price = "Rp 75.000",
                imageUrl = "",
                linkUrl = "https://bem-unsoed.com/merch1",
                category = "clothing"
            ),
            Merch(
                id = "2",
                name = "Totebag BEM",
                description = "Totebag dengan logo BEM Unsoed",
                price = "Rp 45.000",
                imageUrl = "",
                linkUrl = "https://bem-unsoed.com/merch2",
                category = "accessories"
            ),
            Merch(
                id = "3",
                name = "Sticker Pack",
                description = "Kumpulan sticker BEM Unsoed",
                price = "Rp 25.000",
                imageUrl = "",
                linkUrl = "https://bem-unsoed.com/merch3",
                category = "accessories"
            )
        )
    }
}
