package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.repository.VideoRepository
import com.juniori.puzzle.util.GalleryType
import javax.inject.Inject

class DeleteVideoUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    suspend operator fun invoke(documentId: String, galleryType: GalleryType): Resource<Unit> =
        videoRepository.deleteVideo(documentId, galleryType)
}
