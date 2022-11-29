package com.juniori.puzzle.domain.repository

import android.location.Address
import androidx.core.location.LocationListenerCompat
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.WeatherEntity

interface LocationRepository {
    fun registerLocationListener(listener: LocationListenerCompat)
    fun unregisterLocationListener()
    fun getLocationInfo(): Pair<Double, Double>
    fun getAddressInfo(lat: Double, long: Double): List<Address>
    suspend fun getWeatherInfo(lat: Double, long: Double): Resource<List<WeatherEntity>>
}