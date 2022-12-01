package com.juniori.puzzle.ui.addvideo

import androidx.lifecycle.ViewModel
import com.juniori.puzzle.util.VideoMetaDataUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AddVideoViewModel @Inject constructor(
    private val videoMetaDataUtil: VideoMetaDataUtil
) : ViewModel() {

    var videoFilePath = ""
        private set
    var thumbnailBytes = ByteArray(0)
        private set

    private val _uiState = MutableStateFlow<AddVideoUiState?>(null)
    val uiState: StateFlow<AddVideoUiState?> get() = _uiState

    fun notifyTakingVideoFinished(videoFilePath: String) {
        this.videoFilePath = videoFilePath
        thumbnailBytes = videoMetaDataUtil.extractThumbnail(videoFilePath) ?: return
        _uiState.value = AddVideoUiState.GO_TO_UPLOAD
    }

    fun notifyVideoPicked(videoFilePath: String) {
        this.videoFilePath = videoFilePath
        val durationInSeconds = videoMetaDataUtil.getVideoDurationInSeconds(videoFilePath) ?: return
        if (durationInSeconds > AddVideoBottomSheet.VIDEO_DURATION_LIMIT_SECONDS) {
            _uiState.value = AddVideoUiState.SHOW_DURATION_LIMIT_FEEDBACK
        } else {
            thumbnailBytes = videoMetaDataUtil.extractThumbnail(videoFilePath) ?: return
            _uiState.value = AddVideoUiState.GO_TO_UPLOAD
        }
    }
}
