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

    var query = ""
    var sortType = SortType.NEW
    fun setQueryText(nowQuery: String?) {
        if (nowQuery.isNullOrBlank()) {
            query = ""
            getMyData()
            return
        }

        query = nowQuery

        viewModelScope.launch {
            val data =
                getSearchedSocialVideoListUseCase(index = 0, keyword = query, order = sortType)
            if (data is Resource.Success) {
                val result = data.result
                if (result == null || result.isEmpty()) {
                    _list.postValue(emptyList())
                } else {
                    _list.postValue(result)
                }
            } else {
                //todo network err
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
                getSocialVideoList(index = start, order = sortType)
            } else {
                getSearchedSocialVideoListUseCase(
                    index = start,
                    keyword = query,
                    order = sortType
                )
            }

            if (data is Resource.Success) {
                val result = data.result
                addItems(result)//todo empty list(paging)
            } else {
                //todo network err
            }

            _refresh.value = false
        }
    }

    fun getMyData() {
        viewModelScope.launch {
            val data = getSocialVideoList(index = 0, order = sortType)
            if (data is Resource.Success) {
                val result = data.result
                if (result == null || result.isEmpty()) {
                    _list.postValue(emptyList())
                } else {
                    _list.postValue(result)
                }
            } else {
                //todo network err
            }
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
                getMyData()
            } else {
                setQueryText(query)
            }

            return true
        }

        return false
    }
}
