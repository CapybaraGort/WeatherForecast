package org.sergey.forecast.data.mapper

import org.sergey.forecast.data.local.database.entity.StationMetaEntity
import org.sergey.forecast.domain.entity.StationMeta

fun StationMetaEntity.toDomain(): StationMeta =
    StationMeta(
        stationId = stationId,
        name = mapOf("" to name.toString()),
        country = country,
        region = region,
        nationalId = nationalId,
        wmoId = wmoId,
        icaoId = icaoId,
        latitude = latitude,
        longitude = longitude,
        elevation = elevation,
        timezone = timezone,
    )

fun StationMeta.toEntity(): StationMetaEntity =
    StationMetaEntity(
        stationId = stationId,
        name = name?.values?.first().toString(),
        country = country,
        region = region,
        nationalId = nationalId,
        wmoId = wmoId,
        icaoId = icaoId,
        latitude = latitude,
        longitude = longitude,
        elevation = elevation,
        timezone = timezone,
    )