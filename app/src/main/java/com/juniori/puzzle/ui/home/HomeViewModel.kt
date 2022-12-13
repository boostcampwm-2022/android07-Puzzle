package com.juniori.puzzle.ui.home

import androidx.core.location.LocationListenerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.R
import com.juniori.puzzle.util.toAddressString
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.location.LocationInfo
import com.juniori.puzzle.domain.entity.WeatherEntity
import com.juniori.puzzle.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAddressUseCase: GetAddressUseCase,
    private val registerLocationListenerUseCase: RegisterLocationListenerUseCase,
    private val unregisterLocationListenerUseCase: UnregisterLocationListenerUseCase,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<List<WeatherEntity>>>(Resource.Loading)
    val uiState: StateFlow<Resource<List<WeatherEntity>>> = _uiState

    private val _welcomeText = MutableStateFlow("")
    val welcomeText: StateFlow<String> = _welcomeText

    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName

    private val _currentAddress = MutableStateFlow("")
    val currentAddress: StateFlow<String> = _currentAddress

    private val _weatherList = MutableStateFlow<List<WeatherEntity>>(emptyList())
    val weatherList: StateFlow<List<WeatherEntity>> = _weatherList

    private val _weatherMainList =
        MutableStateFlow(WeatherEntity(Date(), 0, 0, 0, 0, "", ""))
    val weatherMainList: StateFlow<WeatherEntity> = _weatherMainList

    private val _weatherFailTextId = MutableLiveData(R.string.location_empty)
    val weatherFailTextId: LiveData<Int> = _weatherFailTextId

    private val _lastLocationInfo =
        MutableStateFlow(LocationInfo(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY))

    private var locationTimerJob: Job? = null

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

    private fun setWeatherFailTextId(id: Int) {
        _weatherFailTextId.value = id
    }

    fun setWeatherInfoText(text: String) {
        _uiState.value = Resource.Failure(Exception(text))
    }

    private fun setCurrentAddress(lat: Double, long: Double) {
        _currentAddress.value = getAddressUseCase(lat, long)[0].toAddressString()
    }

    fun registerListener(listener: LocationListenerCompat) {
        val result = registerLocationListenerUseCase(listener)
        if (result.not()) {
            setWeatherFailTextId(R.string.location_service_off)
        } else {
            locationTimerJob = CoroutineScope(Dispatchers.IO).launch {
                delay(3000)
                withContext(Dispatchers.Main) {
                    showWeather()
                }
            }
        }
    }

    fun unregisterListener() {
        unregisterLocationListenerUseCase()
    }

    fun cancelTimer() {
        locationTimerJob?.cancel()
    }

    fun getWeather(loc: LocationInfo) {
        _lastLocationInfo.value = loc
        viewModelScope.launch {
            when (val result = getWeatherUseCase(loc.lat, loc.lon)) {
                is Resource.Success<List<WeatherEntity>> -> {
                    val list = result.result
                    if (list.size >= 3) {
                        _weatherMainList.value = list[1]
                        _weatherList.value = list.subList(2, list.size)
                        setCurrentAddress(loc.lat, loc.lon)
                        _uiState.value = Resource.Success(list)
                    } else {
                        setWeatherFailTextId(R.string.network_fail)
                    }
                }
                is Resource.Failure -> {
                    setWeatherFailTextId(R.string.network_fail)
                }
                is Resource.Loading -> _uiState.value = Resource.Loading
            }
        }
    }

    fun showWeather() {
        if (_weatherList.value.size < 3) {
            setWeatherFailTextId(R.string.location_empty)
        } else {
            _uiState.value = Resource.Success(_weatherList.value)
        }
    }

}
