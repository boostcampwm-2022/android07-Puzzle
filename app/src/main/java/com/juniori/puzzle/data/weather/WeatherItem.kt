package com.juniori.puzzle.data.weather

data class WeatherItem(
    val fullDate: String,
    val time: String,
    val temp: Int,
    val feelsLike: Int,
    val minTemp: Int,
    val maxTemp: Int,
    val description: String,
    val icon: String
)
