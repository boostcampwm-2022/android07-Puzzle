package com.juniori.puzzle.data.firebase

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.firebase.dto.ArrayValue
import com.juniori.puzzle.data.firebase.dto.BooleanValue
import com.juniori.puzzle.data.firebase.dto.IntegerValue
import com.juniori.puzzle.data.firebase.dto.RunQueryRequestDTO
import com.juniori.puzzle.data.firebase.dto.StringValue
import com.juniori.puzzle.data.firebase.dto.StringValues
import com.juniori.puzzle.data.firebase.dto.VideoDetail
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.util.QueryUtil
import com.juniori.puzzle.util.STORAGE_BASE_URL
import com.juniori.puzzle.util.SortType
import javax.inject.Inject

class FirestoreDataSource @Inject constructor(
    private val service: FirestoreService
) {
    suspend fun postVideoItem(
        uid: String,
        videoName: String,
        isPrivate: Boolean,
        location: String,
        memo: String
    ): Resource<VideoInfoEntity> {
        return try {
            service.createVideoItemDocument(
                videoName,
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
                Resource.Success(it.getVideoInfoEntity())
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
    ): Resource<List<VideoInfoEntity>> {
        return try {
            Resource.Success(
                service.getFirebaseItemByQuery(
                    RunQueryRequestDTO(
                        QueryUtil.getMyVideoQuery(uid, offset, limit)
                    )
                ).filter {
                    it.videoItem != null
                }.map { it.videoItem!!.getVideoInfoEntity() }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun getMyVideoItemsWithKeyword(
        uid: String,
        toSearch: String,
        keyword: String,
        offset: Int?,
        limit: Int?
    ): Resource<List<VideoInfoEntity>> {
        return try {
            Resource.Success(
                service.getFirebaseItemByQuery(
                    RunQueryRequestDTO(
                        QueryUtil.getMyVideoWithKeywordQuery(uid, toSearch, keyword, offset, limit)
                    )
                ).filter {
                    it.videoItem != null
                }.map { it.videoItem!!.getVideoInfoEntity() }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun getPublicVideoItemsOrderBy(
        orderBy: SortType,
        offset: Int? = null,
        limit: Int? = null
    ): Resource<List<VideoInfoEntity>> {
        return try {
            Resource.Success(
                service.getFirebaseItemByQuery(
                    RunQueryRequestDTO(
                        QueryUtil.getPublicVideoQuery(orderBy.value, offset, limit)
                    )
                ).filter {
                    it.videoItem != null
                }.map { it.videoItem!!.getVideoInfoEntity() }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun getPublicVideoItemsWithKeywordOrderBy(
        orderBy: SortType,
        toSearch: String,
        keyword: String,
        offset: Int? = null,
        limit: Int? = null
    ): Resource<List<VideoInfoEntity>> {
        return try {
            Resource.Success(
                service.getFirebaseItemByQuery(
                    RunQueryRequestDTO(
                        QueryUtil.getPublicVideoWithKeywordQuery(
                            orderBy.value,
                            toSearch,
                            keyword,
                            offset,
                            limit
                        )
                    )
                ).filter {
                    it.videoItem != null
                }.map { it.videoItem!!.getVideoInfoEntity() }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }
}
