package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.domain.repository.VideoRepository
import javax.inject.Inject

class FetchMyNextVideosUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    suspend operator fun invoke(uid: String?, start: Int, query: String) {
        videoRepository.fetchMyNextVideos(uid, start, query)
    }
}
