package com.juniori.puzzle.data.firebase

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StorageService {
    @DELETE("o/{name}")
    suspend fun delete(
        @Path("name") name: String,
    ): Response<Unit>

    @POST("o")
    suspend fun insert(
        @Query("name") name: String,
        @Query("uploadType") uploadType: String = "media",
        @Body body: RequestBody
    ): Result<Nothing>
}
