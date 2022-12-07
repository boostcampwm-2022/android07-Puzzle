package com.juniori.puzzle.ui.home

import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.auth.AuthRepositoryMockImpl
import com.juniori.puzzle.data.weather.WeatherRepositoryMockImpl
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.domain.usecase.GetWeatherUseCase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class HomeViewModelTest {
    lateinit var homeVideModel: HomeViewModel

    @Before
    fun setup() {
        val userInfoEntity = UserInfoEntity("aaa", "K052", "url")

        val weatherRepository = WeatherRepositoryMockImpl(Resource.Failure(Exception()))
        val getWeatherUseCase = GetWeatherUseCase(weatherRepository)
        val authRepository = AuthRepositoryMockImpl(Resource.Success(userInfoEntity))
        val getUserInfoUseCase = GetUserInfoUseCase(authRepository)

        homeVideModel = HomeViewModel(getWeatherUseCase, getUserInfoUseCase)
    }

    @Test
    fun testDisplayName() {
        homeVideModel.setDisplayName()

        assertEquals(homeVideModel.displayName.value, "K052")
    }
}