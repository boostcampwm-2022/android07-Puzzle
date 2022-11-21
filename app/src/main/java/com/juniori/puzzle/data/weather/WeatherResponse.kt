package com.juniori.puzzle.data.weather

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherListResponse>,
    val city: WeatherCityResponse
)

data class WeatherListResponse(
    val dt: Long,
    val main: WeatherMainResponse,
    val weather: List<WeatherWeatherResponse>,
    val cloud: WeatherCloudResponse,
    val wind: WeatherWindResponse,
    val visibility: Int,
    val pop: Float,
    val rain: WeatherRainResponse,
    val sys: WeatherSysResponse,
    @SerializedName("dt_txt") val dtTxt: String
)

data class WeatherMainResponse(
    val temp: Float,
    @SerializedName("feels_like") val feelsLike: Float,
    @SerializedName("temp_min") val tempMin: Float,
    @SerializedName("temp_max") val tempMax: Float,
    val pressure: Int,
    @SerializedName("sea_level") val seaLevel: Int,
    @SerializedName("grnd_level") val groundLevel: Int,
    val humidity: Int,
    @SerializedName("temp_kf") val tempKf: Float,

    )

data class WeatherWeatherResponse(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class WeatherCloudResponse(
    val all: Int
)

data class WeatherWindResponse(
    val speed: Float,
    val deg: Int,
    val gust: Float
)

data class WeatherRainResponse(
    @SerializedName("3h") val threeH: Float
)

data class WeatherSysResponse(
    val pod: String
)

data class WeatherCityResponse(
    val id: Int,
    val name: String,
    val coord: WeatherCoordResponse,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

data class WeatherCoordResponse(
    val lat: Float,
    val lon: Float
)