package com.example.eventlistapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.eventlistapp.data.local.entity.Favorite

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite") // Adjust table name if necessary
    fun getAllNotesLiveData(): LiveData<List<Favorite>>

    @Query("SELECT * FROM favorite WHERE id = :eventId LIMIT 1")
    suspend fun getFavoriteById(eventId: Int): Favorite?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: Favorite)

    @Delete
    suspend fun delete(favorite: Favorite)

    @Update
    suspend fun update(favorite: Favorite)
}

