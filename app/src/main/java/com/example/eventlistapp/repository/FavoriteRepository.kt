package com.example.eventlistapp.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.eventlistapp.data.local.entity.Favorite
import com.example.eventlistapp.data.local.room.FavoriteDao
import com.example.eventlistapp.data.local.room.FavoriteRoomDatabase

class FavoriteRepository(application: Application) {
    private val mFavoriteDao: FavoriteDao

    init {
        val db = FavoriteRoomDatabase.getDatabase(application)
        mFavoriteDao = db.favoriteDao()
    }


    suspend fun getFavoriteById(eventId: Int): Favorite? {
        return mFavoriteDao.getFavoriteById(eventId)
    }

    suspend fun insert(favorite: Favorite) {
        mFavoriteDao.insert(favorite)
    }

    suspend fun delete(favorite: Favorite) {
        mFavoriteDao.delete(favorite)
    }

    // New function to return LiveData
    fun getAllFavoritesLiveData(): LiveData<List<Favorite>> {
        return mFavoriteDao.getAllNotesLiveData() // Make sure your DAO has this method
    }
}


