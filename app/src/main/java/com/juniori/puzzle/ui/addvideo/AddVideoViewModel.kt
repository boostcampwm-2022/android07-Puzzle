package com.juniori.puzzle.ui.addvideo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddVideoViewModel : ViewModel() {

    private val _videoName = MutableLiveData<String>()
    val videoName: LiveData<String> get() = _videoName

    fun setVideoName(targetName: String) {
        _videoName.value = targetName
    }
}
