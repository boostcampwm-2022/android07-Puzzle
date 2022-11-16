package com.juniori.puzzle.ui.othersgallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OthersGalleryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is others gallery Fragment"
    }
    val text: LiveData<String> = _text
}
