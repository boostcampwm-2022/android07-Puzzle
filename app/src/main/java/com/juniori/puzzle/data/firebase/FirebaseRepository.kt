package com.juniori.puzzle.data.firebase

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.firebase.dto.ArrayValue
import com.juniori.puzzle.data.firebase.dto.BooleanValue
import com.juniori.puzzle.data.firebase.dto.FieldFilter
import com.juniori.puzzle.data.firebase.dto.FieldReference
import com.juniori.puzzle.data.firebase.dto.Filter
import com.juniori.puzzle.data.firebase.dto.IntegerValue
import com.juniori.puzzle.data.firebase.dto.ListDocumentsResponseDTO
import com.juniori.puzzle.data.firebase.dto.Order
import com.juniori.puzzle.data.firebase.dto.RunQueryRequestDTO
import com.juniori.puzzle.data.firebase.dto.RunQueryResponseDTO
import com.juniori.puzzle.data.firebase.dto.StringValue
import com.juniori.puzzle.data.firebase.dto.StringValues
import com.juniori.puzzle.data.firebase.dto.StructuredQuery
import com.juniori.puzzle.data.firebase.dto.VideoDetail
import com.juniori.puzzle.data.firebase.dto.VideoItem
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val service: FirebaseService
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
        videoUrl: String,
        thumbUrl: String,
        isPrivate: Boolean,
        location: String,
        memo: String
    ): Resource<VideoItem> {
        return try {
            service.createVideoItemDocument(
                mapOf(
                    "fields" to VideoDetail(
                        ownerUid = StringValue(uid),
                        videoUrl = StringValue(videoUrl),
                        thumbUrl = StringValue(thumbUrl),
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

    suspend fun getPublicVideoItemsOrderByLikeDescending(): Resource<List<RunQueryResponseDTO>> {
        return try {
            Resource.Success(
                service.getFirebaseItemByQuery(
                    RunQueryRequestDTO(
                        StructuredQuery(
                            where = Filter(
                                fieldFilter = FieldFilter(
                                    field = FieldReference("is_private"),
                                    op = "EQUAL",
                                    value = BooleanValue(false)
                                )
                            ),
                            orderBy = listOf(
                                Order(
                                    field = FieldReference("like_count"),
                                    direction = "DESCENDING"
                                )
                            )
                        )
                    )
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun getPublicVideoItemsOrderByUpdateTimeDescending(): Resource<List<RunQueryResponseDTO>> {
        return try {
            Resource.Success(
                service.getFirebaseItemByQuery(
                    RunQueryRequestDTO(
                        StructuredQuery(
                            where = Filter(
                                fieldFilter = FieldFilter(
                                    field = FieldReference("is_private"),
                                    op = "EQUAL",
                                    value = BooleanValue(false)
                                )
                            ),
                            orderBy = listOf(
                                Order(
                                    field = FieldReference("update_time"),
                                    direction = "DESCENDING"
                                )
                            )
                        )
                    )
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }
}
