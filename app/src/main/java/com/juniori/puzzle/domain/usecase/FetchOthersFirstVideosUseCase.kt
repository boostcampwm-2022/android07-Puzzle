package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.ui.othersgallery.Repositoryk
import com.juniori.puzzle.util.SortType
import javax.inject.Inject

class FetchOthersFirstVideosUseCase @Inject constructor(private val videoRepository: Repositoryk) {
    suspend operator fun invoke(query: String, sortType: SortType) {
        videoRepository.fetchOthersFirstPage(query, sortType)
    }
}
