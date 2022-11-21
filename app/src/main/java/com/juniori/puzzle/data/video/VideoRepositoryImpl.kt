package com.juniori.puzzle.data.video

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.repository.VideoRepository
import com.juniori.puzzle.util.SortType
import java.io.File
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(val videoRemoteDataSource: VideoRemoteDataSource):
    VideoRepository {
    override suspend fun getMyVideoList(uid: String, index: Int): Resource<List<VideoInfoEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun getSearchedMyVideoList(uid: String, index: Int, keyword: String): Resource<List<VideoInfoEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun getSocialVideoList(index: Int, sortType: SortType): Resource<List<VideoInfoEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun getSearchedSocialVideoList(index: Int, sortType: SortType, keyword: String): Resource<List<VideoInfoEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun getVideoFile(ownerUid: String, videoName: String): Resource<File> {
        TODO("Not yet implemented")
    }

    override suspend fun updateLikeStatus(uid: String, videoName: String): Resource<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteVideo(uid: String, videoName: String): Resource<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun setVideoScope(uid: String, isPrivate: Boolean, videoName: String): Resource<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun postVideoUseCase(videoFile: File, videoInfoEntity: VideoInfoEntity): Resource<Unit> {
        TODO("Not yet implemented")
    }
}