package org.sergey.forecast.domain.entity

import androidx.compose.runtime.Immutable

@Immutable
data class Station(
    val id: String,
    val name: Map<String, String>?,
    val distance: Double?
)
