package com.juniori.puzzle.ui.home

import android.location.Address
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.weather.WeatherItem
import com.juniori.puzzle.data.weather.WeatherRepository
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.util.toAddressString
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

    private val _currentAddress = MutableLiveData("")
    val currentAddress: LiveData<String> = _currentAddress

    private val _weatherList = MutableLiveData<List<WeatherItem>>(emptyList())
    val weatherList: LiveData<List<WeatherItem>> = _weatherList

    private val _weatherMainList = MutableLiveData<WeatherItem>().apply {
        WeatherItem("", "", 0, 0, 0, 0, "", "")
    }
    val weatherMainList: LiveData<WeatherItem> = _weatherMainList

    fun setDisplayName() {
        val userInfo = getUserInfoUseCase()
        if (userInfo is Resource.Success) {
            _displayName.value = "${userInfo.result.nickname}님"
        } else {
            _displayName.value = "누구세요?"
        }
    }

    fun setWelcomeText(text: String) {
        _welcomeText.value = text
    }

    fun setWeatherInfoText(text: String) {
        _weatherInfoText.value = text
    }

    fun setCurrentAddress(address: List<Address>) {
        _currentAddress.value = address[0].toAddressString()
    }

    fun getWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val result = repository.getWeather(latitude, longitude)
            if (result.isSuccess) {
                val list = result.getOrDefault(emptyList())
                _weatherMainList.value = list[0]
                _weatherList.value = list.subList(1, list.size)
                _weatherInfoText.value = ""
            } else {
                _weatherInfoText.value = result.exceptionOrNull()?.message
            }
        }
    }

}
