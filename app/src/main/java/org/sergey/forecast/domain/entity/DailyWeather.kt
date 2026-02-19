package org.sergey.forecast.domain.entity

import androidx.compose.runtime.Immutable

@Immutable
data class DailyWeather(
    val date: String,
    val tavg: Double?,
    val tmin: Double?,
    val tmax: Double?,
    val prcp: Double?
) {
    fun isEmpty(): Boolean = tavg == null && tmin == null && tmax == null && prcp == null
}

