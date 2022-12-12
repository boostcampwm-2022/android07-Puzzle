package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.ui.othersgallery.Repositoryk
import com.juniori.puzzle.ui.othersgallery.VideoFetchingState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetOthersVideoFetchingStateUseCase @Inject constructor(private val videoRepository: Repositoryk) {
    operator fun invoke(): StateFlow<VideoFetchingState> =
        videoRepository.othersVideoFetchingState
}
