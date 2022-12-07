package com.juniori.puzzle.data.weather

import com.juniori.puzzle.data.Resource

class WeatherRepositoryMockImpl(private val status: Resource<List<WeatherItem>>): WeatherRepository {
    override suspend fun getWeather(lat: Double, lon: Double): Resource<List<WeatherItem>> = status
}