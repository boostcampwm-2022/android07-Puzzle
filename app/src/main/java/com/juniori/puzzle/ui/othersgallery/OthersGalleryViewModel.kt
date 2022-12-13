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
import com.juniori.puzzle.util.PagingConst.ITEM_CNT
import com.juniori.puzzle.util.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    private var lastLikeCount = Long.MAX_VALUE
    private var lastTime = Long.MAX_VALUE
    var lastOffset = 0

    var pagingEndFlag = false
        private set

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

    private fun getQueryData() {
        if (refresh.value == true) {
            return
        }
        _list.value = emptyList()
        pagingEndFlag = false
        setLastData(Long.MAX_VALUE, Long.MAX_VALUE, 0)
        viewModelScope.launch {

            _refresh.value = true
            val data = getSearchedSocialVideoListUseCase(
                index = 0,
                keyword = query,
                order = sortType
            )

            if (data is Resource.Success) {
                val result = data.result
                if (result.isNullOrEmpty().not()) {
                    result.last().also {
                        setLastData(it.updateTime, it.likedCount.toLong(), result.countWith(it))
                    }
                    if (result.size < ITEM_CNT) {
                        pagingEndFlag = true
                    }
                    _list.value = result
                }
            } else {
                _state.value = GalleryState.NETWORK_ERROR_BASE
            }

            _refresh.value = false
        }
    }

    private fun getBaseData() {
        if (refresh.value == true) {
            return
        }
        _list.value = emptyList()
        pagingEndFlag = false
        setLastData(Long.MAX_VALUE, Long.MAX_VALUE, 0)
        viewModelScope.launch {
            _refresh.value = true
            val data = getSocialVideoList(
                index = 0,
                order = sortType
            )

            if (data is Resource.Success) {
                val result = data.result
                if (result.isNullOrEmpty().not()) {
                    result.last().also {
                        setLastData(it.updateTime, it.likedCount.toLong(), result.countWith(it))
                    }
                    if (result.size < ITEM_CNT) {
                        pagingEndFlag = true
                    }
                    _list.value = result
                }
            } else {
                _state.value = GalleryState.NETWORK_ERROR_BASE
            }

            _refresh.value = false
        }
    }

    fun getPaging() {
        if (refresh.value == true || pagingEndFlag) {
            return
        }
        viewModelScope.launch {
            _refresh.value = true
            val data = if (query.isBlank()) {
                getSocialVideoList(
                    index = lastOffset,
                    order = sortType,
                    latestData = when (sortType) {
                        SortType.LIKE -> lastLikeCount
                        SortType.NEW -> lastTime
                    }
                )
            } else {
                getSearchedSocialVideoListUseCase(
                    index = list.value?.size ?: 0,
                    keyword = query,
                    order = sortType,
                    latestData = when (sortType) {
                        SortType.LIKE -> lastLikeCount
                        SortType.NEW -> lastTime
                    }
                )
            }
            if (data is Resource.Success) {
                val result = data.result
                if (result.isNullOrEmpty()) {
                    viewModelScope.launch(Dispatchers.IO) {
                        _state.postValue(GalleryState.END_PAGING)
                        delay(1000)
                        _state.postValue(GalleryState.NONE)
                    }
                    pagingEndFlag = true
                } else {
                    result.last().also {
                        setLastData(it.updateTime, it.likedCount.toLong(), result.countWith(it))
                    }
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
            if (list.value == null || requireNotNull(list.value).contains(it).not()) {
                newList.add(it)
            }
        }

        _list.value = newList
    }

    private fun setLastData(time: Long, like: Long, offset: Int) {
        if (sortType == SortType.NEW && lastTime == time) {
            lastOffset += offset
        } else if (sortType == SortType.LIKE && lastLikeCount == like) {
            lastOffset += offset
        } else {
            lastOffset = offset
        }
        lastTime = time
        lastLikeCount = like
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

    private fun List<VideoInfoEntity>.countWith(base: VideoInfoEntity): Int {
        if (sortType == SortType.NEW) {
            var cnt = 0
            this.forEach {
                if (it.updateTime == base.updateTime) {
                    cnt++
                }
            }
            return cnt
        } else {
            var cnt = 0
            this.forEach {
                if (it.likedCount == base.likedCount) {
                    cnt++
                }
            }
            return cnt
        }
    }
}
