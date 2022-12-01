package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.domain.repository.LocationRepository
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(lat: Double, long: Double) =
        locationRepository.getWeatherInfo(lat, long)
}

