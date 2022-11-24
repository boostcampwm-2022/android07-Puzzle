package com.juniori.puzzle.data.weather

import java.util.*

data class WeatherItem(
    val date: Date,
    val temp: Int,
    val feelsLike: Int,
    val minTemp: Int,
    val maxTemp: Int,
    val description: String,
    val icon: String
)
