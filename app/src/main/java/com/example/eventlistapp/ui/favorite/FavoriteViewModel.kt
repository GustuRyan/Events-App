package com.example.eventlistapp.ui.favorite

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.eventlistapp.data.local.entity.Favorite
import com.example.eventlistapp.repository.FavoriteRepository

class FavoriteViewModel(application: Application) : ViewModel() {
    private val mFavoriteRepository: FavoriteRepository = FavoriteRepository(application)

    fun getAllFavoritesLiveData(): LiveData<List<Favorite>> {
        return mFavoriteRepository.getAllFavoritesLiveData()
    }
}
