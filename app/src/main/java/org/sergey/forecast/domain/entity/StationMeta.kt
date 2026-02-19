package org.sergey.forecast.domain.entity

import androidx.compose.runtime.Immutable

@Immutable
data class StationMeta(
    val stationId: String,
    val name: Map<String, String>?,
    val country: String?,
    val region: String?,
    val nationalId: String?,
    val wmoId: String?,
    val icaoId: String?,
    val latitude: Double?,
    val longitude: Double?,
    val elevation: Int?,
    val timezone: String?,
)
