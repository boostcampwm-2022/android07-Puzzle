package com.juniori.puzzle.ui.playvideo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.usecase.ChangeVideoScopeUseCase
import com.juniori.puzzle.domain.usecase.DeleteVideoUseCase
import com.juniori.puzzle.domain.usecase.FetchMyNextVideosUseCase
import com.juniori.puzzle.domain.usecase.FetchOthersNextVideosUseCase
import com.juniori.puzzle.domain.usecase.GetMyVideosUseCase
import com.juniori.puzzle.domain.usecase.GetOthersVideosUseCase
import com.juniori.puzzle.domain.usecase.GetUserInfoByUidUseCase
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.util.GalleryType
import com.juniori.puzzle.util.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayVideoViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getUserInfoByUidUseCase: GetUserInfoByUidUseCase,
    private val getMyVideosUseCase: GetMyVideosUseCase,
    private val getOthersVideosUseCase: GetOthersVideosUseCase,
    private val fetchMyNextVideosUseCase: FetchMyNextVideosUseCase,
    private val fetchOthersNextVideosUseCase: FetchOthersNextVideosUseCase,
    private val deleteVideoUseCase: DeleteVideoUseCase,
    private val changeVideoScopeUseCase: ChangeVideoScopeUseCase
) : ViewModel() {

    var currentUserInfo: UserInfoEntity? = getUserInfo()

    var videoListFlow: StateFlow<List<VideoInfoEntity>> =
        MutableStateFlow(emptyList<VideoInfoEntity>()).asStateFlow()
        private set

    var currentVideoIndex = 0
        private set

    private val _currentVideoFlow = MutableStateFlow<VideoInfoEntity?>(null)
    val currentVideoFlow: StateFlow<VideoInfoEntity?>
        get() = _currentVideoFlow

    private val _getPublisherInfoFlow = MutableStateFlow<Resource<UserInfoEntity>?>(null)
    val getPublisherInfoFlow: StateFlow<Resource<UserInfoEntity>?> = _getPublisherInfoFlow

    private val _deleteFlow = MutableStateFlow<Resource<Unit>?>(null)
    val deleteFlow: StateFlow<Resource<Unit>?> = _deleteFlow

    private val _privacyFlow = MutableStateFlow<Resource<VideoInfoEntity>?>(null)
    val privacyFlow: StateFlow<Resource<VideoInfoEntity>?> = _privacyFlow

    private var query = ""
    private var sortType = SortType.NEW

    private lateinit var galleryType: GalleryType

    private fun getUserInfo(): UserInfoEntity? {
        val resource = getUserInfoUseCase()
        return if (resource is Resource.Success) {
            resource.result
        } else {
            null
        }
    }

    fun setData(
        query: String,
        sortType: SortType,
        clickedVideoIndex: Int,
        galleryType: GalleryType
    ) {
        videoListFlow = if (galleryType == GalleryType.MINE) {
            getMyVideosUseCase()
        } else {
            getOthersVideosUseCase()
        }

        this.query = query
        this.sortType = sortType
        this.galleryType = galleryType
        syncCurrentVideoIndex(clickedVideoIndex)
    }

    fun syncCurrentVideoIndex(index: Int) {
        currentVideoIndex = index
        if (videoListFlow.value.lastIndex >= index) {
            _currentVideoFlow.value = videoListFlow.value[index]
        }
    }

    fun fetchMoreVideos() {
        viewModelScope.launch {
            if (galleryType == GalleryType.MINE) {
                fetchMyNextVideosUseCase(
                    uid = currentUserInfo?.uid,
                    start = videoListFlow.value.size,
                    query = query
                )
            } else {
                fetchOthersNextVideosUseCase(query, sortType)
            }
        }
    }

    fun getPublisherInfo(uid: String) {
        viewModelScope.launch {
            _getPublisherInfoFlow.value = getUserInfoByUidUseCase(uid)
        }
    }

    fun deleteVideo(documentId: String) = viewModelScope.launch {
        _deleteFlow.emit(Resource.Loading)
        _deleteFlow.emit(deleteVideoUseCase(documentId, galleryType))
    }

    fun updateVideoPrivacy(documentInfo: VideoInfoEntity) = viewModelScope.launch {
        _privacyFlow.emit(Resource.Loading)
        _privacyFlow.emit(changeVideoScopeUseCase(documentInfo, galleryType))
    }
}
