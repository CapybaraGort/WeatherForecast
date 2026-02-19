package org.sergey.forecast.data.repository

import org.sergey.forecast.data.local.database.dao.StationCacheDao
import org.sergey.forecast.data.mapper.toDomain
import org.sergey.forecast.data.mapper.toEntity
import org.sergey.forecast.data.remote.MeteostatApi
import org.sergey.forecast.domain.entity.Station
import org.sergey.forecast.domain.repository.StationRepository
import javax.inject.Inject
import kotlin.math.roundToInt

class StationRepositoryImpl @Inject constructor(
    private val api: MeteostatApi,
    private val stationCacheDao: StationCacheDao
): StationRepository {
    override suspend fun getNearbyStations(
        lat: Double,
        lon: Double,
        radius: Int,
        limit: Int
    ): Result<List<Station>> {
        val key = cacheKey(lat, lon, radius)
        return try {

            val cached = stationCacheDao.getByKey(key)
            val isFresh = cached.isNotEmpty() &&
                    (System.currentTimeMillis() - cached.first().cachedAt) < CACHE_TTL_MS

            if (isFresh) {
                return Result.success(cached.map { it.toDomain() })
            }

            val response = api.getNearbyStations(lat, lon, radius, limit)
            val stations = response.data ?: emptyList()

            stationCacheDao.deleteByKey(key)
            if (stations.isNotEmpty()) {
                stationCacheDao.upsertAll(stations.map { it.toEntity(
                    cacheKey = key,
                    lat = lat,
                    lon = lon,
                ) })
            }

            Result.success(stations.map { it.toDomain() })
        } catch (e: Exception) {
            val stale = stationCacheDao.getByKey(key)
            if (stale.isNotEmpty()) {
                Result.success(stale.map { it.toDomain() })
            } else {
                Result.failure(e)
            }
        }
    }

    companion object {
        private const val CACHE_TTL_MS = 60 * 60 * 1000L
        private fun cacheKey(lat: Double, lon: Double, radius: Int): String {
            val roundedLat = (lat * 100).roundToInt() / 100.0
            val roundedLon = (lon * 100).roundToInt() / 100.0
            return "$roundedLat,$roundedLon,$radius"
        }
    }
}