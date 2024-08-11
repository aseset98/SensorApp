package com.example.sensorapp

// SensorDataManager.kt
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


class SensorDataManager(private val context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    private val _accelerometerData = MutableStateFlow("")
    private val _gyroscopeData = MutableStateFlow("")
    private val _lightData = MutableStateFlow("")

    val accelerometerData: StateFlow<String> = _accelerometerData
    val gyroscopeData: StateFlow<String> = _gyroscopeData
    val lightData: StateFlow<String> = _lightData

    private val scope = CoroutineScope(Dispatchers.IO)

    fun start() {
        scope.launch {
            sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            sensorManager.registerListener(gyroscopeListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
            sensorManager.registerListener(lightListener, light, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stop() {
        scope.coroutineContext.cancelChildren()  // Cancel all child coroutines
        sensorManager.unregisterListener(accelerometerListener)
        sensorManager.unregisterListener(gyroscopeListener)
        sensorManager.unregisterListener(lightListener)
    }

    private val accelerometerListener = object : SensorEventListener {
        var data = ""

        override fun onSensorChanged(event: SensorEvent) {
            data = "Accelerometer: x=${event.values[0]}, y=${event.values[1]}, z=${event.values[2]}"
            _accelerometerData.value = data
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    private val gyroscopeListener = object : SensorEventListener {
        var data = ""

        override fun onSensorChanged(event: SensorEvent) {
            data = "Gyroscope: x=${event.values[0]}, y=${event.values[1]}, z=${event.values[2]}"
            _gyroscopeData.value = data
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    private val lightListener = object : SensorEventListener {
        var data = ""

        override fun onSensorChanged(event: SensorEvent) {
            data = "Light: ${event.values[0]} lux"
            _lightData.value = data
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }
}