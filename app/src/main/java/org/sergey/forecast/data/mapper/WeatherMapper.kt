package org.sergey.forecast.data.mapper

import org.sergey.forecast.data.local.database.entity.DailyWeatherCacheEntity
import org.sergey.forecast.data.remote.dto.DailyWeatherDto
import org.sergey.forecast.domain.entity.DailyWeather

fun DailyWeatherDto.toDomain(): DailyWeather =
    DailyWeather(
        date = date.substringBefore(" "),
        tavg = tavg,
        tmin = tmin,
        tmax = tmax,
        prcp = prcp
    )

fun DailyWeatherCacheEntity.toDomain(): DailyWeather =
    DailyWeather(
        date = date,
        tavg = tavg,
        tmin = tmin,
        tmax = tmax,
        prcp = prcp
    )

fun DailyWeather.toEntity(stationId: String) = DailyWeatherCacheEntity(
    stationId = stationId,
    date = date,
    tavg = tavg,
    tmin = tmin,
    tmax = tmax,
    prcp = prcp
)
