package com.juniori.puzzle.domain.repository

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.util.GalleryType
import com.juniori.puzzle.util.SortType
import com.juniori.puzzle.util.VideoFetchingState
import kotlinx.coroutines.flow.StateFlow

interface VideoRepository {

    val myVideoList: StateFlow<List<VideoInfoEntity>>
    val myVideoFetchingState: StateFlow<VideoFetchingState>

    val othersVideoList: StateFlow<List<VideoInfoEntity>>
    val othersVideoFetchingState: StateFlow<VideoFetchingState>

    suspend fun fetchMyFirstPageVideos(uid: String?, query: String)
    suspend fun fetchMyNextVideos(uid: String?, start: Int, query: String)

    suspend fun fetchOthersFirstPageVideos(query: String, sortType: SortType)
    suspend fun fetchOthersNextVideos(query: String, sortType: SortType)

    suspend fun changeVideoScope(
        documentInfo: VideoInfoEntity,
        galleryType: GalleryType
    ): Resource<VideoInfoEntity>
    suspend fun deleteVideo(documentId: String, galleryType: GalleryType): Resource<Unit>

    suspend fun updateLikeStatus(
        documentInfo: VideoInfoEntity,
        uid: String,
        isLiked: Boolean,
        galleryType: GalleryType
    ): Resource<VideoInfoEntity>

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
