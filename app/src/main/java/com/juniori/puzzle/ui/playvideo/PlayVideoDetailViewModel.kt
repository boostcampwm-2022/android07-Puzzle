package com.juniori.puzzle.ui.playvideo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.usecase.GetUserInfoByUidUseCase
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.domain.usecase.UpdateLikeStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayVideoDetailViewModel @Inject constructor(
    getUserInfoUseCase: GetUserInfoUseCase,
    private val updateLikeStatusUseCase: UpdateLikeStatusUseCase,
    private val getUserInfoByUidUseCase: GetUserInfoByUidUseCase
) : ViewModel() {
    private val _getLoginInfoFlow = MutableStateFlow<Resource<UserInfoEntity>?>(null)
    val getLoginInfoFlow: StateFlow<Resource<UserInfoEntity>?> = _getLoginInfoFlow

    private val _getPublisherInfoFlow = MutableStateFlow<Resource<UserInfoEntity>?>(null)
    val getPublisherInfoFlow: StateFlow<Resource<UserInfoEntity>?> = _getPublisherInfoFlow

    private val _videoFlow = MutableStateFlow<Resource<VideoInfoEntity>?>(null)
    val videoFlow: StateFlow<Resource<VideoInfoEntity>?> = _videoFlow

    private val _likeState = MutableStateFlow(false)
    val likeState: StateFlow<Boolean>
        get() = _likeState

    init {
        _getLoginInfoFlow.value = getUserInfoUseCase()
    }

    fun initVideoFlow(currentVideo: VideoInfoEntity) {
        viewModelScope.launch {
            _videoFlow.emit(Resource.Success(currentVideo))
        }
    }

    fun getPublisherInfo(uid: String) {
        viewModelScope.launch {
            _getPublisherInfoFlow.value = getUserInfoByUidUseCase(uid)
        }
    }

    fun setCurrentLikeStatus(currentVideo: VideoInfoEntity, currentUid: String) {
        viewModelScope.launch {
            _likeState.emit(currentVideo.likedUserUidList.contains(currentUid))
        }
    }

    fun changeLikeStatus(currentVideo: VideoInfoEntity, currentUid: String) {
        viewModelScope.launch {
            _videoFlow.emit(Resource.Loading)
            val result = updateLikeStatusUseCase(currentVideo, currentUid, likeState.value)
            if (result is Resource.Success) {
                _videoFlow.emit(result)
                _likeState.emit(likeState.value.not())
            }
        }
    }
}
