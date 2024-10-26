package com.example.eventlistapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventlistapp.data.remote.response.Event
import com.example.eventlistapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val eventsLiveData = MutableLiveData<List<Event>>()
    private val errorMessageLiveData = MutableLiveData<String>()
    private val isLoadingLiveData = MutableLiveData<Boolean>()

    fun getEvents(): LiveData<List<Event>> = eventsLiveData

    fun getErrorMessage(): LiveData<String> = errorMessageLiveData

    fun getIsLoading(): LiveData<Boolean> = isLoadingLiveData // Expose loading LiveData

    fun fetchEvents(forceReload: Boolean = false) {
        if (forceReload || eventsLiveData.value.isNullOrEmpty()) {
            isLoadingLiveData.value = true // Start loading
            viewModelScope.launch {
                try {
                    val response = ApiConfig.getApiService().getEventsLimit(1, 5)
                    if (response.isSuccessful) {
                        val eventList = response.body()?.listEvents
                        eventsLiveData.value = eventList ?: emptyList()
                    } else {
                        errorMessageLiveData.value = "Failed to load finished events. Please try again."
                    }
                } catch (e: Exception) {
                    errorMessageLiveData.value = "Network error. Please check your internet connection."
                } finally {
                    isLoadingLiveData.value = false // End loading
                }
            }
        }
    }
}
