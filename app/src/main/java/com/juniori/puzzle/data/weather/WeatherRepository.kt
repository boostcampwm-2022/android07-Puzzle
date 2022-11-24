package com.juniori.puzzle.data.weather

import com.juniori.puzzle.data.Resource

interface WeatherRepository {
    suspend fun getWeather(lat: Double, lon: Double): Resource<List<WeatherItem>>
}