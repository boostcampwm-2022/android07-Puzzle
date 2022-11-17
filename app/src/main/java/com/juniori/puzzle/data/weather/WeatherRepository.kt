package com.juniori.puzzle.data.weather

interface WeatherRepository {
    suspend fun getWeather(lat: Double, lon: Double): Boolean
}