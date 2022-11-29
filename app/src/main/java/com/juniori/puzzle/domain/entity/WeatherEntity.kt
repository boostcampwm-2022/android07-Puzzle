package com.juniori.puzzle.domain.entity

import java.util.*

data class WeatherEntity(
    val date: Date,
    val temp: Int,
    val feelsLike: Int,
    val minTemp: Int,
    val maxTemp: Int,
    val description: String,
    val icon: String
)