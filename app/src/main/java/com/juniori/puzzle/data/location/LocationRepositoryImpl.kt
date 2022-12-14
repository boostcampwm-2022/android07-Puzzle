package com.juniori.puzzle.data.location

import android.location.Address
import androidx.core.location.LocationListenerCompat
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.weather.WeatherDataSource
import com.juniori.puzzle.domain.entity.WeatherEntity
import com.juniori.puzzle.domain.repository.LocationRepository
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationDataSource: LocationDataSource,
    private val weatherDataSource: WeatherDataSource,
) : LocationRepository {
    override fun registerLocationListener(listener: LocationListenerCompat): Boolean {
        return locationDataSource.registerLocationListener(listener)
    }

    override fun unregisterLocationListener() {
        locationDataSource.unregisterLocationListener()
    }

    override fun getAddressInfo(lat: Double, long: Double): List<Address> {
        return locationDataSource.getCurrentAddress(lat, long)
    }

    override suspend fun getWeatherInfo(lat: Double, long: Double): Resource<List<WeatherEntity>> {
        return weatherDataSource.getWeather(lat, long)
    }
}