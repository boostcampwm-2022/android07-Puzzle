package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.domain.repository.VideoRepository
import javax.inject.Inject

class GetSearchedMyVideoUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    suspend operator fun invoke(uid: String, index: Int, keyword: String) = videoRepository.getSearchedMyVideoList(uid, index, keyword)
}