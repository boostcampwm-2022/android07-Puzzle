package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.auth.AuthRepository
import com.juniori.puzzle.domain.entity.UserInfoEntity
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke() = authRepository.getCurrentUserInfo()
}