package org.sergey.forecast.domain.repository

import kotlinx.coroutines.flow.Flow
import org.sergey.forecast.domain.entity.DailyWeather
import org.sergey.forecast.domain.entity.StationMeta

interface WeatherRepository {

    fun getStationMetaCache(stationId: String): Flow<StationMeta?>
    suspend fun fetchStationMeta(stationId: String): Result<StationMeta?>

    fun getDailyWeatherCache(stationId: String, start: String, end: String): Flow<List<DailyWeather>>

    suspend fun getDailyWeather(
        stationId: String,
        lat: Double,
        lon: Double,
        start: String,
        end: String
    ): Result<List<DailyWeather>>

}