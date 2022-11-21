package com.juniori.puzzle.data.firebase

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.firebase.dto.ArrayValue
import com.juniori.puzzle.data.firebase.dto.BooleanValue
import com.juniori.puzzle.data.firebase.dto.IntegerValue
import com.juniori.puzzle.data.firebase.dto.ListDocumentsResponseDTO
import com.juniori.puzzle.data.firebase.dto.RunQueryRequestDTO
import com.juniori.puzzle.data.firebase.dto.RunQueryResponseDTO
import com.juniori.puzzle.data.firebase.dto.StringValue
import com.juniori.puzzle.data.firebase.dto.StringValues
import com.juniori.puzzle.data.firebase.dto.VideoDetail
import com.juniori.puzzle.data.firebase.dto.VideoItem
import com.juniori.puzzle.util.QueryUtil
import com.juniori.puzzle.util.STORAGE_BASE_URL
import javax.inject.Inject

class FirestoreDataSource @Inject constructor(
    private val service: FirestoreService
) {
    suspend fun getVideoItems(
        pageSize: Int,
        pageToken: String,
        orderBy: String
    ): Resource<ListDocumentsResponseDTO> {
        return try {
            Resource.Success(service.listVideoItemDocuments(pageSize, pageToken, orderBy))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun postVideoItem(
        uid: String,
        videoName: String,
        isPrivate: Boolean,
        location: String,
        memo: String
    ): Resource<VideoItem> {
        return try {
            service.createVideoItemDocument(
                mapOf(
                    "fields" to VideoDetail(
                        ownerUid = StringValue(uid),
                        videoUrl = StringValue(STORAGE_BASE_URL + "o/video%2F" + videoName + "?alt=media"),
                        thumbUrl = StringValue(STORAGE_BASE_URL + "o/thumb%2F" + videoName + "?alt=media"),
                        isPrivate = BooleanValue(isPrivate),
                        likeCount = IntegerValue(0),
                        likedUserList = ArrayValue(StringValues(listOf())),
                        updateTime = IntegerValue(System.currentTimeMillis()),
                        location = StringValue(location),
                        memo = StringValue(memo)
                    )
                )
            ).let {
                Resource.Success(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun getMyVideoItems(
        uid: String,
        offset: Int? = null,
        limit: Int? = null
    ): Resource<List<RunQueryResponseDTO>> {
        return try {
            Resource.Success(
                service.getFirebaseItemByQuery(
                    RunQueryRequestDTO(
                        QueryUtil.getMyVideoQuery(uid, offset, limit)
                    )
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun getPublicVideoItemsOrderByLikeDescending(
        offset: Int? = null,
        limit: Int? = null
    ): Resource<List<RunQueryResponseDTO>> {
        return try {
            Resource.Success(
                service.getFirebaseItemByQuery(
                    RunQueryRequestDTO(
                        QueryUtil.getPublicVideoQuery("like_count", offset, limit)
                    )
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun getPublicVideoItemsOrderByUpdateTimeDescending(
        offset: Int?,
        limit: Int?
    ): Resource<List<RunQueryResponseDTO>> {
        return try {
            Resource.Success(
                service.getFirebaseItemByQuery(
                    RunQueryRequestDTO(
                        QueryUtil.getPublicVideoQuery("update_time", offset, limit)
                    )
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }
}
