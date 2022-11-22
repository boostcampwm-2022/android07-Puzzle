package com.juniori.puzzle.data.firebase

import com.juniori.puzzle.data.firebase.dto.RunQueryRequestDTO
import com.juniori.puzzle.data.firebase.dto.RunQueryResponseDTO
import com.juniori.puzzle.data.firebase.dto.VideoDetail
import com.juniori.puzzle.data.firebase.dto.VideoItem
import retrofit2.http.Body
import retrofit2.http.POST

interface FirestoreService {
    @POST("databases/(default)/documents/videoReal")
    suspend fun createVideoItemDocument(
        @Body fields: Map<String, VideoDetail>
    ): VideoItem

    @POST("databases/(default)/documents:runQuery")
    suspend fun getFirebaseItemByQuery(
        @Body fields: RunQueryRequestDTO
    ): List<RunQueryResponseDTO>
}
