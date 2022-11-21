package com.juniori.puzzle.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.juniori.puzzle.BuildConfig
import com.juniori.puzzle.data.weather.WeatherRepository
import com.juniori.puzzle.data.weather.WeatherRepositoryImpl
import com.juniori.puzzle.network.WeatherService
import com.juniori.puzzle.util.WEATHER_BASE_URL
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherModule {

    private val gson = GsonBuilder().setLenient().create()

    private val weatherService = Retrofit.Builder()
        .baseUrl(WEATHER_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Singleton
    @Provides
    fun providesWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository = impl

    @Singleton
    @Provides
    fun providesWeatherService(): WeatherService {
        return weatherService.create(WeatherService::class.java)
    }
}