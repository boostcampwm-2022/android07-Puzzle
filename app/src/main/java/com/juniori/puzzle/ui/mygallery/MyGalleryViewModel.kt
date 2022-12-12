package com.juniori.puzzle.ui.mygallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.ui.othersgallery.Repositoryk
import com.juniori.puzzle.ui.othersgallery.VideoFetchingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyGalleryViewModel @Inject constructor(
    private val repositoryk: Repositoryk
) : ViewModel() {
    val list: StateFlow<List<VideoInfoEntity>>
        get() = repositoryk.myVideoList

    val state: StateFlow<VideoFetchingState>
        get() = repositoryk.myVideoFetchingState

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
        viewModelScope.launch {
            repositoryk.getMyPaging(start, query)
        }
    }

    fun getMyData() {
        viewModelScope.launch {
            repositoryk.getMyData(query)
        }
    }
}
