package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.domain.repository.VideoRepository
import javax.inject.Inject

class FetchMyFirstVideosUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    suspend operator fun invoke(uid: String?, query: String) {
        videoRepository.fetchMyFirstPageVideos(uid, query)
    }
}
