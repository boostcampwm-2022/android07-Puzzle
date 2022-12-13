package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.repository.VideoRepository
import javax.inject.Inject

class GetUserInfoByUidUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    suspend operator fun invoke(uid: String): Resource<UserInfoEntity> =
        videoRepository.getUserInfoByUidUseCase(uid)
}
