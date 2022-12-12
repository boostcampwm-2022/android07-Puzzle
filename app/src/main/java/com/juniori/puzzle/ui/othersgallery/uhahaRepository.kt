package com.juniori.puzzle.ui.othersgallery

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.firebase.FirestoreDataSource
import com.juniori.puzzle.data.firebase.StorageDataSource
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.util.PagingConst
import com.juniori.puzzle.util.SortType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

enum class VideoFetchingState {
    NONE,
    Loading,
    NO_MORE_VIDEO,
    NETWORK_ERROR_PAGING,
    NETWORK_ERROR_BASE,
}

@Singleton
class Repositoryk @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val storageDataSource: StorageDataSource
) {
    private val _othersVideoList = MutableStateFlow<List<VideoInfoEntity>>(emptyList())
    val othersVideoList = _othersVideoList.asStateFlow()

    private val _othersVideoFetchingState = MutableStateFlow(VideoFetchingState.NONE)
    val othersVideoFetchingState = _othersVideoFetchingState.asStateFlow()

    private val _myVideoList = MutableStateFlow<List<VideoInfoEntity>>(emptyList())
    val myVideoList = _myVideoList.asStateFlow()

    private val _myVideoFetchingState = MutableStateFlow(VideoFetchingState.NONE)
    val myVideoFetchingState = _myVideoFetchingState.asStateFlow()

    private var lastLikeCount = Long.MAX_VALUE
    private var lastTime = Long.MAX_VALUE
    var lastOffset = 0

    private var pagingEndFlag = false

    suspend fun fetchMainData(query: String, sortType: SortType) {
        if (_othersVideoFetchingState.value == VideoFetchingState.Loading) {
            return
        }
        _othersVideoList.value = emptyList()
        pagingEndFlag = false
        setLastData(Long.MAX_VALUE, Long.MAX_VALUE, 0, sortType)

        _othersVideoFetchingState.value = VideoFetchingState.Loading
        val data = if (query.isBlank()) {
            getBaseData(isFirstPage = true, sortType = sortType)
        } else {
            getQueryData(isFirstPage = true, query = query, sortType = sortType)
        }

        if (data is Resource.Success) {
            val result = data.result
            if (result.isEmpty().not()) {
                result.last().also {
                    setLastData(
                        time = it.updateTime,
                        like = it.likedCount.toLong(),
                        offset = result.countWith(it, sortType),
                        sortType = sortType
                    )
                }
                _othersVideoList.value = result
            }
        } else {
            _othersVideoFetchingState.value = VideoFetchingState.NETWORK_ERROR_BASE
            return
        }

        _othersVideoFetchingState.value = VideoFetchingState.NONE
    }

    suspend fun fetchMyData(uid: String?, query: String) {
        if (_myVideoFetchingState.value == VideoFetchingState.Loading) {
            return
        }

        _myVideoList.value = emptyList()
        pagingEndFlag = false

        if (uid == null) {
            _myVideoFetchingState.value = VideoFetchingState.NETWORK_ERROR_BASE
        } else {
            _myVideoFetchingState.value = VideoFetchingState.Loading
            val data = if (query.isBlank()) {
                firestoreDataSource.getMyVideoItems(
                    uid = uid,
                    offset = 0,
                    limit = 12
                )
            } else {
                firestoreDataSource.getMyVideoItemsWithKeyword(
                    uid = uid,
                    toSearch = "location_keyword",
                    keyword = query,
                    offset = 0,
                    limit = 12
                )
            }

            if (data is Resource.Success) {
                val result = data.result
                if (result.isEmpty().not()) {
                    if (result.size < PagingConst.ITEM_CNT) {
                        pagingEndFlag = true
                    }
                    _myVideoList.value = result
                }
            } else {
                _myVideoFetchingState.value = VideoFetchingState.NETWORK_ERROR_BASE
                return
            }

            _myVideoFetchingState.value = VideoFetchingState.NONE
        }
    }

    private suspend fun getBaseData(
        isFirstPage: Boolean,
        sortType: SortType
    ): Resource<List<VideoInfoEntity>> =
        firestoreDataSource.getPublicVideoItemsOrderBy(
            limit = 12,
            offset = if (isFirstPage) 0 else lastOffset,
            orderBy = sortType,
            latestData = if (isFirstPage) {
                null
            } else when (sortType) {
                SortType.LIKE -> lastLikeCount
                SortType.NEW -> lastTime
            }
        )

    private suspend fun getQueryData(
        isFirstPage: Boolean,
        query: String,
        sortType: SortType
    ): Resource<List<VideoInfoEntity>> =
        firestoreDataSource.getPublicVideoItemsWithKeywordOrderBy(
            limit = 12,
            offset = if (isFirstPage) 0 else othersVideoList.value.size,
            orderBy = sortType,
            toSearch = "location_keyword",
            keyword = query,
            latestData = if (isFirstPage) {
                null
            } else when (sortType) {
                SortType.LIKE -> lastLikeCount
                SortType.NEW -> lastTime
            }
        )

    suspend fun fetchNextOthersVideos(query: String, sortType: SortType) {
        if (_othersVideoFetchingState.value == VideoFetchingState.Loading || pagingEndFlag) {
            return
        }
        _othersVideoFetchingState.value = VideoFetchingState.Loading
        val data = if (query.isBlank()) {
            getBaseData(isFirstPage = false, sortType = sortType)
        } else {
            getQueryData(isFirstPage = false, query = query, sortType = sortType)
        }

        if (data is Resource.Success) {
            val result = data.result
            if (result.isEmpty()) {
                withContext(Dispatchers.IO) {
                    _othersVideoFetchingState.value = VideoFetchingState.NO_MORE_VIDEO
                    delay(1000)
                }
                pagingEndFlag = true
            } else {
                result.last().also {
                    setLastData(
                        time = it.updateTime,
                        like = it.likedCount.toLong(),
                        offset = result.countWith(it, sortType),
                        sortType = sortType
                    )
                }
                _othersVideoList.value = _othersVideoList.value + result
            }
        } else {
            _othersVideoFetchingState.value = VideoFetchingState.NETWORK_ERROR_PAGING
            return
        }

        _othersVideoFetchingState.value = VideoFetchingState.NONE
    }

    suspend fun fetchMyNextVideos(uid: String?, start: Int, query: String) {
        if (_myVideoFetchingState.value == VideoFetchingState.Loading || pagingEndFlag) {
            return
        }

        if (uid == null) {
            _myVideoFetchingState.value = VideoFetchingState.NETWORK_ERROR_BASE
        } else {
            _myVideoFetchingState.value = VideoFetchingState.Loading
            val data = if (query.isBlank()) {
                firestoreDataSource.getMyVideoItems(
                    uid = uid,
                    offset = start,
                    limit = 12
                )
            } else {
                firestoreDataSource.getMyVideoItemsWithKeyword(
                    uid = uid,
                    toSearch = "location_keyword",
                    keyword = query,
                    offset = start,
                    limit = 12
                )
            }

            if (data is Resource.Success) {
                val result = data.result
                if (result.isEmpty()) {
                    withContext(Dispatchers.IO) {
                        _myVideoFetchingState.value = VideoFetchingState.NO_MORE_VIDEO
                        delay(1000)
                    }
                    pagingEndFlag = true
                } else {
                    _myVideoList.value = _myVideoList.value + result
                }
            } else {
                _myVideoFetchingState.value = VideoFetchingState.NETWORK_ERROR_PAGING
                return
            }

            _myVideoFetchingState.value = VideoFetchingState.NONE
        }
    }

    private fun setLastData(time: Long, like: Long, offset: Int, sortType: SortType) {
        if (sortType == SortType.NEW && lastTime == time) {
            lastOffset += offset
        } else if (sortType == SortType.LIKE && lastLikeCount == like) {
            lastOffset += offset
        } else {
            lastOffset = offset
        }
        lastTime = time
        lastLikeCount = like
    }

    private fun List<VideoInfoEntity>.countWith(base: VideoInfoEntity, sortType: SortType): Int {
        return if (sortType == SortType.NEW) {
            this.count { another ->
                another.updateTime == base.updateTime
            }
        } else {
            this.count { another ->
                another.likedCount == base.likedCount
            }
        }
    }

    suspend fun changeVideoScope(
        documentInfo: VideoInfoEntity
    ): Resource<VideoInfoEntity> {
        val changeResult = firestoreDataSource.changeVideoItemPrivacy(documentInfo)
        if (changeResult is Resource.Success) {
            val updatedVideoInfo = changeResult.result
            updateVideoList(_othersVideoList, updatedVideoInfo)
        }
        return changeResult
    }

    private fun updateVideoList(
        listFlow: MutableStateFlow<List<VideoInfoEntity>>,
        updatedVideoInfo: VideoInfoEntity
    ) {
        listFlow.value = listFlow.value.map { videoInfo ->
            if (videoInfo.documentId == updatedVideoInfo.documentId) {
                updatedVideoInfo
            } else {
                videoInfo
            }
        }
    }

    suspend fun deleteVideo(documentId: String): Resource<Unit> {
        return if (
            storageDataSource.deleteVideo(documentId).isSuccess &&
            storageDataSource.deleteThumbnail(documentId).isSuccess
        ) {
            firestoreDataSource.deleteVideoItem(documentId).also { deletionResource ->
                if (deletionResource is Resource.Success) {
                    _othersVideoList.value = _othersVideoList.value.filterNot { videoInfo ->
                        videoInfo.documentId == documentId
                    }
                }
            }
        } else {
            Resource.Failure(Exception("delete video and thumbnail in Storage failed"))
        }
    }
}
