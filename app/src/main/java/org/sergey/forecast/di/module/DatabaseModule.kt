package org.sergey.forecast.di.module

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.sergey.forecast.data.local.database.AppDatabase
import org.sergey.forecast.data.local.database.dao.DailyWeatherCacheDao
import org.sergey.forecast.data.local.database.dao.StationCacheDao
import org.sergey.forecast.data.local.database.dao.StationMetaDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "forecast_db"
        ).fallbackToDestructiveMigration(true).build()

    @Provides
    @Singleton
    fun provideStationMetaDao(db: AppDatabase): StationMetaDao = db.stationMetaDao()

    @Provides
    @Singleton
    fun provideDailyWeatherCacheDao(db: AppDatabase): DailyWeatherCacheDao = db.dailyWeatherCacheDao()

    @Provides
    @Singleton
    fun provideStationCacheDao(db: AppDatabase): StationCacheDao = db.stationCacheDao()
}
