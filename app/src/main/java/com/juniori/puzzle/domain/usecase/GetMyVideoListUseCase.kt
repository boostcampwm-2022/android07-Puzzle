package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.repository.VideoRepository
import com.juniori.puzzle.di.MockData
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import javax.inject.Inject

class GetMyVideoListUseCase @Inject constructor(@MockData private val videoRepository: VideoRepository) {
    suspend operator fun invoke(uid: String, index: Int): Resource<List<VideoInfoEntity>> = videoRepository.getMyVideoList(uid, index)
}