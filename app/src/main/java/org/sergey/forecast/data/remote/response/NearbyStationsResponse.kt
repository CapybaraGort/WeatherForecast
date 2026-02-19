package org.sergey.forecast.data.remote.response

import org.sergey.forecast.data.remote.dto.StationDto

data class NearbyStationsResponse(
    val data: List<StationDto>?
)
