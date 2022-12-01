package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.repository.VideoRepository
import javax.inject.Inject

class DeleteVideoUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    suspend operator fun invoke(documentId: String): Resource<Unit> =
        videoRepository.deleteVideo(documentId)
}
