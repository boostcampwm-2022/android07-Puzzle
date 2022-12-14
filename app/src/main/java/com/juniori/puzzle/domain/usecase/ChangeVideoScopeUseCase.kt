package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.repository.VideoRepository
import com.juniori.puzzle.util.GalleryType
import javax.inject.Inject

class ChangeVideoScopeUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    suspend operator fun invoke(
        documentInfo: VideoInfoEntity,
        galleryType: GalleryType
    ): Resource<VideoInfoEntity> =
        videoRepository.changeVideoScope(documentInfo, galleryType)
}
