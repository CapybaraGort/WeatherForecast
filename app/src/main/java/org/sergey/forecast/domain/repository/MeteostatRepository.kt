package org.sergey.forecast.domain.repository

import org.sergey.forecast.domain.entity.Station

interface MeteostatRepository {
    suspend fun getNearbyStations(
        lat: Double,
        lon: Double,
        radius: Int,
        limit: Int
    ): Result<List<Station>>
}