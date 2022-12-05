package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.repository.VideoRepository
import javax.inject.Inject

class PostUserInfoUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    suspend operator fun invoke(
        uid: String,
        nickname: String,
        profileImage: String
    ): Resource<UserInfoEntity> = videoRepository.postUserInfoInFirestore(
        uid, nickname, profileImage
    )
}
