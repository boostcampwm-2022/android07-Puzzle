package com.juniori.puzzle.ui.home

import android.location.Address
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.data.weather.WeatherItem
import com.juniori.puzzle.util.toAddressString
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.weather.WeatherRepository
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    private val _uiState = MutableLiveData<Resource<List<WeatherItem>>>(Resource.Loading)
    val uiState = _uiState

    private val _welcomeText = MutableLiveData("")
    val welcomeText: LiveData<String> = _welcomeText

    private val _weatherInfoText = MutableLiveData("")
    val weatherInfoText: LiveData<String> = _weatherInfoText

    private val _displayName = MutableLiveData("")
    val displayName: LiveData<String> = _displayName

    private val _currentAddress = MutableLiveData("")
    val currentAddress: LiveData<String> = _currentAddress

    private val _weatherList = MutableLiveData<List<WeatherItem>>(emptyList())
    val weatherList: LiveData<List<WeatherItem>> = _weatherList

    private val _weatherMainList =
        MutableLiveData(WeatherItem(Date(), 0, 0, 0, 0, "", ""))
    val weatherMainList: LiveData<WeatherItem> = _weatherMainList

    fun setUiState(state: Resource<List<WeatherItem>>) {
        _uiState.value = state
    }

    fun setDisplayName() {
        val userInfo = getUserInfoUseCase()
        if (userInfo is Resource.Success) {
            _displayName.value = userInfo.result.nickname
        } else {
            _displayName.value = ""
        }
    }

    fun setWelcomeText(text: String) {
        _welcomeText.value = text
    }

    fun setWeatherInfoText(text: String) {
        _weatherInfoText.value = text
        _uiState.value = Resource.Failure(Exception())
    }

    fun setCurrentAddress(address: List<Address>) {
        _currentAddress.value = address[0].toAddressString()
    }

    fun getWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            delay(1000)
            val result = repository.getWeather(latitude, longitude)
            if (result.isSuccess) {
                val list = result.getOrDefault(emptyList())
                _weatherMainList.value = list[0]
                _weatherList.value = list.subList(1, list.size)
                _weatherInfoText.value = ""
                uiState.value = Resource.Success(list)
            } else {
                _weatherInfoText.value = result.exceptionOrNull()?.message
                uiState.value = Resource.Failure(Exception(""))
            }
        }
    }

}
