package com.juniori.puzzle.ui.addvideo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.firebase.FirestoreDataSource
import com.juniori.puzzle.data.firebase.StorageDataSource
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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

    private val _uploadFlow = MutableSharedFlow<Resource<VideoInfoEntity>>(replay = 0)
    val uploadFlow: SharedFlow<Resource<VideoInfoEntity>> = _uploadFlow

    fun uploadVideo(filePath: String) = viewModelScope.launch {
        val currentUserInfo = getUserInfoUseCase()
        val uid =
            if (currentUserInfo is Resource.Success) currentUserInfo.result.uid else return@launch
        val nameToPost =
            "${uid}_${System.currentTimeMillis()}"
        _uploadFlow.emit(Resource.Loading)
        storageDataSource.insertVideo(
            nameToPost,
            File(filePath).readBytes()
        ).onSuccess {
            val result = firestoreDataSource.postVideoItem(
                uid = uid,
                videoName = nameToPost,
                isPrivate = false,
                location = "test",
                memo = "test"
            )
            _uploadFlow.emit(result)
        }.onFailure {
            _uploadFlow.emit(Resource.Failure(it as Exception))
        }
    }
}
