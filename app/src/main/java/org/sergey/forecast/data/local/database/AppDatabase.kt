package org.sergey.forecast.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.sergey.forecast.data.local.database.dao.DailyWeatherCacheDao
import org.sergey.forecast.data.local.database.dao.StationCacheDao
import org.sergey.forecast.data.local.database.dao.StationMetaDao
import org.sergey.forecast.data.local.database.entity.DailyWeatherCacheEntity
import org.sergey.forecast.data.local.database.entity.StationCacheEntity
import org.sergey.forecast.data.local.database.entity.StationMetaEntity

@Database(
    entities = [
        StationMetaEntity::class,
        DailyWeatherCacheEntity::class,
        StationCacheEntity::class
    ],
    version = 8,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stationMetaDao(): StationMetaDao
    abstract fun dailyWeatherCacheDao(): DailyWeatherCacheDao
    abstract fun stationCacheDao(): StationCacheDao
}
