package com.juniori.puzzle.ui.mygallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.usecase.GetMyVideoListUseCase
import com.juniori.puzzle.domain.usecase.GetSearchedMyVideoUseCase
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.util.GalleryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyGalleryViewModel @Inject constructor(
    val getMyVideoListUseCase: GetMyVideoListUseCase,
    val getUserInfoUseCase: GetUserInfoUseCase,
    val getSearchedMyVideoUseCase: GetSearchedMyVideoUseCase
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

    private var query = ""
    private var pagingEndFlag = false

    fun setQueryText(nowQuery: String?) {
        if(query==nowQuery){
            return
        }
        query = if (nowQuery != null && nowQuery.isNotBlank()) {
            nowQuery
        } else {
            ""
        }

        getMyData()
    }

    private fun getQueryData() {
        if (refresh.value == true) {
            return
        }
        val uid = getUid()

        _list.value = emptyList()
        pagingEndFlag = false

        if (uid == null) {
            _state.value = GalleryState.NETWORK_ERROR_BASE
        } else {
            viewModelScope.launch {
                _refresh.value = true
                val data = getSearchedMyVideoUseCase(uid, 0, query)
                if (data is Resource.Success) {
                    _state.value = GalleryState.NONE

                    val result = data.result
                    if (result == null || result.isEmpty()) {

                    } else {
                        _list.value = result
                    }
                } else {
                    _state.value = GalleryState.NETWORK_ERROR_BASE
                }

                _refresh.value = false
            }
        }
    }

    private fun getBaseData() {
        if (refresh.value == true) {
            return
        }
        val uid = getUid()

        _list.value = emptyList()
        pagingEndFlag = false

        if (uid == null) {
            _state.value = GalleryState.NETWORK_ERROR_BASE
        } else {
            viewModelScope.launch {
                _refresh.value = true
                val data = getMyVideoListUseCase(uid, 0)
                if (data is Resource.Success) {
                    _state.value = GalleryState.NONE

                    val result = data.result
                    if (result == null || result.isEmpty()) {

                    } else {
                        _list.value = result
                    }
                } else {
                    _state.value = GalleryState.NETWORK_ERROR_BASE
                }

                _refresh.value = false
            }
        }
    }

    fun getPaging(start: Int) {
        if (refresh.value == true||pagingEndFlag) {
            return
        }

        val uid = getUid()
        if (uid == null) {
            _state.value = GalleryState.NETWORK_ERROR_PAGING
        } else {
            viewModelScope.launch {
                _refresh.value = true
                val data = if (query.isBlank()) {
                    getMyVideoListUseCase(uid, start)
                } else {
                    getSearchedMyVideoUseCase(uid, start, query)
                }

                if (data is Resource.Success) {
                    val result = data.result
                    if (result == null || result.isEmpty()) {
                        viewModelScope.launch(Dispatchers.IO) {
                            _state.postValue(GalleryState.END_PAGING)
                            delay(1000)
                            _state.postValue(GalleryState.NONE)
                        }
                        pagingEndFlag = true
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
    }

    fun getMyData() {
        if (query.isEmpty()) {
            getBaseData()
        } else {
            getQueryData()
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
}
