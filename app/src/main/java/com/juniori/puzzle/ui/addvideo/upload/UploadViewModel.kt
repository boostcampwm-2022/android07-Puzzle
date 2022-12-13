package com.juniori.puzzle.ui.addvideo.upload

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS
import com.arthenica.mobileffmpeg.FFmpeg
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.domain.usecase.PostVideoUseCase
import com.juniori.puzzle.util.deleteIfFileUri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val postVideoUseCase: PostVideoUseCase
) : ViewModel(), DefaultLifecycleObserver {

    var videoFilePath = ""
        private set
    private var compressedVideoFilePath = ""
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

    private val _compressFlow = MutableStateFlow(-1)
    val compressFlow: StateFlow<Int> = _compressFlow

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

    fun compressVideo(context: Context) = viewModelScope.launch {
        if (_compressFlow.value >= 0) return@launch
        _compressFlow.emit(0)

        val videoLength = MediaPlayer.create(context, Uri.parse(videoFilePath)).duration
        compressedVideoFilePath = "${videoFilePath.substringBeforeLast(".mp4")}_compressed.mp4"

        Config.resetStatistics()
        Config.enableStatisticsCallback {
            val percentile = it.time * 100 / videoLength
            if (percentile <= 100) {
                _compressFlow.value = percentile
            }
        }

        FFmpeg.executeAsync(
            getFFMPEGCommand(
                videoFilePath,
                compressedVideoFilePath
            )
        ) { _, returnCode ->
            when (returnCode) {
                RETURN_CODE_SUCCESS -> {
                    uploadVideo()
                }
                else -> {
                    _uploadFlow.value = Resource.Failure(Exception("Compress Failed"))
                }
            }
        }
    }

    private fun getFFMPEGCommand(inputPath: String, outputPath: String) =
        "-i $inputPath -c:v mpeg4 -crf 18 -preset veryfast $outputPath"

    private fun uploadVideo() = viewModelScope.launch {
        if (_uploadFlow.value == Resource.Loading) return@launch
        _uploadFlow.emit(Resource.Loading)

        val uid = getUid() ?: return@launch
        val videoName = "${uid}_${System.currentTimeMillis()}"
        _uploadFlow.emit(
            postVideoUseCase(
                uid = uid,
                videoName = videoName,
                isPrivate = isPublicUpload.not(),
                location = golfCourse,
                memo = memo,
                imageByteArray = thumbnailBytes,
                videoByteArray = File(compressedVideoFilePath).readBytes()
            )
        )
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
        compressedVideoFilePath.deleteIfFileUri()
        _uploadFlow.value = null
        _compressFlow.value = -1
    }

    fun onMemoTextChanged(charSequence: CharSequence) {
        memo = charSequence.toString()
    }

    fun onGolfCourseTextChanged(charSequence: CharSequence) {
        golfCourse = charSequence.toString()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        removeCurrentData()
        initializeMediaData("", ByteArray(0))
    }
}
