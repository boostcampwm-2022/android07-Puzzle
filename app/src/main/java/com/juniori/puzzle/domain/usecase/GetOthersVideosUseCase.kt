package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.repository.VideoRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetOthersVideosUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    operator fun invoke(): StateFlow<List<VideoInfoEntity>> =
        videoRepository.othersVideoList
}
