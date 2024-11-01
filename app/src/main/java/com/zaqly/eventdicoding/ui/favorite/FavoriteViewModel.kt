package com.zaqly.eventdicoding.ui.favorite


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaqly.eventdicoding.room.FavoriteEvent
import com.zaqly.eventdicoding.room.FavoriteEventDao
import kotlinx.coroutines.launch

class FavoriteViewModel(private val dao: FavoriteEventDao) : ViewModel() {

    fun getFavoriteEvents(): LiveData<List<FavoriteEvent>> {
        return dao.getAllFavorites()
    }
    fun removeFavorite(event: FavoriteEvent) {
        viewModelScope.launch {
            dao.deleteFavorite(event)
        }
    }

}