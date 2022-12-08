package com.juniori.puzzle.ui.playvideo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.usecase.ChangeVideoScopeUseCase
import com.juniori.puzzle.domain.usecase.DeleteVideoUseCase
import com.juniori.puzzle.domain.usecase.GetUserInfoByUidUseCase
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.ui.othersgallery.Repositoryk
import com.juniori.puzzle.util.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayVideoViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val deleteVideoUseCase: DeleteVideoUseCase,
    private val changeVideoScopeUseCase: ChangeVideoScopeUseCase,
    private val getUserInfoByUidUseCase: GetUserInfoByUidUseCase,
    private val repository: Repositoryk
) : ViewModel() {

    var currentUserInfo: UserInfoEntity? = getUserInfo()

    val videoListFlow: StateFlow<List<VideoInfoEntity>> =
        repository.othersVideoList

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

    private fun getUserInfo(): UserInfoEntity? {
        val resource = getUserInfoUseCase.invoke()
        return if (resource is Resource.Success) {
            resource.result
        } else {
            null
        }
    }

    fun setData(query: String, sortType: SortType, clickedVideoIndex: Int) {
        this.query = query
        this.sortType = sortType
        setCurrentVideoIndex(clickedVideoIndex)
    }

    fun setCurrentVideoIndex(index: Int) {
        currentVideoIndex = index
        if (videoListFlow.value.lastIndex >= index) {
            _currentVideoFlow.value = videoListFlow.value[index]
        }
    }

    fun fetchMoreVideos() {
        viewModelScope.launch {
            repository.getPaging(query, sortType)
        }
    }

    fun getPublisherInfo(uid: String) {
        viewModelScope.launch {
            _getPublisherInfoFlow.value = getUserInfoByUidUseCase(uid)
        }
    }

    fun deleteVideo(documentId: String) = viewModelScope.launch {
        _deleteFlow.emit(Resource.Loading)
        // _deleteFlow.emit(deleteVideoUseCase(documentId))
        _deleteFlow.emit(repository.deleteVideo(documentId))
    }

    fun updateVideoPrivacy(documentInfo: VideoInfoEntity) = viewModelScope.launch {
        _privacyFlow.emit(Resource.Loading)
        // _privacyFlow.emit(changeVideoScopeUseCase(documentInfo))
        _privacyFlow.emit(repository.changeVideoScope(documentInfo))
    }
}
