package com.juniori.puzzle.ui.mygallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.usecase.GetMyVideoListUseCase
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.mock.getVideoListMockData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class MyGalleryViewModel @Inject constructor(
    val getMyVideoListUseCase: GetMyVideoListUseCase,
    val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {
    private var myData = listOf<VideoInfoEntity>()

    private val _list = MutableLiveData<List<VideoInfoEntity>>()
    val list: LiveData<List<VideoInfoEntity>>
        get() = _list

    fun setQueryText(nowQuery: String?) {
        if (nowQuery.isNullOrBlank()) {
            _list.value = myData
            return
        }

        val regex = """.*$nowQuery.*""".toRegex()
        val targetList = myData.filter {
            regex.matches(it.location)
        }
        _list.value = targetList
    }

    fun getMyData() {
        val userInfo = getUserInfoUseCase()
        val uid: String? = if (userInfo is Resource.Success) {
            userInfo.result.uid
        } else {
            null
        }

        if (uid==null){
            //todo network err
        }else {
            viewModelScope.launch {
                val data = getMyVideoListUseCase(uid,0)
                if(data is Resource.Success){
                    myData = data.result
                }else{
                    //todo network err
                }
                _list.postValue(myData)
            }
        }
    }
}
