package com.juniori.puzzle.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyPageViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is my page Fragment"
    }
    val text: LiveData<String> = _text
}
