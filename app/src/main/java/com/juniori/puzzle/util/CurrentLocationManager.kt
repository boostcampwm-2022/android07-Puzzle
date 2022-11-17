package com.juniori.puzzle.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class CurrentLocationManager @Inject constructor(
    @ActivityContext private val context: Context
) {
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun getCurrentLocation() {
        var location: Location? = null


    }
}