package com.juniori.puzzle.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.weather.WeatherRepository
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    private val _welcomeText = MutableLiveData("")
    val welcomeText: LiveData<String> = _welcomeText

    private val _weatherInfoText = MutableLiveData("날씨 보기")
    val weatherInfoText: LiveData<String> = _weatherInfoText

    private val _displayName = MutableLiveData("")
    val displayName: LiveData<String> = _displayName

    fun setDisplayName() {
        val userInfo = getUserInfoUseCase()
        if (userInfo is Resource.Success) {
            _displayName.value = "${userInfo.result.nickname}님"
        }
        else {
            _displayName.value = "누구세요?"
        }
    }

    fun setWelcomeText(text: String) {
        _welcomeText.value = text
    }

    fun setWeatherInfoText(text: String) {
        _weatherInfoText.value = text
    }

    fun getWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val result = repository.getWeather(latitude, longitude)
        }
    }

}
