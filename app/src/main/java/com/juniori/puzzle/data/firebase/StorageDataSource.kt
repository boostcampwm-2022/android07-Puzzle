package com.juniori.puzzle.data.firebase

import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

class StorageDataSource @Inject constructor(
    private val service: StorageService
) {
    suspend fun insertVideo(
        name: String,
        fileByteArray: ByteArray
    ): Result<Nothing> {
        return service.insert(
            "video/$name", body = RequestBody.create(
                MediaType.parse("video/mp4"),
                fileByteArray
            )
        )
    }
}
