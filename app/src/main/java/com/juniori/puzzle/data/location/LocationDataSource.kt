package com.juniori.puzzle.data.location

import android.location.Address
import androidx.core.location.LocationListenerCompat

interface LocationDataSource {
    fun registerLocationListener(listener: LocationListenerCompat)
    fun unregisterLocationListener()
    fun getLatestLocation(): Pair<Double, Double>
    fun getCurrentAddress(lat: Double, long: Double): List<Address>
}