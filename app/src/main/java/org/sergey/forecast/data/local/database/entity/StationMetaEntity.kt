package org.sergey.forecast.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "station_meta")
data class StationMetaEntity(
    @PrimaryKey val id: Int = 0,
    val stationId: String,
    val name: String?,
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
