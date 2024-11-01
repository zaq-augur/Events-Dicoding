package com.zaqly.eventdicoding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zaqly.eventdicoding.room.FavoriteEventDao
import com.zaqly.eventdicoding.ui.favorite.FavoriteViewModel

class DetailViewModelFactory( private val dao: FavoriteEventDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailEventViewModel::class.java)) {
            return DetailEventViewModel(dao) as T
        }
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            return FavoriteViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}