package com.juniori.puzzle.data.firebase

import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

class StorageDataSource @Inject constructor(
    private val service: StorageService
) {
    suspend fun deleteVideo(
        name: String
    ): Result<Int> {
        return try {
            val result = service.delete("video/$name")
            if (result.code() >= 400) {
                Result.failure(Exception(result.message()))
            } else {
                Result.success(result.code())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteThumbnail(
        name: String
    ): Result<Int> {
        return try {
            val result = service.delete("thumb/$name")
            if (result.code() >= 400) {
                Result.failure(Exception(result.message()))
            } else {
                Result.success(result.code())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertVideo(
        name: String,
        fileByteArray: ByteArray
    ): Result<Nothing> {
        return try {
            service.insert(
                "video/$name", body = RequestBody.create(
                    MediaType.parse("video/mp4"),
                    fileByteArray
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertThumbnail(
        name: String,
        fileByteArray: ByteArray
    ): Result<Nothing> {
        return try {
            service.insert(
                "thumb/$name", body = RequestBody.create(
                    MediaType.parse("image/jpeg"),
                    fileByteArray
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
