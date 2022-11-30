package com.juniori.puzzle.ui.addvideo

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.firebase.FirestoreDataSource
import com.juniori.puzzle.data.firebase.StorageDataSource
import com.juniori.puzzle.di.LocalCacheModule
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.util.VideoMetaDataUtil
import com.juniori.puzzle.util.deleteIfFileUri
import com.juniori.puzzle.util.saveInFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AddVideoViewModel @Inject constructor(
    @Named(LocalCacheModule.CACHE_DIR_PATH)
    private val cacheDirPath: String,
    private val videoMetaDataUtil: VideoMetaDataUtil,
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

    private val _uploadFlow = MutableSharedFlow<Resource<VideoInfoEntity>>(replay = 0)
    val uploadFlow: SharedFlow<Resource<VideoInfoEntity>> = _uploadFlow

    private val _uiState = MutableStateFlow<AddVideoUiState?>(null)
    val uiState: StateFlow<AddVideoUiState?> get() = _uiState

    private var isUploadingToServer = false

    fun saveVideoPlayState(playBackPosition: Long, wasBeingPlayed: Boolean) {
        playPosition = playBackPosition
        playWhenReady = wasBeingPlayed
    }

    fun notifyAction(actionState: AddVideoActionState) {
        when (actionState) {
            is AddVideoActionState.VideoPicked -> {
                val durationInSeconds: Long =
                    videoMetaDataUtil.getVideoDurationInSeconds(actionState.uri) ?: return
                if (durationInSeconds > AddVideoBottomSheet.VIDEO_DURATION_LIMIT_SECONDS) {
                    _uiState.value = AddVideoUiState.SHOW_DURATION_LIMIT_FEEDBACK
                    return
                }

                viewModelScope.launch(Dispatchers.IO) {
                    actionState.videoBytes.saveInFile(videoFilePath)
                    _uiState.value = AddVideoUiState.GO_TO_UPLOAD
                    thumbnailBytes = videoMetaDataUtil.extractThumbnail(videoFilePath) ?: return@launch
                }
            }
            is AddVideoActionState.TakingVideoCompleted -> {
                videoFilePath = "$cacheDirPath/${actionState.videoName}.mp4"
                thumbnailBytes = videoMetaDataUtil.extractThumbnail(videoFilePath) ?: return
                _uiState.value = AddVideoUiState.GO_TO_UPLOAD
            }
        }
    }

    fun uploadVideo() = viewModelScope.launch {
        if (isUploadingToServer) {
            return@launch
        }
        val uid = getUid() ?: return@launch
        val videoName = "${uid}_${System.currentTimeMillis()}"

        isUploadingToServer = true
        _uploadFlow.emit(Resource.Loading)

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
        isUploadingToServer = false
    }

    fun getUid(): String? {
        val currentUserInfo = getUserInfoUseCase()
        return if (currentUserInfo is Resource.Success) {
            currentUserInfo.result.uid
        } else {
            null
        }
    }

    private fun initializeUploading() {
        playPosition = 0
        playWhenReady = true
        memo = ""
        videoFilePath.deleteIfFileUri()
    }

    fun onMemoTextChanged(charSequence: CharSequence) {
        memo = charSequence.toString()
    }

    fun onGolfCourseTextChanged(charSequence: CharSequence) {
        golfCourse = charSequence.toString()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        initializeUploading()
    }
}
