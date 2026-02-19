package org.sergey.forecast.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.sergey.forecast.data.local.database.entity.StationMetaEntity

@Dao
interface StationMetaDao {

    @Query("SELECT * FROM station_meta WHERE stationId = :stationId")
    fun getByStationId(stationId: String): Flow<StationMetaEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: StationMetaEntity)
}
