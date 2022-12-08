package com.juniori.puzzle.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import androidx.core.location.LocationListenerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocationDataSourceImpl @Inject constructor(
    @ApplicationContext context: Context
) : LocationDataSource {
    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val geoCoder = Geocoder(context)
    private lateinit var locationListener: LocationListenerCompat

    @SuppressLint("MissingPermission")
    override fun registerLocationListener(listener: LocationListenerCompat) {
        locationListener = listener
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            LOCATION_MIN_TIME_INTERVAL,
            LOCATION_MIN_DISTANCE_INTERVAL,
            locationListener
        )
    }

    @SuppressLint("MissingPermission")
    override fun unregisterLocationListener() {
        locationManager.removeUpdates(locationListener)
    }

    @SuppressLint("MissingPermission")
    override fun getLatestLocation(): Pair<Double, Double> {
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            val location = locationManager.getLastKnownLocation(
                LocationManager.NETWORK_PROVIDER
            )
            val latitude = location?.latitude ?: DEFAULT_LATITUDE
            val longitude = location?.longitude ?: DEFAULT_LONGITUDE
            return Pair(latitude, longitude)
        }
        return Pair(100f.toDouble(), 200f.toDouble())
    }

    override fun getCurrentAddress(lat: Double, long: Double): List<Address> {
        return geoCoder.getFromLocation(lat, long, ADDRESS_MAX_RESULT)
    }

    companion object {
        private const val DEFAULT_LATITUDE = 37.0
        private const val DEFAULT_LONGITUDE = 127.0
        private const val LOCATION_MIN_TIME_INTERVAL = 3000L
        private const val LOCATION_MIN_DISTANCE_INTERVAL = 30f
        private const val ADDRESS_MAX_RESULT = 1
    }

}