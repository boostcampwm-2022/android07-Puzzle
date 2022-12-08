package com.juniori.puzzle.data.video

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.firebase.FirestoreDataSource
import com.juniori.puzzle.data.firebase.StorageDataSource
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.repository.VideoRepository
import com.juniori.puzzle.util.PagingConst.ITEM_CNT
import com.juniori.puzzle.util.SortType
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val storageDataSource: StorageDataSource
) : VideoRepository {
    /** 내 비디오 목록 가져오기
     * @param uid: 사용자 uid
     * @param index: 가져오기 시작할 바디오 index*/
    override suspend fun getMyVideoList(uid: String, index: Int): Resource<List<VideoInfoEntity>> {
        return firestoreDataSource.getMyVideoItems(
            uid = uid,
            offset = index,
            limit = ITEM_CNT
        )
    }

    /** 검색을 통해 내 비디오 목록 가져오기
     * @param uid: 사용자 uid
     * @param index: 가져오기 시작할 바디오 index
     * @param keyword: 검색할 단어 */
    override suspend fun getSearchedMyVideoList(
        uid: String,
        index: Int,
        keyword: String
    ): Resource<List<VideoInfoEntity>> {
        return firestoreDataSource.getMyVideoItemsWithKeyword(
            uid = uid,
            toSearch = "location_keyword",
            keyword = keyword,
            offset = index,
            limit = ITEM_CNT
        )
    }

    /** 공개 상태인 비디오 목록 가져오기
     * @param index: 가져오기 시작할 바디오 index
     * @param sortType: 정렬 타입 */
    override suspend fun getSocialVideoList(
        index: Int,
        sortType: SortType,
        latestData: Long?
    ): Resource<List<VideoInfoEntity>> {
        return firestoreDataSource.getPublicVideoItemsOrderBy(
            orderBy = sortType,
            latestData = latestData,
            offset = index,
            limit = ITEM_CNT
        )
    }

    /** 검색을 통해 공개 상태인 비디오 목록 가져오기
     * @param index: 가져오기 시작할 바디오 index
     * @param sortType: 정렬 타입
     * @param keyword: 검색할 단어*/
    override suspend fun getSearchedSocialVideoList(
        index: Int,
        sortType: SortType,
        keyword: String,
        latestData: Long?
    ): Resource<List<VideoInfoEntity>> {
        return firestoreDataSource.getPublicVideoItemsWithKeywordOrderBy(
            orderBy = sortType,
            toSearch = "location_keyword",
            keyword = keyword,
            latestData = latestData,
            offset = index,
            limit = ITEM_CNT,
        )
    }

    override suspend fun updateServerNickname(userInfoEntity: UserInfoEntity): Resource<UserInfoEntity> {
        return firestoreDataSource.changeUserNickname(
            userInfoEntity.uid,
            userInfoEntity.nickname,
            userInfoEntity.profileImage
        )
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

    override suspend fun postUserInfoInFirestore(
        uid: String,
        nickname: String,
        profileImage: String
    ): Resource<UserInfoEntity> {
        return firestoreDataSource.postUserItem(uid, nickname, profileImage)
    }
}