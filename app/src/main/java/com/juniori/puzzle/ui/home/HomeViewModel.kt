package com.juniori.puzzle.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _welcomeText = MutableLiveData<String>("")
    val welcomeText: LiveData<String> = _welcomeText

    fun setWelcomText(text: String) {
        _welcomeText.value = text
    }
}
