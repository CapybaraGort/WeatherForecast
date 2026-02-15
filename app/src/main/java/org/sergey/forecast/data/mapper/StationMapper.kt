package org.sergey.forecast.data.mapper

import org.sergey.forecast.data.remote.dto.StationDto
import org.sergey.forecast.domain.entity.Station

fun StationDto.toDomain(): Station =
    Station(id, name, distance)