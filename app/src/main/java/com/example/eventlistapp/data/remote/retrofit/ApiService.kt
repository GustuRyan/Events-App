package com.example.eventlistapp.data.remote.retrofit

import com.example.eventlistapp.data.remote.response.Events
import com.example.eventlistapp.data.remote.response.Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getEvents(@Query("active") active: Int): Call<Response>

    @GET("events")
    fun searchEvents(@Query("q") query: String): Call<Response>

    @GET("events/{id}")
    fun getEventDetail(@Path("id") id: Int): Call<Events>
}