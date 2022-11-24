package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateNicknameUseCase @Inject constructor(private val authRepository: AuthRepository){
    suspend operator fun invoke(newNickname: String) = authRepository.updateNickname(newNickname)
}