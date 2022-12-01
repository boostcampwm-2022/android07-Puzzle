package com.juniori.puzzle.ui.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.juniori.puzzle.R
import com.juniori.puzzle.databinding.ActivitySensorBinding

class SensorActivity : AppCompatActivity() {
    private val sensorManager: SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private lateinit var sensor: Sensor
    private lateinit var binding: ActivitySensorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorBinding.inflate(layoutInflater)
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(binding.sensorBall, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        sensorManager.unregisterListener(binding.sensorBall)
        super.onPause()
    }
}