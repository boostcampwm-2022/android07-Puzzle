package com.juniori.puzzle.network

import com.juniori.puzzle.data.FireStoreResponse
import com.juniori.puzzle.data.VideoDetail
import com.juniori.puzzle.data.VideoItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FirebaseService {
    @GET("databases/(default)/documents/video")
    suspend fun getFirebaseItem(
        @Query("pageSize") pageSize: Int,
        @Query("pageToken") pageToken: String,
        @Query("orderBy") orderBy: String
    ): FireStoreResponse

    @POST("databases/(default)/documents/video")
    suspend fun postFirebaseItemInVideo(
        @Query("documentId") videoName: String,
        @Body fields: Map<String, VideoDetail>
    ): Response<VideoItem>


}
