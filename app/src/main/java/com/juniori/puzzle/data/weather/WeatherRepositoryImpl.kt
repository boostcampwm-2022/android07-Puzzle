package com.juniori.puzzle.data.weather

import com.juniori.puzzle.BuildConfig
import com.juniori.puzzle.network.WeatherService
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val service: WeatherService
) : WeatherRepository {
    override suspend fun getWeather(lat: Double, lon: Double): Boolean {
        val response = service.getWeather(lat,lon,SERVICE_KEY)

        return response.isSuccessful
    }

    companion object {
        private const val SERVICE_KEY = BuildConfig.WEATHER_SERVICE_KEY
    }
}