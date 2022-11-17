package com.juniori.puzzle.ui.mygallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.GlobalScope.coroutineContext

class MyGalleryViewModel : ViewModel() {
    private val _list = MutableLiveData<List<VideoMockData>>(VideoMockData.mockList(0))
    val list: LiveData<List<VideoMockData>>
        get() = _list

    private val _refresh = MutableLiveData(false)
    val refresh: LiveData<Boolean>
        get() = _refresh

    var query=""

    fun getData(start : Int){
        if(query.isBlank()&&_refresh.value == false){
            _refresh.value = true
            viewModelScope.launch(Dispatchers.IO) {
                delay(1000)
                val tempList = list.value as MutableList
                VideoMockData.mockList(start).forEach {
                    tempList.add(it)
                }
                _list.postValue(tempList)
                _refresh.postValue(false)
            }

        }else{

        }
    }

}
