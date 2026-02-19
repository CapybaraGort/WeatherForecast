package org.sergey.forecast.data.remote

import org.sergey.forecast.data.remote.response.DailyWeatherResponse
import org.sergey.forecast.data.remote.response.NearbyStationsResponse
import org.sergey.forecast.data.remote.response.StationMetaResponse
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

    @GET("stations/meta")
    suspend fun getStationMeta(@Query("id") id: String): StationMetaResponse

    @GET("point/daily")
    suspend fun getDailyWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("start") start: String,
        @Query("end") end: String,
    ): DailyWeatherResponse
}

