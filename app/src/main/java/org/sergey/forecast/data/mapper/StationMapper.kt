package org.sergey.forecast.data.mapper

import org.sergey.forecast.data.local.database.entity.StationCacheEntity
import org.sergey.forecast.data.remote.dto.StationDto
import org.sergey.forecast.data.remote.dto.StationMetaDto
import org.sergey.forecast.domain.entity.Station
import org.sergey.forecast.domain.entity.StationMeta

fun StationDto.toDomain(): Station =
    Station(id, name, distance)

fun StationMetaDto.toDomain(): StationMeta =
    StationMeta(
        stationId = id,
        name = name,
        country = country,
        region = region,
        nationalId = identifier?.national,
        wmoId = identifier?.wmo,
        icaoId = identifier?.icao,
        latitude = location?.latitude,
        longitude = location?.longitude,
        elevation = location?.elevation,
        timezone = timezone,
    )

fun Station.toEntity(
    cacheKey: String,
    lat: Double,
    lon: Double,
    distance: Double
) =
    StationCacheEntity(
        cacheKey = cacheKey,
        stationId = id,
        name = name?.values?.first() ?: "?",
        latitude = lat,
        longitude = lon,
        distance = distance,
        cachedAt = System.currentTimeMillis()
    )

fun StationCacheEntity.toDomain() = Station(
    id = stationId,
    name = mapOf("" to name),
    distance = distance
)

fun StationDto.toEntity(
    cacheKey: String,
    lat: Double,
    lon: Double,
) =
    StationCacheEntity(
        cacheKey = cacheKey,
        stationId = id,
        name = name?.values?.first() ?: "?",
        latitude = lat,
        longitude = lon,
        distance = distance,
        cachedAt = System.currentTimeMillis()
    )
