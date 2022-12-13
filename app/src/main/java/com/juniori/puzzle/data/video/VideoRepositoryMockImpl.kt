package com.juniori.puzzle.data.video

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.repository.VideoRepository
import com.juniori.puzzle.util.SortType
import javax.inject.Inject

class VideoRepositoryMockImpl @Inject constructor(private val videoList: List<VideoInfoEntity>) :
    VideoRepository {
    override suspend fun getMyVideoList(uid: String, index: Int): Resource<List<VideoInfoEntity>> {
        return Resource.Success(
            videoList.filter { videoInfoEntity -> videoInfoEntity.ownerUid == uid }
        )
    }

    override suspend fun getSearchedMyVideoList(
        uid: String,
        index: Int,
        keyword: String
    ): Resource<List<VideoInfoEntity>> {
        return Resource.Success(
            videoList.filter { videoInfoEntity -> videoInfoEntity.ownerUid == uid }
                .filter { videoInfoEntity -> videoInfoEntity.location == keyword }
        )
    }

    override suspend fun getSocialVideoList(
        index: Int,
        sortType: SortType,
        latestData: Long?
    ): Resource<List<VideoInfoEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun getSearchedSocialVideoList(
        index: Int,
        sortType: SortType,
        keyword: String,
        latestData: Long?
    ): Resource<List<VideoInfoEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateLikeStatus(
        documentInfo: VideoInfoEntity,
        uid: String,
        isLiked: Boolean
    ): Resource<VideoInfoEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteVideo(documentId: String): Resource<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun changeVideoScope(documentInfo: VideoInfoEntity): Resource<VideoInfoEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadVideo(
        uid: String,
        videoName: String,
        isPrivate: Boolean,
        location: String,
        memo: String,
        videoByteArray: ByteArray,
        imageByteArray: ByteArray
    ): Resource<VideoInfoEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserInfoByUidUseCase(uid: String): Resource<UserInfoEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun postUserInfoInFirestore(
        uid: String,
        nickname: String,
        profileImage: String
    ): Resource<UserInfoEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun updateServerNickname(userInfoEntity: UserInfoEntity): Resource<UserInfoEntity> {
        TODO("Not yet implemented")
    }
}