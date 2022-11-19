package com.juniori.puzzle.data.weather

import com.juniori.puzzle.BuildConfig
import com.juniori.puzzle.network.WeatherService
import com.juniori.puzzle.util.toItem
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val service: WeatherService
) : WeatherRepository {
    override suspend fun getWeather(lat: Double, lon: Double): Result<List<WeatherItem>> {
        val response = service.getWeather(lat,lon,SERVICE_KEY,"metric","kr")

        return if(response.isSuccessful){
            Result.success(response.body()?.toItem() ?: emptyList())
        }
        else{
            Result.failure(Exception("네트워크 통신에 실패했습니다"))
        }
    }

    companion object {
        private const val SERVICE_KEY = BuildConfig.WEATHER_SERVICE_KEY
    }
}