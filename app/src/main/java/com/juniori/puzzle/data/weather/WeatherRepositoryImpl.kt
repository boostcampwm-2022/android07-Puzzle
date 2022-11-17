package com.juniori.puzzle.data.weather

import com.juniori.puzzle.BuildConfig
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.network.WeatherService
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val service: WeatherService
) : WeatherRepository {
    override suspend fun getWeather(nx: Int, ny: Int): Boolean {
        val response = service.getWeather(SERVICE_KEY, 1, 1000,
            "JSON", "202221117", "1130", nx, ny)

        return response.isSuccessful
    }

    companion object {
        private const val SERVICE_KEY = BuildConfig.WEATHER_SERVICE_KEY
    }
}