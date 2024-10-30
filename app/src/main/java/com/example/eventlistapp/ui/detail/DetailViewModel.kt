package com.example.eventlistapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.eventlistapp.repository.EventRepository
import com.example.eventlistapp.data.local.entity.Favorite
import com.example.eventlistapp.data.remote.response.Event
import com.example.eventlistapp.repository.FavoriteRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val eventRepository = EventRepository() // No need to pass ApiService for fetchEvents

    private val _event = MutableLiveData<Event?>()
    val event: LiveData<Event?> get() = _event

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val favoriteRepository: FavoriteRepository = FavoriteRepository(application)

    fun fetchEventDetail(eventId: Int) {
        _loading.postValue(true)

        eventRepository.fetchEvents { response ->
            if (response?.listEvents != null) {
                // Find the event by ID using listEvents
                _event.postValue(response.listEvents.find { it?.id == eventId })
            } else {
                _event.postValue(null) // Handle error or empty case
            }
            _loading.postValue(false)
        }
    }

    suspend fun toggleFavorite(event: Event) {
        val favorite = convertEventToFavorite(event)
        val existingFavorite = favoriteRepository.getFavoriteById(favorite.id)

        if (existingFavorite != null) {
            favoriteRepository.delete(existingFavorite)
        } else {
            favoriteRepository.insert(favorite)
        }
    }

    suspend fun isEventFavorite(eventId: Int): Boolean {
        return favoriteRepository.getFavoriteById(eventId) != null
    }

    private fun convertEventToFavorite(event: Event): Favorite {
        return Favorite(
            id = event.id ?: 0,
            title = event.name,
            category = event.category,
            ownerName = event.ownerName,
            cityName = event.cityName,
            description = event.description,
            beginTime = event.beginTime,
            endTime = event.endTime,
            link = event.link,
            quota = event.quota,
            registrants = event.registrants,
            mediaCover = event.mediaCover
        )
    }
}
