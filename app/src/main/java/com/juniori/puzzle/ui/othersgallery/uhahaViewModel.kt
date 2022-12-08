package com.juniori.puzzle.ui.othersgallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.usecase.GetSearchedSocialVideoListUseCase
import com.juniori.puzzle.domain.usecase.GetSocialVideoListUseCase
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.util.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OthersGalleryViewModelk @Inject constructor(
    val getSocialVideoList: GetSocialVideoListUseCase,
    val getUserInfoUseCase: GetUserInfoUseCase,
    val getSearchedSocialVideoListUseCase: GetSearchedSocialVideoListUseCase,
    val repositoryk: Repositoryk
) : ViewModel() {

    val videoList: StateFlow<List<VideoInfoEntity>>
        get() = repositoryk.othersVideoList

    val videoFetchingState: StateFlow<VideoFetchingState>
        get() = repositoryk.fetchingState

    var query = ""
    var sortType = SortType.NEW

    init {
        if (videoList.value.isEmpty()) {
            viewModelScope.launch {
                repositoryk.getMainData(query, sortType)
            }
        }
    }

    fun getMainData() {
        viewModelScope.launch {
            repositoryk.getMainData(query, sortType)
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

        getMainData()
    }

    fun getPaging() {
        viewModelScope.launch {
            repositoryk.getPaging(query, sortType)
        }
    }

    fun setOrderType(type: SortType): Boolean {
        if (sortType != type) {
            sortType = type

            if (query.isBlank()) {
                getMainData()
            } else {
                setQueryText(query)
            }

            return true
        }

        return false
    }
}

