package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.domain.repository.VideoRepository
import com.juniori.puzzle.util.SortType
import javax.inject.Inject

class FetchOthersNextVideosUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    suspend operator fun invoke(query: String, sortType: SortType) {
        videoRepository.fetchOthersNextVideos(query, sortType)
    }
}
