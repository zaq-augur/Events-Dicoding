package com.zaqly.eventdicoding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaqly.eventdicoding.api.model.DetailEventResponse
import com.zaqly.eventdicoding.api.model.Event
import com.zaqly.eventdicoding.api.service.ApiConfig
import com.zaqly.eventdicoding.room.FavoriteEvent
import com.zaqly.eventdicoding.room.FavoriteEventDao
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailEventViewModel(private val dao: FavoriteEventDao) : ViewModel() {

    private val _eventDetail = MutableLiveData<Event?>()
    val eventDetail: LiveData<Event?> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchEventDetails(eventId: String) {
        _isLoading.value = true
        ApiConfig.eventApiService.getEventDetails(eventId).enqueue(object :
            Callback<DetailEventResponse> {
            override fun onResponse(call: Call<DetailEventResponse>, response: Response<DetailEventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val event = response.body()?.event
                    if (event != null) {
                        _eventDetail.value = event
                    } else {
                        _errorMessage.value = "Event details are empty"
                    }
                } else {
                    _errorMessage.value = "Failed to fetch event details: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Network error: ${t.message}"
            }
        })
    }

    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent?> = dao.getFavoriteEventById(id)

    fun insertFavorite(favoriteEvent: FavoriteEvent) {
        viewModelScope.launch {
            dao.insertFavorite(favoriteEvent)
        }
    }

    fun deleteFavorite(favoriteEvent: FavoriteEvent) {
        viewModelScope.launch {
            dao.deleteFavorite(favoriteEvent)
        }
    }
}