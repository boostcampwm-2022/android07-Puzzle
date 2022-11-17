package com.juniori.puzzle.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.BuildConfig
import com.juniori.puzzle.data.weather.WeatherRepository
import com.juniori.puzzle.network.WeatherService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: WeatherRepository
): ViewModel() {

    private val _welcomeText = MutableLiveData("")
    val welcomeText: LiveData<String> = _welcomeText

    fun setWelcomeText(text: String) {
        _welcomeText.value = text
    }

    fun getWeather(){
        viewModelScope.launch {
            val result=repository.getWeather(55,127)
        }
    }

}
