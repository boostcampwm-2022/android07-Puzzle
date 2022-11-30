package com.juniori.puzzle.ui.othersgallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.usecase.GetSearchedSocialVideoListUseCase
import com.juniori.puzzle.domain.usecase.GetSocialVideoListUseCase
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.util.GalleryState
import com.juniori.puzzle.util.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OthersGalleryViewModel @Inject constructor(
    val getSocialVideoList: GetSocialVideoListUseCase,
    val getUserInfoUseCase: GetUserInfoUseCase,
    val getSearchedSocialVideoListUseCase: GetSearchedSocialVideoListUseCase
) : ViewModel() {
    private val _list = MutableLiveData<List<VideoInfoEntity>>()
    val list: LiveData<List<VideoInfoEntity>>
        get() = _list

    private val _refresh = MutableLiveData(false)
    val refresh: LiveData<Boolean>
        get() = _refresh

    private val _state = MutableLiveData(GalleryState.NONE)
    val state: LiveData<GalleryState>
        get() = _state

    var query = ""
    var sortType = SortType.NEW
    fun setQueryText(nowQuery: String?) {
        query = if (nowQuery != null && nowQuery.isNotBlank()) {
            nowQuery
        } else {
            ""
        }

        getMainData()
    }


    private fun getQueryData() {
        viewModelScope.launch {
            val data = getSearchedSocialVideoListUseCase(
                index = 0,
                keyword = query,
                order = sortType
            )

            if (data is Resource.Success) {
                val result = data.result
                if (result == null || result.isEmpty()) {
                    _list.postValue(emptyList())
                } else {
                    _list.postValue(result)
                }
            } else {
                _state.value = GalleryState.NETWORK_ERROR_BASE
            }
        }
    }

    private fun getBaseData() {
        viewModelScope.launch {
            val data = getSocialVideoList(
                index = 0,
                order = sortType
            )
            if (data is Resource.Success) {
                val result = data.result
                if (result == null || result.isEmpty()) {
                    _list.postValue(emptyList())
                } else {
                    _list.postValue(result)
                }
            } else {
                _state.value = GalleryState.NETWORK_ERROR_BASE
            }
        }
    }

    fun getPaging(start: Int) {
        if (refresh.value == true) {
            return
        }

        viewModelScope.launch {
            _refresh.value = true
            val data = if (query.isBlank()) {
                getSocialVideoList(
                    index = start,
                    order = sortType
                )
            } else {
                getSearchedSocialVideoListUseCase(
                    index = start,
                    keyword = query,
                    order = sortType
                )
            }

            if (data is Resource.Success) {
                val result = data.result
                if (result == null || result.isEmpty()) {
                    _state.value = GalleryState.END_PAGING
                } else {
                    _state.value = GalleryState.NONE
                    addItems(result)
                }
            } else {
                _state.value = GalleryState.NETWORK_ERROR_PAGING
            }

            _refresh.value = false
        }
    }

    fun getMainData() {
        if (query.isEmpty()) {
            getBaseData()
        } else {
            getQueryData()
        }
    }

    private fun addItems(items: List<VideoInfoEntity>) {
        val newList = mutableListOf<VideoInfoEntity>()
        _list.value?.forEach {
            newList.add(it)
        }

        items.forEach {
            newList.add(it)
        }

        _list.value = newList
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
