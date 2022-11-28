package com.juniori.puzzle.ui.addvideo

import androidx.lifecycle.*
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
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
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

    private var videoName: String = ""
    val videoFilePath get() = "$cacheDirPath/$videoName.mp4"
    var playPosition: Long = 0
        private set
    var playWhenReady = true
        private set

    var comments: String = ""
    var golfCourse = ""
    var isPublicUpload = false

    private val _uploadFlow = MutableSharedFlow<Resource<VideoInfoEntity>>(replay = 0)
    val uploadFlow: SharedFlow<Resource<VideoInfoEntity>> = _uploadFlow

    private val _uiState = MutableLiveData<AddVideoUiState>(AddVideoUiState.NONE)
    val uiState: LiveData<AddVideoUiState> get() = _uiState

    private var isUploadingToServer = false

    private val pickedCalendar = MutableLiveData(Calendar.getInstance())
    val pickedDate: LiveData<Date> = pickedCalendar.map { calendar -> calendar.time }

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
                }
            }
            is AddVideoActionState.TakingVideoCompleted -> {
                videoName = actionState.videoName
            }
        }
    }

    fun uploadVideo() = viewModelScope.launch {
        if (isUploadingToServer) {
            return@launch
        }
        val uid = getUid() ?: return@launch
        val videoName = videoName
        val thumbnailBytes = videoMetaDataUtil.extractThumbnail(videoFilePath) ?: return@launch

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
                    memo = comments
                )
                _uploadFlow.emit(result)
            }.onFailure {
                _uploadFlow.emit(Resource.Failure(it as Exception))
            }
        }
        isUploadingToServer = false
    }

    private fun getUid(): String? {
        val currentUserInfo = getUserInfoUseCase()
        return if (currentUserInfo is Resource.Success) {
            currentUserInfo.result.uid
        } else {
            null
        }
    }

    fun saveComments(comments: String) {
        this.comments = comments
    }

    private fun initializeUploading() {
        videoName = ""
        playPosition = 0
        playWhenReady = true
        comments = ""
        _uiState.value = AddVideoUiState.NONE
        videoFilePath.deleteIfFileUri()
    }

    fun changeDatesOfCalendar(year: Int, month: Int, dayOfMonth: Int) {
        pickedCalendar.value?.let { calendar ->
            calendar.set(year, month, dayOfMonth)
            pickedCalendar.value = calendar
        }
    }

    fun changeTimeOfCalendar(hourOfDay: Int, minute: Int) {
        pickedCalendar.value?.let { calendar ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            pickedCalendar.value = calendar
        }
    }

    fun onGolfCourseTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
        golfCourse = charSequence.toString()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        initializeUploading()
    }
}
