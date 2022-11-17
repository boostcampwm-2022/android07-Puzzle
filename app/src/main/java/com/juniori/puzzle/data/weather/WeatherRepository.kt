package com.juniori.puzzle.data.weather

interface WeatherRepository {
    suspend fun getWeather(nx: Int, ny: Int): Boolean
}