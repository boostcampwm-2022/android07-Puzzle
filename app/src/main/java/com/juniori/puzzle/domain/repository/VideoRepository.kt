package com.juniori.puzzle.domain.repository

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.util.SortType

interface VideoRepository {
    suspend fun getMyVideoList(uid: String, index: Int): Resource<List<VideoInfoEntity>>
    suspend fun getSearchedMyVideoList(uid: String, index: Int, keyword: String): Resource<List<VideoInfoEntity>>

    suspend fun getSocialVideoList(
        index: Int,
        sortType: SortType,
        latestData: Long?
    ): Resource<List<VideoInfoEntity>>

    suspend fun getSearchedSocialVideoList(
        index: Int,
        sortType: SortType,
        keyword: String,
        latestData: Long?
    ): Resource<List<VideoInfoEntity>>

    suspend fun updateLikeStatus(
        documentInfo: VideoInfoEntity,
        uid: String,
        isLiked: Boolean
    ): Resource<VideoInfoEntity>

    suspend fun deleteVideo(documentId: String): Resource<Unit>
    suspend fun changeVideoScope(documentInfo: VideoInfoEntity): Resource<VideoInfoEntity>
    suspend fun uploadVideo(
        uid: String,
        videoName: String,
        isPrivate: Boolean,
        location: String,
        memo: String,
        videoByteArray: ByteArray,
        imageByteArray: ByteArray
    ): Resource<VideoInfoEntity>

    suspend fun getUserInfoByUidUseCase(uid: String): Resource<UserInfoEntity>
    suspend fun postUserInfoInFirestore(
        uid: String,
        nickname: String,
        profileImage: String
    ): Resource<UserInfoEntity>

    suspend fun updateServerNickname(userInfoEntity: UserInfoEntity): Resource<UserInfoEntity>
}