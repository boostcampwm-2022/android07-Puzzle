package com.juniori.puzzle.ui.home

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.WeatherEntity
import com.juniori.puzzle.domain.usecase.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.Date

class HomeViewModelTest {
    lateinit var homeViewModel: HomeViewModel
    lateinit var getAddressUseCase: GetAddressUseCase
    lateinit var getLocationUseCase: GetLocationInfoUseCase
    lateinit var registerLocationListenerUseCase: RegisterLocationListenerUseCase
    lateinit var unregisterLocationListenerUseCase: UnregisterLocationListenerUseCase
    lateinit var getWeatherUseCase: GetWeatherUseCase
    lateinit var getUserInfoUseCase: GetUserInfoUseCase

    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)

        getAddressUseCase = Mockito.mock(GetAddressUseCase::class.java)
        getLocationUseCase = Mockito.mock(GetLocationInfoUseCase::class.java)
        registerLocationListenerUseCase = Mockito.mock(RegisterLocationListenerUseCase::class.java)
        unregisterLocationListenerUseCase = Mockito.mock(UnregisterLocationListenerUseCase::class.java)
        getWeatherUseCase = Mockito.mock(GetWeatherUseCase::class.java)
        getUserInfoUseCase = Mockito.mock(GetUserInfoUseCase::class.java)

        homeViewModel = HomeViewModel(getAddressUseCase, getLocationUseCase, registerLocationListenerUseCase, unregisterLocationListenerUseCase, getWeatherUseCase, getUserInfoUseCase)
    }

    @Test
    fun negativeLocationTest(): Unit = runBlocking {
        Mockito.`when`(getLocationUseCase()).thenReturn(Pair(NEGATIVE_LOCATION, NEGATIVE_LOCATION))
        homeViewModel.getWeather().join()

        assertTrue(homeViewModel.uiState.value is Resource.Failure)
    }

    @Test
    fun emptyAddressTest(): Unit = runBlocking {
        val mockWeatherEntity = WeatherEntity(Date(), 10, 10, 10, 10, "", "")
        Mockito.`when`(getLocationUseCase()).thenReturn(Pair(NORMAL_LOCATION, NORMAL_LOCATION))
        Mockito.`when`(getAddressUseCase(NORMAL_LOCATION, NORMAL_LOCATION)).thenReturn(emptyList())
        val mockWeatherList = listOf(mockWeatherEntity, mockWeatherEntity, mockWeatherEntity, mockWeatherEntity)
        Mockito.`when`(getWeatherUseCase(NORMAL_LOCATION, NORMAL_LOCATION)).thenReturn(Resource.Success(mockWeatherList))

        assertEquals(Resource.Success(mockWeatherList), homeViewModel.uiState.value)
    }

    @Test
    fun emptyWeatherTest(): Unit = runBlocking {
        Mockito.`when`(getLocationUseCase()).thenReturn(Pair(NORMAL_LOCATION, NORMAL_LOCATION))
        Mockito.`when`(getWeatherUseCase(NORMAL_LOCATION, NORMAL_LOCATION)).thenReturn(Resource.Success(emptyList()))

        homeViewModel.getWeather().join()
        assertTrue(homeViewModel.uiState.value is Resource.Failure)
    }

    @Test
    fun failWeatherTest(): Unit = runBlocking {
        Mockito.`when`(getLocationUseCase()).thenReturn(Pair(NORMAL_LOCATION, NORMAL_LOCATION))
        Mockito.`when`(getWeatherUseCase(NORMAL_LOCATION, NORMAL_LOCATION)).thenReturn(Resource.Failure(Exception()))

        homeViewModel.getWeather().join()
        assertTrue(homeViewModel.uiState.value is Resource.Failure)
    }

    @Test
    fun shortWeatherListTest(): Unit = runBlocking {
        val mockWeatherEntity = WeatherEntity(Date(), 10, 10, 10, 10, "", "")
        val mockWeatherList = listOf(mockWeatherEntity)

        Mockito.`when`(getLocationUseCase()).thenReturn(Pair(NORMAL_LOCATION, NORMAL_LOCATION))
        Mockito.`when`(getWeatherUseCase(NORMAL_LOCATION, NORMAL_LOCATION)).thenReturn(Resource.Success(mockWeatherList))

        homeViewModel.getWeather().join()
        assertTrue(homeViewModel.uiState.value is Resource.Success)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    companion object {
        const val NEGATIVE_LOCATION = -10.0
        const val NORMAL_LOCATION = 10.0
    }
}