package org.sergey.forecast.data.remote.dto

data class StationDto(
    val id: String,
    val name: Map<String, String>?,
    val distance: Double?
)
