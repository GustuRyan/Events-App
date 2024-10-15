package com.example.eventlistapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eventlistapp.Event
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class HomeViewModel : ViewModel() {

    private val _eventList = MutableLiveData<List<Event>>()
    val eventList: LiveData<List<Event>> get() = _eventList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchEvents() {

        _isLoading.value = true // Set loading state to true
        _errorMessage.value = null // Clear previous error messages

        val client = AsyncHttpClient()
        val url = "https://event-api.dicoding.dev/events?active=1&limit=5" // API URL for fetching events

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
                    val responseArray = jsonObject.getJSONArray("listEvents") // Get the listEvents array
                    val events = mutableListOf<Event>()

                    for (i in 0 until responseArray.length()) {
                        val eventJson = responseArray.getJSONObject(i)

                        // Extract the image URL from the description
                        val descriptionHtml = eventJson.getString("description")
                        val imageUrl = extractImageUrl(descriptionHtml)

                        val event = Event(
                            summary = eventJson.getString("summary"),
                            imageLogo = imageUrl ?: "default_image_url_here",
                            ownerName = eventJson.getString("ownerName"),
                            cityName = eventJson.getString("cityName"),
                            name = eventJson.getString("name"),
                            id = eventJson.getInt("id"),
                            category = eventJson.getString("category")
                        )
                        events.add(event)
                    }

                    _eventList.value = events // Update the event list LiveData

                } catch (e: Exception) {
                    _isLoading.value = false // Hide loading state on error
                    _errorMessage.value = e.message // Set error message
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable
            ) {
                _isLoading.value = false // Hide loading state on failure

                val errorMessage = when (statusCode) {
                    401 -> "$statusCode: Bad Request"
                    403 -> "$statusCode: Forbidden"
                    404 -> "$statusCode: Not Found"
                    else -> {
                        val message = responseBody?.let { String(it) } ?: error.message
                        "$statusCode: ${message ?: "Unknown error"}"
                    }
                }
                _errorMessage.value = errorMessage // Set error message
                Log.e("API Error", "Status Code: $statusCode, Error: ${error.message}")
            }
        })
    }

    private fun extractImageUrl(description: String): String? {
        // Simple regex to extract the image URL from the HTML
        val regex = """<img[^>]+src="([^">]+)"""".toRegex()
        val matchResult = regex.find(description)
        return matchResult?.groups?.get(1)?.value
    }
}
