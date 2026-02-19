package org.sergey.forecast.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.sergey.forecast.data.local.database.dao.DailyWeatherCacheDao
import org.sergey.forecast.data.local.database.dao.StationCacheDao
import org.sergey.forecast.data.local.database.dao.StationMetaDao
import org.sergey.forecast.data.mapper.toDomain
import org.sergey.forecast.data.mapper.toEntity
import org.sergey.forecast.data.remote.MeteostatApi
import org.sergey.forecast.domain.entity.DailyWeather
import org.sergey.forecast.domain.entity.Station
import org.sergey.forecast.domain.entity.StationMeta
import org.sergey.forecast.domain.repository.WeatherRepository
import javax.inject.Inject
import kotlin.math.roundToInt

class WeatherRepositoryImpl @Inject constructor(
    private val api: MeteostatApi,
    private val stationMetaDao: StationMetaDao,
    private val dailyWeatherCacheDao: DailyWeatherCacheDao,
) : WeatherRepository {

    override fun getStationMetaCache(stationId: String): Flow<StationMeta?> =
        stationMetaDao.getByStationId(stationId).map { it?.toDomain() }

    override suspend fun fetchStationMeta(stationId: String): Result<StationMeta?> {
        return try {
            val response = api.getStationMeta(stationId)
            val meta = response.data?.toDomain()
            meta?.let { stationMetaDao.insert(it.toEntity()) }
            Result.success(meta)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getDailyWeatherCache(
        stationId: String,
        start: String,
        end: String
    ): Flow<List<DailyWeather>> {
        return dailyWeatherCacheDao.getDailyWeather(stationId, start, end)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getDailyWeather(
        stationId: String,
        start: String,
        end: String
    ): Result<List<DailyWeather>> {
        return try {
            val response = api.getDailyWeather(stationId, start, end)
            val data = response.data?.map { it.toDomain() } ?: emptyList()
            if (data.isNotEmpty()) {
                dailyWeatherCacheDao.upsertAll(data.map { it.toEntity(stationId) })
            }
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}