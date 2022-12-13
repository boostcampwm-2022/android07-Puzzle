package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.repository.VideoRepository
import com.juniori.puzzle.util.SortType
import javax.inject.Inject

class GetSocialVideoListUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    suspend operator fun invoke(
        index: Int,
        order: SortType,
        latestData: Long? = null
    ): Resource<List<VideoInfoEntity>> =
        videoRepository.getSocialVideoList(index, order, latestData)
}