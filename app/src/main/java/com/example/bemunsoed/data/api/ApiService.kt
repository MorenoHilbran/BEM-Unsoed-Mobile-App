package com.example.bemunsoed.data.api

import com.example.bemunsoed.data.model.Event
import com.example.bemunsoed.data.model.Merch
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("events")
    suspend fun getEvents(
        @Query("highlight") highlight: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<List<Event>>

    @GET("merch")
    suspend fun getMerch(
        @Query("limit") limit: Int? = null,
        @Query("cursor") cursor: String? = null
    ): Response<List<Merch>>
}
