package com.juniori.puzzle.data.weather

data class WeatherSealedResponse(
    val response: WeatherResponse
)

data class WeatherResponse(
    val header: WeatherHeaderResponse,
    val body: WeatherBodyResponse
)

data class WeatherHeaderResponse(
    val resultCode: String,
    val resultMsg: String
)

data class WeatherBodyResponse(
    val dataType: String,
    val items: WeatherBodyItemsResponse,
    val pageNo: Int,
    val numOfRows: Int,
    val totalCount: Int
)

data class WeatherBodyItemsResponse(
    val item: List<WeatherItemResponse>
)

data class WeatherItemResponse(
    val baseDate: String,
    val baseTime: String,
    val category: String,
    val fcstDate: String,
    val fcstTime: String,
    val fcstValue: String,
    val nx: Int,
    val ny: Int
)