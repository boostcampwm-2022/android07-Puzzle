package com.juniori.puzzle.ui.addvideo

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.di.LocalCacheModule
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.domain.usecase.PostVideoUseCase
import com.juniori.puzzle.util.VideoMetaDataUtil
import com.juniori.puzzle.util.deleteIfFileUri
import com.juniori.puzzle.util.saveInFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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
    private val postVideoUseCase: PostVideoUseCase
) : ViewModel(), DefaultLifecycleObserver {

    private var videoName: String = ""
    val videoFilePath get() = "$cacheDirPath/$videoName.mp4"
    var thumbnailBytes = ByteArray(0)
        private set

    var playPosition: Long = 0
        private set
    var playWhenReady = true
        private set

    var memo = ""
    var golfCourse = ""
    var isPublicUpload = false

    private val _uploadFlow = MutableSharedFlow<Resource<VideoInfoEntity>>(replay = 0)
    val uploadFlow: SharedFlow<Resource<VideoInfoEntity>> = _uploadFlow

    private val _uiState = MutableLiveData<AddVideoUiState>(AddVideoUiState.NONE)
    val uiState: LiveData<AddVideoUiState> get() = _uiState

    private var isUploadingToServer = false

    fun setVideoName(targetName: String) {
        videoName = targetName
    }

    fun saveVideoPlayState(playBackPosition: Long, wasBeingPlayed: Boolean) {
        playPosition = playBackPosition
        playWhenReady = wasBeingPlayed
    }

    fun notifyAction(actionState: AddVideoActionState) {
        when (actionState) {
            is AddVideoActionState.StartingToAdd -> {
                _uiState.value = AddVideoUiState.NONE
            }
            is AddVideoActionState.VideoPicked -> {
                val durationInSeconds: Long =
                    videoMetaDataUtil.getVideoDurationInSeconds(actionState.uri) ?: return
                if (durationInSeconds > AddVideoBottomSheet.VIDEO_DURATION_LIMIT_SECONDS) {
                    _uiState.value = AddVideoUiState.SHOW_DURATION_LIMIT_FEEDBACK
                    return
                }

                val uid = getUid() ?: return
                videoName = "${uid}_${System.currentTimeMillis()}"
                viewModelScope.launch(Dispatchers.IO) {
                    actionState.videoBytes.saveInFile(videoFilePath)
                    _uiState.postValue(AddVideoUiState.GO_TO_UPLOAD)
                    thumbnailBytes =
                        videoMetaDataUtil.extractThumbnail(videoFilePath) ?: return@launch
                }
            }
            is AddVideoActionState.TakingVideoCompleted -> {
                videoName = actionState.videoName
                thumbnailBytes = videoMetaDataUtil.extractThumbnail(videoFilePath) ?: return
            }
        }
    }

    fun uploadVideo() = viewModelScope.launch {
        if (isUploadingToServer) {
            return@launch
        }
        val uid = getUid() ?: return@launch

        isUploadingToServer = true
        _uploadFlow.emit(Resource.Loading)
        _uploadFlow.emit(
            postVideoUseCase(
                uid,
                videoName,
                isPublicUpload.not(),
                golfCourse,
                memo,
                File(videoFilePath).readBytes(),
                thumbnailBytes
            )
        )
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

    fun saveMemo(comments: String) {
        this.memo = comments
    }

    private fun initializeUploading() {
        videoName = ""
        playPosition = 0
        playWhenReady = true
        memo = ""
        _uiState.value = AddVideoUiState.NONE
        videoFilePath.deleteIfFileUri()
    }

    fun onGolfCourseTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
        golfCourse = charSequence.toString()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        initializeUploading()
    }
}
