package com.juniori.puzzle.network

import com.juniori.puzzle.data.weather.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("/data/2.5/forecast")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("lang") lang: String = "kr",
        @Query("units") units: String = "metric",
        @Query("cnt") cnt: Int = 11
    ): Response<WeatherResponse>

}