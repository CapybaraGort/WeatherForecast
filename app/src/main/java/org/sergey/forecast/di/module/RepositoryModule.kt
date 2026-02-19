package org.sergey.forecast.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.sergey.forecast.data.repository.StationRepositoryImpl
import org.sergey.forecast.data.repository.WeatherRepositoryImpl
import org.sergey.forecast.domain.repository.StationRepository
import org.sergey.forecast.domain.repository.WeatherRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        impl: WeatherRepositoryImpl
    ): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindStationRepository(
        impl: StationRepositoryImpl
    ): StationRepository
}