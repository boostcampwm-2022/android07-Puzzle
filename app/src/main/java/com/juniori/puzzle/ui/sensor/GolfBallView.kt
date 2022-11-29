package com.juniori.puzzle.ui.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.AttributeSet
import android.view.Surface
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.max

class GolfBallView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs),
    SensorEventListener {

    private val sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val windowManager: WindowManager by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
    private val maxSize: Pair<Int, Int> by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Pair(
                windowManager.currentWindowMetrics.bounds.width(),
                windowManager.currentWindowMetrics.bounds.height()
            )
        } else {
            Pair(windowManager.defaultDisplay.width, windowManager.defaultDisplay.height)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor == sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)) {
            event?.let {
                var sensorX = x
                var sensorY = y
                var sensorZ = z
                when (windowManager.defaultDisplay.rotation) {
                    Surface.ROTATION_0 -> {
                        sensorX = event.values[0]
                        sensorY = event.values[1]
                        sensorZ = event.values[2]
                    }
                    Surface.ROTATION_90 -> {
                        sensorX = -event.values[1]
                        sensorY = event.values[0]
                        sensorZ = event.values[2]
                    }
                    Surface.ROTATION_180 -> {
                        sensorX = -event.values[0]
                        sensorY = -event.values[1]
                        sensorZ = event.values[2]
                    }
                    Surface.ROTATION_270 -> {
                        sensorX = event.values[1]
                        sensorY = -event.values[0]
                        sensorZ = event.values[2]
                    }
                }

                x -= sensorX * sensorZ
                y += sensorY * sensorZ

                var isDrawXNeeded = true
                var isDrawYNeeded = true
                if (x < 0) {
                    x = 0f
                } else if (x + width > maxSize.first) {
                    x = (maxSize.first - width).toFloat()
                } else {
                    isDrawXNeeded = false
                }

                if (y < 0) {
                    y = 0f
                } else if (y + height > maxSize.second) {
                    y = (maxSize.second - height).toFloat()
                } else {
                    isDrawYNeeded = false
                }

                if (isDrawXNeeded || isDrawYNeeded) requestLayout()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}