package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.repository.AuthRepository
import com.juniori.puzzle.domain.repository.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateNicknameUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val videoRepository: VideoRepository
){
    suspend operator fun invoke(newNickname: String): Resource<UserInfoEntity> {
        if (newNickname.isBlank()) {
            return Resource.Failure(Exception())
        }

        val newInfo = withContext(Dispatchers.IO) {
            authRepository.updateNickname(newNickname)
        }

        return if (newInfo is Resource.Success) {
            videoRepository.updateServerNickname(newInfo.result)
        } else {
            Resource.Failure(Exception())
        }
    }
}