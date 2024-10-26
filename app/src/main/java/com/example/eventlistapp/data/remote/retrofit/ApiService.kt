package com.example.eventlistapp.data.remote.retrofit

import com.example.eventlistapp.data.remote.response.ApiResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("events")
    suspend fun getEvents(@Query("active") active: Int): Response<ApiResponse>

    @GET("events")
    suspend fun getEventsLimit(
        @Query("active") active: Int,
        @Query("limit") limit: Int
    ): Response<ApiResponse>

    @GET("events")
    suspend fun searchEvents(@Query("q") query: String): Response<ApiResponse>

    @GET("events/{id}")
    suspend fun getEventDetail(@Path("id") id: Int): Response<ApiResponse>

    companion object {
        private const val BASE_URL = "https://event-api.dicoding.dev/"

        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
