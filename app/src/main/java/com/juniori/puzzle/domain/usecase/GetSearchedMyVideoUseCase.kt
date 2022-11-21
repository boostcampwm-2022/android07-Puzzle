package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.domain.repository.VideoRepository
import com.juniori.puzzle.di.MockData
import javax.inject.Inject

class GetSearchedMyVideoUseCase @Inject constructor(@MockData private val videoRepository: VideoRepository) {
    suspend operator fun invoke(uid: String, index: Int, keyword: String) = videoRepository.getSearchedMyVideoList(uid, index, keyword)
}