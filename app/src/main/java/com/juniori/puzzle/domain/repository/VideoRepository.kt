package com.juniori.puzzle.domain.repository

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.util.SortType
import java.io.File

interface VideoRepository {
    suspend fun getMyVideoList(uid: String, index: Int): Resource<List<VideoInfoEntity>>
    suspend fun getSearchedMyVideoList(uid: String, index: Int, keyword: String): Resource<List<VideoInfoEntity>>
    suspend fun getSocialVideoList(index: Int, sortType: SortType): Resource<List<VideoInfoEntity>>
    suspend fun getSearchedSocialVideoList(index: Int, sortType: SortType, keyword: String): Resource<List<VideoInfoEntity>>
    suspend fun getVideoFile(ownerUid: String, videoName: String): Resource<File>
    suspend fun updateLikeStatus(uid: String, videoName: String): Resource<Unit>
    suspend fun deleteVideo(uid: String, videoName: String): Resource<Unit>
    suspend fun setVideoScope(uid: String, isPrivate: Boolean, videoName: String): Resource<Unit>
    suspend fun postVideoUseCase(videoFile: File, videoInfoEntity: VideoInfoEntity): Resource<Unit>
    suspend fun updateServerNickname(userInfoEntity: UserInfoEntity): Resource<UserInfoEntity>
}