package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.domain.repository.VideoRepository
import com.juniori.puzzle.util.VideoFetchingState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetMyVideoFetchingStateUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    operator fun invoke(): StateFlow<VideoFetchingState> =
        videoRepository.myVideoFetchingState
}
