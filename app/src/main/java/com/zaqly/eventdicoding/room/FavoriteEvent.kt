package com.zaqly.eventdicoding.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FavoriteEvent")
data class FavoriteEvent(
    @PrimaryKey(autoGenerate = false)
    var id: String = "",
    var name: String = "",
    var mediaCover: String? = null,
)

