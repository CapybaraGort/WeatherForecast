package org.sergey.forecast.data.local.database.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "daily_weather_cache",
    primaryKeys = ["stationId", "date"],
    indices = [Index(value = ["stationId", "date"])]
)
data class DailyWeatherCacheEntity(
    val stationId: String,
    val date: String,
    val tavg: Double?,
    val tmin: Double?,
    val tmax: Double?,
    val prcp: Double?
)