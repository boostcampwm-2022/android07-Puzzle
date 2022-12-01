package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.domain.repository.LocationRepository
import javax.inject.Inject

class UnregisterLocationListenerUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    operator fun invoke() = repository.unregisterLocationListener()
}