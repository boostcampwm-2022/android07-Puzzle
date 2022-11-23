package com.juniori.puzzle.data.weather

import com.juniori.puzzle.BuildConfig
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.network.WeatherService
import com.juniori.puzzle.util.toItem
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val service: WeatherService
) : WeatherRepository {
    override suspend fun getWeather(lat: Double, lon: Double): Resource<List<WeatherItem>> {
        val response = service.getWeather(lat, lon, SERVICE_KEY)

        return try {
            Resource.Success(response.body()?.toItem() ?: emptyList())
        } catch (e: Exception) {
            Resource.Failure(Exception())
        }
    }

    companion object {
        private const val SERVICE_KEY = BuildConfig.WEATHER_SERVICE_KEY
    }
}