package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.repository.VideoRepository
import javax.inject.Inject

class UpdateServerNicknameUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    suspend operator fun invoke(userInfoEntity: UserInfoEntity) = videoRepository.updateServerNickname(userInfoEntity)
}