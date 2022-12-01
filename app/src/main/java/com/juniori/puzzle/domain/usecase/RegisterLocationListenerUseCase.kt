package com.juniori.puzzle.domain.usecase

import androidx.core.location.LocationListenerCompat
import com.juniori.puzzle.domain.repository.LocationRepository
import javax.inject.Inject

class RegisterLocationListenerUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    operator fun invoke(listener: LocationListenerCompat) =
        repository.registerLocationListener(listener)
}