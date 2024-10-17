package com.example.eventlistapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class DetailViewModel : ViewModel() {
    private val _eventDetails = MutableLiveData<Event>()
    val eventDetails: LiveData<Event> get() = _eventDetails

    private val _isLoading = MutableLiveData<Boolean>() // Declare isLoading
    val isLoading: LiveData<Boolean> get() = _isLoading // Expose it as LiveData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchEventDetails(eventId: Int) {
        val url = "https://event-api.dicoding.dev/events/$eventId"
        val client = AsyncHttpClient()

        _isLoading.value = true // Set loading state to true
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
                _isLoading.value = false // Hide loading state
                val result = String(responseBody)
                Log.d("API Response", result)

                try {
                    val jsonObject = JSONObject(result)
                    val eventJson = jsonObject.getJSONObject("event")

                    // Create Event instance
                    val event = Event(
                        id = eventJson.getInt("id"),
                        name = eventJson.getString("name"),
                        description = eventJson.getString("description"),
                        mediaCover = eventJson.getString("mediaCover"),
                        registrants = eventJson.getInt("registrants"),
                        imageLogo = eventJson.getString("imageLogo"),
                        link = eventJson.getString("link"),
                        ownerName = eventJson.getString("ownerName"),
                        cityName = eventJson.getString("cityName"),
                        quota = eventJson.getInt("quota"),
                        beginTime = eventJson.getString("beginTime"),
                        endTime = eventJson.getString("endTime"),
                        category = eventJson.getString("category"),
                        summary = eventJson.optString("summary") // Use optString to avoid NullPointerException
                    )

                    // Set the event details to LiveData
                    _eventDetails.value = event

                } catch (e: Exception) {
                    _isLoading.value = false
                    _errorMessage.value = e.message
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable) {
                _isLoading.value = false
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode: Bad Request"
                    403 -> "$statusCode: Forbidden"
                    404 -> "$statusCode: Not Found"
                    else -> "$statusCode: ${error.message}"
                }
                _errorMessage.value = errorMessage
            }
        })
    }
}
