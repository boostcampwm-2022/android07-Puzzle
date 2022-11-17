package com.juniori.puzzle.data.firebase

import com.juniori.puzzle.data.firebase.dto.ListDocumentsResponseDTO
import com.juniori.puzzle.data.firebase.dto.RunQueryRequestDTO
import com.juniori.puzzle.data.firebase.dto.RunQueryResponseDTO
import com.juniori.puzzle.data.firebase.dto.StructuredQuery
import com.juniori.puzzle.data.firebase.dto.VideoDetail
import com.juniori.puzzle.data.firebase.dto.VideoItem
import retrofit2.Response
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val service: FirebaseService
) {
    suspend fun getFirebaseItems(pageSize: Int, pageToken: String, orderBy: String): ListDocumentsResponseDTO {
        return service.listVideoItemDocuments(pageSize, pageToken, orderBy)
    }

    suspend fun postFirebaseItem(videoName: String, fields: VideoDetail): Response<VideoItem> {
        return service.createVideoItemDocument(videoName, mapOf("fields" to fields) )
    }

    suspend fun getFirebaseItemsByQuery(structuredQuery: StructuredQuery): Response<List<RunQueryResponseDTO>>  {
        return service.getFirebaseItemByQuery(RunQueryRequestDTO(structuredQuery))
    }
}
