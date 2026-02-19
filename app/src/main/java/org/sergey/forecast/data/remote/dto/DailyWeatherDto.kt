package org.sergey.forecast.data.remote.dto

data class DailyWeatherDto(
    val date: String,
    val tavg: Double?,
    val tmin: Double?,
    val tmax: Double?,
    val prcp: Double?
)

