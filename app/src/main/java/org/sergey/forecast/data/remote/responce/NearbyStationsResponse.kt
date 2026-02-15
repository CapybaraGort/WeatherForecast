package org.sergey.forecast.data.remote.responce

import org.sergey.forecast.data.remote.dto.StationDto

data class NearbyStationsResponse(
    val data: List<StationDto>?
)
