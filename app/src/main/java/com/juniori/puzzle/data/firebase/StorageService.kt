package com.juniori.puzzle.data.firebase

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface StorageService {
    @POST("o")
    suspend fun insert(
        @Query("name") name: String,
        @Query("uploadType") uploadType: String = "media",
        @Body body: RequestBody
    ): Result<Nothing>
}
