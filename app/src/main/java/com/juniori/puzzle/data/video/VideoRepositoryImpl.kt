package com.juniori.puzzle.data.video

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.firebase.FirestoreDataSource
import com.juniori.puzzle.data.firebase.StorageDataSource
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.repository.VideoRepository
import com.juniori.puzzle.util.SortType
import java.io.File
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val storageDataSource: StorageDataSource
) : VideoRepository {
    override suspend fun getMyVideoList(uid: String, index: Int): Resource<List<VideoInfoEntity>> {
        return firestoreDataSource.getMyVideoItems(
            uid = uid,
            offset = index,
            limit = 10
        )
    }

    override suspend fun getSearchedMyVideoList(
        uid: String,
        index: Int,
        keyword: String
    ): Resource<List<VideoInfoEntity>> {
        return firestoreDataSource.getMyVideoItemsWithKeyword(
            uid = uid,
            toSearch = "location",
            keyword = keyword,
            offset = index,
            limit = 10
        )
    }

    override suspend fun getSocialVideoList(
        index: Int,
        sortType: SortType
    ): Resource<List<VideoInfoEntity>> {
        return firestoreDataSource.getPublicVideoItemsOrderBy(
            orderBy = sortType,
            offset = index,
            limit = 10
        )
    }

    override suspend fun getSearchedSocialVideoList(
        index: Int,
        sortType: SortType,
        keyword: String
    ): Resource<List<VideoInfoEntity>> {
        return firestoreDataSource.getPublicVideoItemsWithKeywordOrderBy(
            orderBy = sortType,
            toSearch = "location",
            keyword = keyword,
            offset = index,
            limit = 10
        )
    }

    override suspend fun getVideoFile(ownerUid: String, videoName: String): Resource<File> {
        TODO("Not yet implemented")
    }

    override suspend fun updateLikeStatus(
        documentInfo: VideoInfoEntity,
        uid: String,
        isLiked: Boolean
    ): Resource<VideoInfoEntity> {
        return if (isLiked) {
            firestoreDataSource.removeVideoItemLike(documentInfo, uid)
        } else {
            firestoreDataSource.addVideoItemLike(documentInfo, uid)
        }
    }

    override suspend fun deleteVideo(documentId: String): Resource<Unit> {
        return if (storageDataSource.deleteVideo(documentId).isSuccess && storageDataSource.deleteThumbnail(
                documentId
            ).isSuccess
        ) {
            firestoreDataSource.deleteVideoItem(documentId)
        } else {
            Resource.Failure(Exception("delete video and thumbnail in Storage failed"))
        }
    }

    override suspend fun changeVideoScope(
        documentInfo: VideoInfoEntity
    ): Resource<VideoInfoEntity> {
        return firestoreDataSource.changeVideoItemPrivacy(documentInfo)
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
        return if (storageDataSource.insertVideo(
                videoName,
                videoByteArray
            ).isSuccess && storageDataSource.insertThumbnail(
                videoName,
                imageByteArray
            ).isSuccess
        ) {
            firestoreDataSource.postVideoItem(uid, videoName, isPrivate, location, memo)
        } else {
            Resource.Failure(Exception("upload video and thumbnail in Storage failed"))
        }
    }

    override suspend fun getUserInfoByUidUseCase(uid: String): Resource<UserInfoEntity> {
        return firestoreDataSource.getUserItem(uid)
    }
}