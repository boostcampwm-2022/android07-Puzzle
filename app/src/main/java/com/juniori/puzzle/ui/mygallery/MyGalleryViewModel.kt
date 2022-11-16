package com.juniori.puzzle.ui.mygallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyGalleryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is my gallery Fragment"
    }
    val text: LiveData<String> = _text
}
