package org.sergey.forecast.data.remote.dto

data class StationMetaDto(
    val id: String,
    val name: Map<String, String>?,
    val country: String?,
    val region: String?,
    val identifier: StationIdentifierDto?,
    val location: StationLocationDto?,
    val timezone: String?,
)

data class StationIdentifierDto(
    val national: String?,
    val wmo: String?,
    val icao: String?
)

data class StationLocationDto(
    val latitude: Double?,
    val longitude: Double?,
    val elevation: Int?
)
