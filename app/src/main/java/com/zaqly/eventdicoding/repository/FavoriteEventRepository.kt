package com.zaqly.eventdicoding.repository

import androidx.lifecycle.LiveData
import com.zaqly.eventdicoding.room.FavoriteEvent
import com.zaqly.eventdicoding.room.FavoriteEventDao

class FavoriteEventRepository ( val favoriteEventDao: FavoriteEventDao) {

    suspend fun insertFavorite(favoriteEvent: FavoriteEvent) {
        favoriteEventDao.insertFavorite(favoriteEvent)
    }
    suspend fun deleteFavorite(favoriteEvent: FavoriteEvent) {
        favoriteEventDao.deleteFavorite(favoriteEvent)
    }

    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent?> {
        return favoriteEventDao.getFavoriteEventById(id)
    }

    fun getAllFavorites(): LiveData<List<FavoriteEvent>> {
        return favoriteEventDao.getAllFavorites()
    }
}