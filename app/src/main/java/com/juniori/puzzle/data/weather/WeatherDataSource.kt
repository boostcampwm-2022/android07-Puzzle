package com.juniori.puzzle.data.weather

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.WeatherEntity

interface WeatherDataSource {
    suspend fun getWeather(lat: Double, lon: Double): Resource<List<WeatherEntity>>
}