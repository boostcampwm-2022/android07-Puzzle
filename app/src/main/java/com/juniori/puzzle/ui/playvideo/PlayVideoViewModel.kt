package com.juniori.puzzle.ui.playvideo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.firebase.FirestoreDataSource
import com.juniori.puzzle.data.firebase.StorageDataSource
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayVideoViewModel @Inject constructor(
    getUserInfoUseCase: GetUserInfoUseCase,
    private val firestoreDataSource: FirestoreDataSource,
    private val storageDataSource: StorageDataSource
) : ViewModel() {
    private val _getLoginInfoFlow = MutableStateFlow<Resource<UserInfoEntity>?>(null)
    val getLoginInfoFlow: StateFlow<Resource<UserInfoEntity>?> = _getLoginInfoFlow

    private val _getPublisherInfoFlow = MutableStateFlow<Resource<UserInfoEntity>?>(null)
    val getPublisherInfoFlow: StateFlow<Resource<UserInfoEntity>?> = _getPublisherInfoFlow

    private val _deleteFlow = MutableStateFlow<Resource<Unit>?>(null)
    val deleteFlow: StateFlow<Resource<Unit>?> = _deleteFlow

    private val _updateFlow = MutableStateFlow<Resource<VideoInfoEntity>?>(null)
    val updateFlow: StateFlow<Resource<VideoInfoEntity>?> = _updateFlow

    private val _likeState = MutableStateFlow(false)
    val likeState: StateFlow<Boolean>
        get() = _likeState

    init {
        _getLoginInfoFlow.value = getUserInfoUseCase()
    }

    fun getPublisherInfo(uid: String) {
        viewModelScope.launch {
            _getPublisherInfoFlow.value = firestoreDataSource.getUserItem(uid)
        }
    }

    fun setCurrentLikeStatus(currentVideo: VideoInfoEntity, currentUid: String) {
        viewModelScope.launch {
            _likeState.emit(currentVideo.likedUserUidList.contains(currentUid))
        }
    }

    fun changeLikeStatus(currentVideo: VideoInfoEntity, currentUid: String) {
        if (likeState.value) {
            cancelLike(currentVideo, currentUid)
        } else {
            like(currentVideo, currentUid)
        }
    }

    private fun like(currentVideo: VideoInfoEntity, currentUid: String) {
        viewModelScope.launch {
            val result = firestoreDataSource.addVideoItemLike(
                currentVideo, currentUid
            )
            if (result is Resource.Success) {
                _updateFlow.emit(result)
                _likeState.emit(true)
            }
        }
    }

    private fun cancelLike(currentVideo: VideoInfoEntity, currentUid: String) {
        viewModelScope.launch {
            val result = firestoreDataSource.removeVideoItemLike(
                currentVideo, currentUid
            )
            if (result is Resource.Success) {
                _updateFlow.emit(result)
                _likeState.emit(false)
            }
        }
    }

    fun deleteVideo(documentId: String) = viewModelScope.launch {
        _deleteFlow.emit(Resource.Loading)
        storageDataSource.deleteVideo(documentId).onSuccess {
            _deleteFlow.emit(firestoreDataSource.deleteVideoItem(documentId))
        }.onFailure {
            _deleteFlow.emit(Resource.Failure(it as Exception))
        }
    }

    fun updateVideoPrivacy(documentInfo: VideoInfoEntity) = viewModelScope.launch {
        _updateFlow.emit(Resource.Loading)
        _updateFlow.emit(firestoreDataSource.changeVideoItemPrivacy(documentInfo))
    }
}