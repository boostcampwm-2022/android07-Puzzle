package com.juniori.puzzle.network

import com.juniori.puzzle.data.FireStoreResponse
import com.juniori.puzzle.data.VideoDetail
import com.juniori.puzzle.data.VideoItem
import retrofit2.Response
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val service: FirebaseService
) {
    suspend fun getFirebaseItem(pageSize: Int): FireStoreResponse {
        return service.getFirebaseItem(pageSize)
    }

    suspend fun postFirebaseItem(fields: VideoDetail): Response<VideoItem> {
        return service.postFirebaseItemInVideo("test", mapOf("fields" to fields) )
    }
}
