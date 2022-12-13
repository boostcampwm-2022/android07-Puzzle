package com.juniori.puzzle.ui.othersgallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.usecase.FetchOthersFirstVideosUseCase
import com.juniori.puzzle.domain.usecase.FetchOthersNextVideosUseCase
import com.juniori.puzzle.domain.usecase.GetOthersVideoFetchingStateUseCase
import com.juniori.puzzle.domain.usecase.GetOthersVideosUseCase
import com.juniori.puzzle.util.SortType
import com.juniori.puzzle.util.VideoFetchingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OthersGalleryViewModel @Inject constructor(
    getOthersVideosUseCase: GetOthersVideosUseCase,
    getVideoFetchingStateUseCase: GetOthersVideoFetchingStateUseCase,
    private val fetchOthersFirstVideosUseCase: FetchOthersFirstVideosUseCase,
    private val fetchOthersNextVideosUseCase: FetchOthersNextVideosUseCase
) : ViewModel() {

    val videoList: StateFlow<List<VideoInfoEntity>> =
        getOthersVideosUseCase()

    val videoFetchingState: StateFlow<VideoFetchingState> =
        getVideoFetchingStateUseCase()

    var query = ""
    var sortType = SortType.NEW

    init {
        if (videoList.value.isEmpty()) {
            viewModelScope.launch {
                fetchOthersFirstVideosUseCase(query, sortType)
            }
        }
    }

    fun fetchFirstVideoPage() {
        viewModelScope.launch {
            fetchOthersFirstVideosUseCase(query, sortType)
        }
    }

    fun setQueryText(nowQuery: String?) {
        if (query == nowQuery) {
            return
        }
        query = if (nowQuery != null && nowQuery.isNotBlank()) {
            nowQuery
        } else {
            ""
        }

        fetchFirstVideoPage()
    }

    fun fetchNextVideoPage() {
        viewModelScope.launch {
            fetchOthersNextVideosUseCase(query, sortType)
        }
    }

    fun setOrderType(type: SortType): Boolean {
        if (sortType != type) {
            sortType = type

            if (query.isBlank()) {
                fetchFirstVideoPage()
            } else {
                setQueryText(query)
            }

            return true
        }

        return false
    }
}
