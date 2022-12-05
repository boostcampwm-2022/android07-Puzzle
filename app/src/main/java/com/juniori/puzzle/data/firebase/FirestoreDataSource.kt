package com.juniori.puzzle.data.firebase

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.firebase.dto.ArrayValue
import com.juniori.puzzle.data.firebase.dto.BooleanValue
import com.juniori.puzzle.data.firebase.dto.IntegerValue
import com.juniori.puzzle.data.firebase.dto.RunQueryRequestDTO
import com.juniori.puzzle.data.firebase.dto.StringValue
import com.juniori.puzzle.data.firebase.dto.StringValues
import com.juniori.puzzle.data.firebase.dto.UserDetail
import com.juniori.puzzle.data.firebase.dto.VideoDetail
import com.juniori.puzzle.data.firebase.dto.getVideoInfoEntity
import com.juniori.puzzle.data.firebase.dto.toStringValues
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.util.QueryUtil
import com.juniori.puzzle.util.STORAGE_BASE_URL
import com.juniori.puzzle.util.SortType
import com.juniori.puzzle.util.toLocationKeyword
import javax.inject.Inject

class FirestoreDataSource @Inject constructor(
    private val service: FirestoreService
) {
    suspend fun deleteVideoItem(documentId: String): Resource<Unit> {
        return try {
            Resource.Success(service.deleteVideoItemDocument(documentId))
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    suspend fun changeVideoItemPrivacy(
        documentInfo: VideoInfoEntity
    ): Resource<VideoInfoEntity> {
        return try {
            service.patchVideoItemDocument(
                documentInfo.documentId,
                mapOf(
                    with(documentInfo) {
                        "fields" to VideoDetail(
                            ownerUid = StringValue(ownerUid),
                            videoUrl = StringValue(videoUrl),
                            thumbUrl = StringValue(thumbnailUrl),
                            isPrivate = BooleanValue(isPrivate.not()),
                            likeCount = IntegerValue(likedCount.toLong()),
                            likedUserList = ArrayValue(likedUserUidList.toStringValues()),
                            updateTime = IntegerValue(updateTime),
                            location = StringValue(location),
                            locationKeyword = ArrayValue(locationKeyword.toStringValues()),
                            memo = StringValue(memo)
                        )
                    }
                )
            ).let {
                Resource.Success(it.getVideoInfoEntity())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun addVideoItemLike(
        documentInfo: VideoInfoEntity,
        uid: String
    ): Resource<VideoInfoEntity> {
        return try {
            service.patchVideoItemDocument(
                documentInfo.documentId,
                mapOf(
                    with(documentInfo) {
                        "fields" to VideoDetail(
                            ownerUid = StringValue(ownerUid),
                            videoUrl = StringValue(videoUrl),
                            thumbUrl = StringValue(thumbnailUrl),
                            isPrivate = BooleanValue(isPrivate),
                            likeCount = IntegerValue(likedCount.toLong() + 1),
                            likedUserList = ArrayValue((likedUserUidList + uid).toStringValues()),
                            updateTime = IntegerValue(updateTime),
                            location = StringValue(location),
                            locationKeyword = ArrayValue(locationKeyword.toStringValues()),
                            memo = StringValue(memo)
                        )
                    }
                )
            ).let {
                Resource.Success(it.getVideoInfoEntity())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun removeVideoItemLike(
        documentInfo: VideoInfoEntity,
        uid: String
    ): Resource<VideoInfoEntity> {
        return try {
            service.patchVideoItemDocument(
                documentInfo.documentId,
                mapOf(
                    with(documentInfo) {
                        "fields" to VideoDetail(
                            ownerUid = StringValue(ownerUid),
                            videoUrl = StringValue(videoUrl),
                            thumbUrl = StringValue(thumbnailUrl),
                            isPrivate = BooleanValue(isPrivate),
                            likeCount = IntegerValue(likedCount.toLong() - 1),
                            likedUserList = ArrayValue((likedUserUidList - uid).toStringValues()),
                            updateTime = IntegerValue(updateTime),
                            location = StringValue(location),
                            locationKeyword = ArrayValue(locationKeyword.toStringValues()),
                            memo = StringValue(memo)
                        )
                    }
                )
            ).let {
                Resource.Success(it.getVideoInfoEntity())
            }
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
                        locationKeyword = ArrayValue(location.toLocationKeyword().toStringValues()),
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
                ).getVideoInfoEntity()
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
                ).getVideoInfoEntity()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun getPublicVideoItemsOrderBy(
        orderBy: SortType,
        latestData: Long?,
        offset: Int? = null,
        limit: Int? = null,
    ): Resource<List<VideoInfoEntity>> {
        return try {
            Resource.Success(
                service.getFirebaseItemByQuery(
                    RunQueryRequestDTO(
                        QueryUtil.getPublicVideoQuery(
                            orderBy = orderBy,
                            latestData = latestData,
                            offset = offset,
                            limit = limit
                        )
                    )
                ).getVideoInfoEntity()
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
        latestData: Long?,
        offset: Int? = null,
        limit: Int? = null
    ): Resource<List<VideoInfoEntity>> {
        return try {
            Resource.Success(
                service.getFirebaseItemByQuery(
                    RunQueryRequestDTO(
                        QueryUtil.getPublicVideoWithKeywordQuery(
                            orderBy,
                            toSearch,
                            keyword,
                            latestData,
                            offset,
                            limit,
                        )
                    )
                ).getVideoInfoEntity()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun getUserItem(
        uid: String
    ): Resource<UserInfoEntity> {
        return try {
            service.getUserItemDocument(uid).let {
                Resource.Success(it.getUserInfoEntity())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun postUserItem(
        uid: String,
        nickname: String,
        profileImage: String
    ): Resource<UserInfoEntity> {
        return try {
            service.createUserItemDocument(
                uid,
                mapOf(
                    "fields" to UserDetail(
                        nickname = StringValue(nickname),
                        profileImage = StringValue(profileImage)
                    )
                )
            ).let {
                Resource.Success(it.getUserInfoEntity())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun changeUserNickname(
        uid: String,
        newNickname: String,
        profileImage: String
    ): Resource<UserInfoEntity> {
        return try {
            service.patchUserItemDocument(
                uid,
                mapOf(
                    "fields" to UserDetail(
                        nickname = StringValue(newNickname),
                        profileImage = StringValue(profileImage)
                    )
                )
            ).let {
                Resource.Success(it.getUserInfoEntity())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }
}
