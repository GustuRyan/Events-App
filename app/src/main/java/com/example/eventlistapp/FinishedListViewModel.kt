package com.example.eventlistapp.ui.finished

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eventlistapp.Event
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import org.json.JSONObject

class FinishedListViewModel : ViewModel() {
    private val _eventList = MutableLiveData<List<Event>>()
    val eventList: LiveData<List<Event>> get() = _eventList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val client = AsyncHttpClient()

    fun fetchFinishedEvents() {
        _isLoading.value = true
        _errorMessage.value = null

        // URL for fetching finished events
        val url = "https://event-api.dicoding.dev/events?active=0" // Active status set to 0 for finished events

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out cz.msebera.android.httpclient.Header>?,
                responseBody: ByteArray
            ) {
                _isLoading.value = false
                val result = String(responseBody)
                Log.d("API Response", result)

                try {
                    val jsonObject = JSONObject(result)
                    val responseArray = jsonObject.getJSONArray("listEvents")
                    val events = mutableListOf<Event>()

                    for (i in 0 until responseArray.length()) {
                        val eventJson = responseArray.getJSONObject(i)
                        val descriptionHtml = eventJson.getString("description")
                        // Use 'this@FinishedListViewModel' to call the outer class function
                        val imageUrl = extractImageUrl(descriptionHtml)

                        val event = Event(
                            summary = eventJson.getString("summary"),
                            registrants = eventJson.getInt("registrants"),
                            imageLogo = imageUrl ?: "default_image_url_here",
                            ownerName = eventJson.getString("ownerName"),
                            cityName = eventJson.getString("cityName"),
                            quota = eventJson.getInt("quota"),
                            name = eventJson.getString("name"),
                            id = eventJson.getInt("id"),
                            category = eventJson.getString("category")
                        )
                        events.add(event)
                    }

                    _eventList.value = events

                } catch (e: Exception) {
                    _errorMessage.value = e.message
                    Log.e("FinishedListViewModel", "Error parsing JSON: ${e.message}")
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out cz.msebera.android.httpclient.Header>?,
                responseBody: ByteArray,
                error: Throwable
            ) {
                _isLoading.value = false
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                _errorMessage.value = errorMessage
                Log.e("FinishedListViewModel", "Error fetching events: $errorMessage")
            }
        })
    }

    fun searchFinishedEvents(searchText: String) {
        _isLoading.value = true
        _errorMessage.value = null

        // URL for fetching finished events with a search query
        val url = "https://event-api.dicoding.dev/events?active=0&q=$searchText"

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out cz.msebera.android.httpclient.Header>?,
                responseBody: ByteArray
            ) {
                _isLoading.value = false
                val result = String(responseBody)
                Log.d("API Search Response", result)

                try {
                    val jsonObject = JSONObject(result)
                    val responseArray = jsonObject.getJSONArray("listEvents")
                    val events = mutableListOf<Event>()

                    for (i in 0 until responseArray.length()) {
                        val eventJson = responseArray.getJSONObject(i)
                        val descriptionHtml = eventJson.getString("description")
                        // Use 'this@FinishedListViewModel' to call the outer class function
                        val imageUrl = extractImageUrl(descriptionHtml)

                        val event = Event(
                            summary = eventJson.getString("summary"),
                            registrants = eventJson.getInt("registrants"),
                            imageLogo = imageUrl ?: "default_image_url_here",
                            ownerName = eventJson.getString("ownerName"),
                            cityName = eventJson.getString("cityName"),
                            quota = eventJson.getInt("quota"),
                            name = eventJson.getString("name"),
                            id = eventJson.getInt("id"),
                            category = eventJson.getString("category")
                        )
                        events.add(event)
                    }

                    _eventList.value = events

                } catch (e: Exception) {
                    _errorMessage.value = e.message
                    Log.e("FinishedListViewModel", "Error parsing JSON: ${e.message}")
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out cz.msebera.android.httpclient.Header>?,
                responseBody: ByteArray,
                error: Throwable
            ) {
                _isLoading.value = false
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                _errorMessage.value = errorMessage
                Log.e("FinishedListViewModel", "Error fetching events: $errorMessage")
            }
        })
    }

    private fun extractImageUrl(description: String): String? {
        // Extract image URL from HTML using regex
        val regex = """<img[^>]+src="([^">]+)"""".toRegex()
        val matchResult = regex.find(description)
        return matchResult?.groups?.get(1)?.value
    }
}
