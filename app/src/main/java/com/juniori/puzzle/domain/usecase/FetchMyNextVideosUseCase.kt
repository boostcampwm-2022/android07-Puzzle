package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.ui.othersgallery.Repositoryk
import javax.inject.Inject

class FetchMyNextVideosUseCase @Inject constructor(private val videoRepository: Repositoryk) {
    suspend operator fun invoke(uid: String?, start: Int, query: String) {
        videoRepository.fetchMyNextVideos(uid, start, query)
    }
}
