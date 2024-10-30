package com.example.eventlistapp.repository

import com.example.eventlistapp.data.remote.response.Response
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response as OkHttpResponse
import java.io.IOException

class EventRepository {
    private val client = OkHttpClient()
    private val gson = Gson()

    fun fetchEvents(callback: (Response?) -> Unit) {
        val request = Request.Builder()
            .url("https://event-api.dicoding.dev/events") // Replace with your actual URL
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure (you can also notify UI or log this error)
                e.printStackTrace()
                callback(null)
            }

            override fun onResponse(call: Call, response: OkHttpResponse) {
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        val jsonString = responseBody.string()

                        // Deserialize the JSON into your Response data class
                        val eventResponse = gson.fromJson(jsonString, Response::class.java)

                        // Pass the response to the callback
                        callback(eventResponse)
                    } ?: run {
                        callback(null) // Handle null response body
                    }
                } else {
                    callback(null) // Handle unsuccessful response
                }
            }
        })
    }
}