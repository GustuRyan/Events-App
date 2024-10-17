package com.example.eventlistapp.ui.upcoming

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eventlistapp.Event
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class UpcomingViewModel : ViewModel() {
    private val _eventList = MutableLiveData<List<Event>>()
    val eventList: LiveData<List<Event>> get() = _eventList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun searchEvents(searchText: String, parentFragment: String) {
        _isLoading.value = true // Show loading state
        _errorMessage.value = null // Clear previous errors

        val client = AsyncHttpClient()
        val url = when (parentFragment) {
            "HomeFragment" -> "https://event-api.dicoding.dev/events?active=0&limit=5"
            "UpcomingFragment" -> "https://event-api.dicoding.dev/events?active=1&q=$searchText"
            else -> {
                _isLoading.value = false
                _errorMessage.value = "Unknown parent fragment"
                return
            }
        }

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray
            ) {
                _isLoading.value = false // Hide loading state
                val result = String(responseBody)
                Log.d("API Response", result)

                try {
                    val jsonObject = JSONObject(result)
                    val responseArray = jsonObject.getJSONArray("listEvents")
                    val events = mutableListOf<Event>()

                    for (i in 0 until responseArray.length()) {
                        val eventJson = responseArray.getJSONObject(i)

                        // Extract image URL from the description
                        val descriptionHtml = eventJson.getString("description")
                        val imageUrl = extractImageUrl(descriptionHtml)

                        val event = Event(
                            summary = eventJson.getString("summary"),
                            imageLogo = imageUrl ?: "default_image_url_here",
                            ownerName = eventJson.getString("ownerName"),
                            cityName = eventJson.getString("cityName"),
                            name = eventJson.getString("name"),
                            id = eventJson.getInt("id"),
                            category = eventJson.getString("category"),
                            registrants = eventJson.getInt("registrants"),
                            quota = eventJson.getInt("quota")
                        )
                        events.add(event)
                    }

                    _eventList.value = events // Update LiveData with the fetched events

                } catch (e: Exception) {
                    _isLoading.value = false
                    _errorMessage.value = e.message
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable
            ) {
                _isLoading.value = false
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode: Bad Request"
                    403 -> "$statusCode: Forbidden"
                    404 -> "$statusCode: Not Found"
                    else -> {
                        val message = responseBody?.let { String(it) } ?: error.message
                        "$statusCode: ${message ?: "Unknown error"}"
                    }
                }
                _errorMessage.value = errorMessage
                Log.e("API Error", "Status Code: $statusCode, Error: ${error.message}")
            }

            private fun extractImageUrl(description: String): String? {
                // Extract image URL from HTML using regex
                val regex = """<img[^>]+src="([^">]+)"""".toRegex()
                val matchResult = regex.find(description)
                return matchResult?.groups?.get(1)?.value
            }
        })
    }

    fun clearEventList() {
        _eventList.value = emptyList() // Clear the event list
    }
}