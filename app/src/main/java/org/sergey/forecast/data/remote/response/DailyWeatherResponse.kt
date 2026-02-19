package org.sergey.forecast.data.remote.response

import org.sergey.forecast.data.remote.dto.DailyWeatherDto

data class DailyWeatherResponse(
    val data: List<DailyWeatherDto>?
)

