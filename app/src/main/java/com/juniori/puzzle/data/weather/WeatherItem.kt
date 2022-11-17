package com.juniori.puzzle.data.weather

data class WeatherItem(
    val temp: Float,
    val feelsLike: Float,
    val minTemp: Float,
    val maxTemp: Float,
    val description: String,
    val icon: String
)
