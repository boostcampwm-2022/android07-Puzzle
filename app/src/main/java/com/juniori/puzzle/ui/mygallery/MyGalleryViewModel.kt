package com.juniori.puzzle.ui.mygallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.usecase.FetchMyFirstVideosUseCase
import com.juniori.puzzle.domain.usecase.FetchMyNextVideosUseCase
import com.juniori.puzzle.domain.usecase.GetMyVideoFetchingStateUseCase
import com.juniori.puzzle.domain.usecase.GetMyVideosUseCase
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.util.VideoFetchingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyGalleryViewModel @Inject constructor(
    getMyVideosUseCase: GetMyVideosUseCase,
    getVideoFetchingStateUseCase: GetMyVideoFetchingStateUseCase,
    private val fetchMyFirstVideosUseCase: FetchMyFirstVideosUseCase,
    private val fetchMyNextVideosUseCase: FetchMyNextVideosUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    val videoList: StateFlow<List<VideoInfoEntity>> =
        getMyVideosUseCase()

    val videoFetchingState: StateFlow<VideoFetchingState> =
        getVideoFetchingStateUseCase()

    private var query = ""

    fun setQueryText(nowQuery: String?) {
        if (query == nowQuery) {
            return
        }
        query = if (nowQuery != null && nowQuery.isNotBlank()) {
            nowQuery
        } else {
            ""
        }

        getMyData()
    }

    fun getPaging(start: Int) {
        val uid = getUid()
        viewModelScope.launch {
            fetchMyNextVideosUseCase(uid, start, query)
        }
    }

    fun getMyData() {
        val uid = getUid()
        viewModelScope.launch {
            fetchMyFirstVideosUseCase(uid, query)
        }
    }

    private fun getUid(): String? {
        val userInfo = getUserInfoUseCase()
        val uid: String? = if (userInfo is Resource.Success) {
            userInfo.result.uid
        } else {
            null
        }

        return uid
    }
}
