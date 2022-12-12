package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.ui.othersgallery.Repositoryk
import javax.inject.Inject

class FetchMyFirstVideosUseCase @Inject constructor(private val videoRepository: Repositoryk) {
    suspend operator fun invoke(uid: String?, query: String) {
        videoRepository.fetchMyData(uid, query)
    }
}
