package com.juniori.puzzle.ui.addvideo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.firebase.FirestoreDataSource
import com.juniori.puzzle.data.firebase.StorageDataSource
import com.juniori.puzzle.data.firebase.dto.VideoItem
import com.juniori.puzzle.domain.repository.AuthRepository
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddVideoViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val storageDataSource: StorageDataSource,
    private val firestoreDataSource: FirestoreDataSource
) : ViewModel() {

    private val _videoName = MutableLiveData<String>()
    val videoName: LiveData<String> get() = _videoName

    fun setVideoName(targetName: String) {
        _videoName.value = targetName
    }

    private val _uploadFlow = MutableStateFlow<Resource<VideoItem>?>(null)
    val uploadFlow: StateFlow<Resource<VideoItem>?> = _uploadFlow

    fun uploadVideo(filePath: String) = viewModelScope.launch {
        val currentUserInfo = getUserInfoUseCase()
        val uid =
            if (currentUserInfo is Resource.Success) currentUserInfo.result.uid else return@launch
        val videoNameToPost =
            "${uid}_${System.currentTimeMillis()}"
        _uploadFlow.emit(Resource.Loading)
        storageDataSource.insertVideo(
            videoNameToPost,
            File(filePath).readBytes()
        ).onSuccess {
            val result = firestoreDataSource.postVideoItem(
                uid = uid,
                videoName = videoNameToPost,
                isPrivate = false,
                location = "test",
                memo = "test"
            )
            _uploadFlow.emit(result)
        }

    }
}
