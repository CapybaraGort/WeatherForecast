package org.sergey.forecast.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.sergey.forecast.data.local.database.entity.StationCacheEntity

@Dao
interface StationCacheDao {

    @Query("SELECT * FROM station_cache WHERE cacheKey = :key")
    suspend fun getByKey(key: String): List<StationCacheEntity>

    @Upsert
    suspend fun upsertAll(stations: List<StationCacheEntity>)

    @Query("DELETE FROM station_cache WHERE cacheKey = :key")
    suspend fun deleteByKey(key: String)
}