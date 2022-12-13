package com.juniori.puzzle.data.weather

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.WeatherEntity
import com.juniori.puzzle.network.WeatherService
import com.juniori.puzzle.util.WEATHER_SERVICE_KEY
import com.juniori.puzzle.util.toItem
import javax.inject.Inject

class WeatherDataSourceImpl @Inject constructor(
    private val service: WeatherService
) : WeatherDataSource {

    override suspend fun getWeather(lat: Double, lon: Double): Resource<List<WeatherEntity>> {
        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) return Resource.Failure(Exception())

        return try {
            val response = service.getWeather(lat, lon, WEATHER_SERVICE_KEY)
            Resource.Success(response.body()?.toItem() ?: emptyList())
        } catch (e: Exception) {
            Resource.Failure(Exception())
        }
    }

}