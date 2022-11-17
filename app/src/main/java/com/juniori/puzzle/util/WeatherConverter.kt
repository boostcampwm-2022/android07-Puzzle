package com.juniori.puzzle.util

import com.juniori.puzzle.data.weather.WeatherItem
import com.juniori.puzzle.data.weather.WeatherResponse

fun WeatherResponse.toItem():List<WeatherItem>{
    return list.map {
        WeatherItem(
            temp = it.main.temp,
            feelsLike = it.main.feelsLike,
            minTemp = it.main.tempMin,
            maxTemp = it.main.tempMax,
            description = it.weather[0].description,
            icon = it.weather[0].icon
        )
    }
}