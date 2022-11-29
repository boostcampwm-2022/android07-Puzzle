package com.juniori.puzzle.data.firebase

import com.juniori.puzzle.data.firebase.dto.RunQueryRequestDTO
import com.juniori.puzzle.data.firebase.dto.RunQueryResponseDTO
import com.juniori.puzzle.data.firebase.dto.UserDetail
import com.juniori.puzzle.data.firebase.dto.UserItem
import com.juniori.puzzle.data.firebase.dto.VideoDetail
import com.juniori.puzzle.data.firebase.dto.VideoItem
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FirestoreService {
    @DELETE("databases/(default)/documents/videoReal/{documentId}")
    suspend fun deleteVideoItemDocument(
        @Path("documentId") documentId: String,
    )

    @PATCH("databases/(default)/documents/videoReal/{documentId}")
    suspend fun patchVideoItemDocument(
        @Path("documentId") documentId: String,
        @Body fields: Map<String, VideoDetail>
    ): VideoItem

    @POST("databases/(default)/documents/videoReal")
    suspend fun createVideoItemDocument(
        @Query("documentId") documentId: String,
        @Body fields: Map<String, VideoDetail>
    ): VideoItem

    @POST("databases/(default)/documents:runQuery")
    suspend fun getFirebaseItemByQuery(
        @Body fields: RunQueryRequestDTO
    ): List<RunQueryResponseDTO>

    @GET("databases/(default)/documents/userReal/{documentId}")
    suspend fun getUserItemDocument(
        @Path("documentId") documentId: String,
    ): UserItem

    @PATCH("databases/(default)/documents/userReal/{documentId}")
    suspend fun patchUserItemDocument(
        @Path("documentId") documentId: String,
        @Body fields: Map<String, UserDetail>
    ): UserItem

    @POST("databases/(default)/documents/userReal")
    suspend fun createUserItemDocument(
        @Query("documentId") documentId: String,
        @Body fields: Map<String, UserDetail>
    ): UserItem
}
