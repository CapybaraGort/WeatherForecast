package org.sergey.forecast.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import org.sergey.forecast.data.local.database.entity.DailyWeatherCacheEntity

@Dao
interface DailyWeatherCacheDao {

    @Query(
        "SELECT * FROM daily_weather_cache " +
                "WHERE stationId = :stationId AND date BETWEEN :startDate AND :endDate " +
                "ORDER BY date"
    )
    fun getDailyWeather(
        stationId: String,
        startDate: String,
        endDate: String
    ): Flow<List<DailyWeatherCacheEntity>>

    @Upsert
    suspend fun upsertAll(entities: List<DailyWeatherCacheEntity>)

    @Query(
        "DELETE FROM daily_weather_cache " +
                "WHERE stationId = :stationId AND date BETWEEN :startDate AND :endDate"
    )
    suspend fun deleteRange(stationId: String, startDate: String, endDate: String)
}