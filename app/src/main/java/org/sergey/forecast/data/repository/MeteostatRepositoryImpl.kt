package org.sergey.forecast.data.repository

import android.util.Log
import org.sergey.forecast.data.mapper.toDomain
import org.sergey.forecast.data.remote.MeteostatApi
import org.sergey.forecast.domain.entity.Station
import org.sergey.forecast.domain.repository.MeteostatRepository
import javax.inject.Inject

class MeteostatRepositoryImpl @Inject constructor(
    private val api: MeteostatApi
) : MeteostatRepository {
    override suspend fun getNearbyStations(
        lat: Double,
        lon: Double,
        radius: Int,
        limit: Int
    ): Result<List<Station>> {
        try {
            val response = api.getNearbyStations(lat, lon, radius, limit)
            val stations = response.data?.map { it.toDomain() } ?: listOf()
            return Result.success(stations)
        } catch(e: Exception) {
            return Result.failure(e)
        }
    }
}