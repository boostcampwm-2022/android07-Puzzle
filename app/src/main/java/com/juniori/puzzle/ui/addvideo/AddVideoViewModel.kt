package com.juniori.puzzle.ui.addvideo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddVideoViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is add video dialog"
    }
    val text: LiveData<String> = _text
}
