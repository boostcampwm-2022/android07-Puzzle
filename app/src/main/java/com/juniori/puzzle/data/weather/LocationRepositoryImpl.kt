package com.juniori.puzzle.data.weather

import com.juniori.puzzle.domain.repository.LocationRepository
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationDataSource : LocationDataSource,
    private val weatherDataSource : WeatherDataSource,
): LocationRepository {
    override fun getLocationInfo() {
        TODO("Not yet implemented")
    }

    override fun getWeatherInfo() {
        TODO("Not yet implemented")
    }
}