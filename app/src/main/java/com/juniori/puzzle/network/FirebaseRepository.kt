package com.juniori.puzzle.network

import com.juniori.puzzle.data.FireStoreResponse
import com.juniori.puzzle.data.VideoDetail
import com.juniori.puzzle.data.VideoItem
import retrofit2.Response
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val service: FirebaseService
) {
    suspend fun getFirebaseItem(pageSize: Int, pageToken: String, orderBy: String): FireStoreResponse {
        return service.getFirebaseItem(pageSize, pageToken, orderBy)
    }

    suspend fun postFirebaseItem(videoName: String, fields: VideoDetail): Response<VideoItem> {
        return service.postFirebaseItemInVideo(videoName, mapOf("fields" to fields) )
    }
}
