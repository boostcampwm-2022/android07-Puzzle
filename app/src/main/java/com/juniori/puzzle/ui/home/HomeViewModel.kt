package com.juniori.puzzle.ui.home

import androidx.core.location.LocationListenerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.R
import com.juniori.puzzle.util.toAddressString
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.WeatherEntity
import com.juniori.puzzle.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAddressUseCase: GetAddressUseCase,
    private val getLocationUseCase: GetLocationInfoUseCase,
    private val registerLocationListenerUseCase: RegisterLocationListenerUseCase,
    private val unregisterLocationListenerUseCase: UnregisterLocationListenerUseCase,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    private val _uiState = MutableLiveData<Resource<List<WeatherEntity>>>(Resource.Loading)
    val uiState = _uiState

    private val _welcomeText = MutableLiveData("")
    val welcomeText: LiveData<String> = _welcomeText

    private val _displayName = MutableLiveData("")
    val displayName: LiveData<String> = _displayName

    private val _currentAddress = MutableLiveData("")
    val currentAddress: LiveData<String> = _currentAddress

    private val _weatherList = MutableLiveData<List<WeatherEntity>>(emptyList())
    val weatherList: LiveData<List<WeatherEntity>> = _weatherList

    private val _weatherMainList =
        MutableLiveData(WeatherEntity(Date(), 0, 0, 0, 0, "", ""))
    val weatherMainList: LiveData<WeatherEntity> = _weatherMainList

    fun setUiState(state: Resource<List<WeatherEntity>>) {
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
        _uiState.value = Resource.Failure(Exception(text))
    }

    private fun setCurrentAddress(lat: Double, long: Double) {
        _currentAddress.value = getAddressUseCase(lat, long)[0].toAddressString()
    }

    fun registerListener(listener: LocationListenerCompat) {
        registerLocationListenerUseCase(listener)
    }

    fun unregisterListener() {
        unregisterLocationListenerUseCase()
    }

    fun getWeather() {
        viewModelScope.launch {
            val location = getLocationUseCase()
            if (location.first <= 0f.toDouble() && location.second <= 0f.toDouble()) {
                setWeatherInfoText("네트워크 및 위치 서비스를 연결해주세요")
                return@launch
            }

            when (val result = getWeatherUseCase(location.first, location.second)) {
                is Resource.Success<List<WeatherEntity>> -> {
                    val list = result.result
                    if (list.size >= 3) {
                        _weatherMainList.value = list[1]
                        _weatherList.value = list.subList(2, list.size)
                        setCurrentAddress(location.first, location.second)
                        _uiState.value = Resource.Success(list)
                    } else {
                        _uiState.value = Resource.Failure(Exception("네트워크 통신에 실패하였습니다"))
                    }
                }
                is Resource.Failure -> {
                    uiState.value = Resource.Failure(Exception("네트워크 통신에 실패하였습니다"))
                }
                is Resource.Loading -> uiState.value = Resource.Loading
            }
        }
    }

}
