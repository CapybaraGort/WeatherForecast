package org.sergey.forecast.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.sergey.forecast.data.repository.MeteostatRepositoryImpl
import org.sergey.forecast.domain.repository.MeteostatRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMeteostatRepository(
        impl: MeteostatRepositoryImpl
    ): MeteostatRepository
}