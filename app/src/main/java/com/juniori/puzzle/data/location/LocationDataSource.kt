package com.juniori.puzzle.data.location

import android.location.Address
import androidx.core.location.LocationListenerCompat

interface LocationDataSource {
    fun registerLocationListener(listener: LocationListenerCompat): Boolean
    fun unregisterLocationListener()
    fun getCurrentAddress(lat: Double, long: Double): List<Address>
}