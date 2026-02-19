package org.sergey.forecast.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "station_cache")
data class StationCacheEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cacheKey: String,
    val stationId: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val distance: Double?,
    val cachedAt: Long
)