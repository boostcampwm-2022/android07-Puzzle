package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.domain.repository.LocationRepository
import javax.inject.Inject

class GetAddressUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    operator fun invoke(lat: Double, long: Double) = repository.getAddressInfo(lat, long)
}