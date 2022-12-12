package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.ui.othersgallery.Repositoryk
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetMyVideosUseCase @Inject constructor(private val videoRepository: Repositoryk) {
    operator fun invoke(): StateFlow<List<VideoInfoEntity>> =
        videoRepository.myVideoList
}
