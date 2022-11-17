package com.juniori.puzzle.network

import com.juniori.puzzle.data.weather.WeatherSealedResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst")
    suspend fun getWeather(
        @Query("serviceKey") serviceKey: String,
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int,
        @Query("dataType") dataType: String,
        @Query("base_date") baseDate: String,
        @Query("base_time") baseTime: String,
        @Query("nx") nx: Int,
        @Query("ny") ny: Int
    ): Response<WeatherSealedResponse>
}