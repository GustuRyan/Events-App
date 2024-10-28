package com.example.eventlistapp.ui.upcoming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventlistapp.data.remote.response.Event
import com.example.eventlistapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch

class UpcomingViewModel : ViewModel() {
    private val eventsLiveData = MutableLiveData<List<Event>>()
    private val errorMessageLiveData = MutableLiveData<String>()
    private val isLoadingLiveData = MutableLiveData<Boolean>()

    fun getEvents(): LiveData<List<Event>> = eventsLiveData

    fun getErrorMessage(): LiveData<String> = errorMessageLiveData

    fun getIsLoading(): LiveData<Boolean> = isLoadingLiveData // Expose loading LiveData

    fun fetchUpcomingEvents(forceReload: Boolean = false, fragmentType: Int) {
        if (forceReload || eventsLiveData.value.isNullOrEmpty()) {
            isLoadingLiveData.value = true // Start loading
            viewModelScope.launch {
                try {
                    val response = if (fragmentType == 1) {
                        ApiConfig.getApiService().getEvents(fragmentType)
                    } else {
                        ApiConfig.getApiService().getEventsLimit(fragmentType, 5)
                    }

                    if (response.isSuccessful) {
                        val eventList = response.body()?.listEvents
                        eventsLiveData.value = eventList ?: emptyList()
                    } else {
                        errorMessageLiveData.value = "Failed to load events. Please try again."
                    }
                } catch (e: Exception) {
                    errorMessageLiveData.value = "Network error. Please check your internet connection."
                } finally {
                    isLoadingLiveData.value = false // End loading
                }
            }
        }
    }

    fun searchEvents(query: String) {
        isLoadingLiveData.value = true // Start loading for search
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().searchEvents(query)
                if (response.isSuccessful) {
                    val eventList = response.body()?.listEvents
                    eventsLiveData.value = eventList ?: emptyList()
                } else {
                    errorMessageLiveData.value = "Search failed. Please try again."
                }
            } catch (e: Exception) {
                errorMessageLiveData.value = "Network error. Please check your internet connection."
            } finally {
                isLoadingLiveData.value = false // End loading
            }
        }
    }

    fun clearEventList() {
        eventsLiveData.value = emptyList() // Clear the current event list
    }
}