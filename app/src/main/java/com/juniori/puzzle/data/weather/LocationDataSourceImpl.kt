package com.juniori.puzzle.data.weather

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.core.location.LocationListenerCompat
import com.juniori.puzzle.R
import dagger.hilt.android.qualifiers.ActivityContext
import okhttp3.Address
import javax.inject.Inject

class LocationDataSourceImpl @Inject constructor(
    @ActivityContext context: Context
):LocationDataSource {
    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val locationListener = object : LocationListenerCompat {
        override fun onLocationChanged(loc: Location) {
            getWeatherInfo(loc.latitude, loc.longitude)
        }

        override fun onProviderDisabled(provider: String) {
            super.onProviderDisabled(provider)
            homeViewModel.setWeatherInfoText(getString(R.string.location_service_off))
        }

        override fun onProviderEnabled(provider: String) {
            super.onProviderEnabled(provider)
            getWeatherByLocation()
        }
    }

    @SuppressLint("MissingPermission")
    override fun getLatestLocation(): Pair<Double, Double> {
        val location = locationManager.getLastKnownLocation(
            LocationManager.NETWORK_PROVIDER
        )
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
        LOCATION_MIN_TIME_INTERVAL,
        LOCATION_MIN_DISTANCE_INTERVAL,
        locationListener
        )

        val latitude = location?.latitude ?: DEFAULT_LATITUDE
        val longitude = location?.longitude ?: DEFAULT_LONGITUDE
        return Pair(latitude, longitude)
    }

    override fun getCurrentAddress(lat: Double, long: Double): List<Address> {
        TODO("Not yet implemented")
    }

    companion object {
        private const val DEFAULT_LATITUDE = 37.0
        private const val DEFAULT_LONGITUDE = 127.0
        private const val LOCATION_MIN_TIME_INTERVAL = 3000L
        private const val LOCATION_MIN_DISTANCE_INTERVAL = 30f
        private const val ADDRESS_MAX_RESULT = 1
    }

}