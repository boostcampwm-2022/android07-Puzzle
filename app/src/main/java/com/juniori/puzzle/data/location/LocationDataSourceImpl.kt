package com.juniori.puzzle.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.location.LocationProvider
import androidx.core.location.LocationListenerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocationDataSourceImpl @Inject constructor(
    @ApplicationContext context: Context
) : LocationDataSource {
    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val geoCoder = Geocoder(context)
    private var locationListener: LocationListenerCompat? = null

    @SuppressLint("MissingPermission")
    override fun registerLocationListener(listener: LocationListenerCompat): Boolean {
        return if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (locationListener == null) {
                locationListener = listener
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    LOCATION_MIN_TIME_INTERVAL,
                    LOCATION_MIN_DISTANCE_INTERVAL,
                    locationListener!!
                )
            }
            true
        } else {
            false
        }
    }

    @SuppressLint("MissingPermission")
    override fun unregisterLocationListener() {
        locationListener?.let {
            locationManager.removeUpdates(it)
            locationListener = null
        }
    }

    override fun getCurrentAddress(lat: Double, long: Double): List<Address> {
        return try {
            geoCoder.getFromLocation(lat, long, ADDRESS_MAX_RESULT)
        } catch (e: Exception) {
            emptyList()
        }
    }

    companion object {
        private const val LOCATION_MIN_TIME_INTERVAL = 3000L
        private const val LOCATION_MIN_DISTANCE_INTERVAL = 30f
        private const val ADDRESS_MAX_RESULT = 1
    }

}