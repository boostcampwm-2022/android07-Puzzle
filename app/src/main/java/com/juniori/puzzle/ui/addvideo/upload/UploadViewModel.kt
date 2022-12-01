package com.juniori.puzzle.ui.addvideo.upload

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.firebase.FirestoreDataSource
import com.juniori.puzzle.data.firebase.StorageDataSource
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.util.deleteIfFileUri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val storageDataSource: StorageDataSource,
    private val firestoreDataSource: FirestoreDataSource
) : ViewModel(), DefaultLifecycleObserver {

    var videoFilePath = ""
        private set
    var thumbnailBytes = ByteArray(0)
        private set

    var playPosition: Long = 0
        private set
    var playWhenReady = true
        private set

    var memo = ""
        private set
    var golfCourse = ""
        private set
    var isPublicUpload = false

    private val _uploadFlow = MutableStateFlow<Resource<VideoInfoEntity>?>(null)
    val uploadFlow: StateFlow<Resource<VideoInfoEntity>?> = _uploadFlow

    fun initializeMediaData(videoFilePath: String, thumbnailBytes: ByteArray) {
        this.videoFilePath = videoFilePath
        this.thumbnailBytes = thumbnailBytes
    }

    fun saveVideoPlayState(playBackPosition: Long, wasBeingPlayed: Boolean) {
        playPosition = playBackPosition
        playWhenReady = wasBeingPlayed
    }

    fun uploadVideo() = viewModelScope.launch {
        if (_uploadFlow.value == Resource.Loading) {
            return@launch
        }
        _uploadFlow.emit(Resource.Loading)

        val uid = getUid() ?: return@launch
        val videoName = "${uid}_${System.currentTimeMillis()}"

        storageDataSource.insertThumbnail(videoName, thumbnailBytes).onSuccess {
            storageDataSource.insertVideo(
                videoName,
                File(videoFilePath).readBytes()
            ).onSuccess {
                val result = firestoreDataSource.postVideoItem(
                    uid = uid,
                    videoName = videoName,
                    isPrivate = isPublicUpload.not(),
                    location = golfCourse,
                    memo = memo
                )
                _uploadFlow.emit(result)
            }.onFailure {
                _uploadFlow.emit(Resource.Failure(it as Exception))
            }
        }
    }

    private fun getUid(): String? {
        val currentUserInfo = getUserInfoUseCase()
        return if (currentUserInfo is Resource.Success) {
            currentUserInfo.result.uid
        } else {
            null
        }
    }

    private fun removeCurrentData() {
        playPosition = 0
        playWhenReady = true
        memo = ""
        golfCourse = ""
        videoFilePath.deleteIfFileUri()
        _uploadFlow.value = null
    }

    fun onMemoTextChanged(charSequence: CharSequence) {
        memo = charSequence.toString()
    }

    fun onGolfCourseTextChanged(charSequence: CharSequence) {
        golfCourse = charSequence.toString()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        initializeMediaData("", ByteArray(0))
        removeCurrentData()
    }
}
