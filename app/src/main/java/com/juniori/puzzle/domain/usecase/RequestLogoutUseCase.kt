package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.data.auth.AuthRepository
import javax.inject.Inject

class RequestLogoutUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke() = authRepository.requestLogout()
}