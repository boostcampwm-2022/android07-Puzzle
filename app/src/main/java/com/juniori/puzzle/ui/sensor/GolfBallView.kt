package com.juniori.puzzle.ui.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.AttributeSet
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.max

class GolfBallView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs),
    SensorEventListener {

    private val sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val windowManager: WindowManager by lazy{
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
    private var maxSize: Pair<Float, Float> = Pair(x + width, y + height)

    fun setMaxSize(width: Int, height: Int) {
        println("width height $width $height")
        maxSize = Pair(width.toFloat(), height.toFloat())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor == sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)) {
            println("event ${event?.values?.joinToString(", ")}}")
            event?.let {
                val eX = it.values[0]
                val eY = it.values[1]
                val eZ = it.values[2]

                println("x y $x $y $maxSize")
                if (x <= 0 || y <= 0 || x + width >= maxSize.first || y + height >= maxSize.second) return@let

                x += eX
                y += eY
                requestLayout()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}