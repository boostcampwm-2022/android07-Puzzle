package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.data.weather.WeatherRepository
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(latitude: Double, longitude: Double) = weatherRepository.getWeather(latitude, longitude)
}