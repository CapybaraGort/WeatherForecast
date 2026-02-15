package org.sergey.forecast.data.remote

import org.sergey.forecast.data.remote.responce.NearbyStationsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MeteostatApi {

    @GET("stations/nearby")
    suspend fun getNearbyStations(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("radius") radius: Int,
        @Query("limit") limit: Int = 10
    ): NearbyStationsResponse
}
