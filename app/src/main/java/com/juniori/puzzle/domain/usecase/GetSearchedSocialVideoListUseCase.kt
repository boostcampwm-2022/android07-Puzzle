package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.di.MockData
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.repository.VideoRepository
import com.juniori.puzzle.util.SortType
import javax.inject.Inject

class GetSearchedSocialVideoListUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    suspend operator fun invoke(index: Int, keyword: String, order: SortType): Resource<List<VideoInfoEntity>> =
        videoRepository.getSearchedSocialVideoList(index, order, keyword)
}