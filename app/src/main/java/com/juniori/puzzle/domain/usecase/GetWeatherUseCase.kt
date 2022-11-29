package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.domain.repository.AuthRepository
import com.juniori.puzzle.domain.repository.LocationRepository
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    operator fun invoke() = locationRepository.getWeatherInfo()
}

