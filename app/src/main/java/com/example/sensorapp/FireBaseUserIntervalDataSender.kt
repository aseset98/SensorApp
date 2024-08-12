package com.example.sensorapp

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import javax.inject.Inject


class FireBaseUserIntervalDataSender @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val sensorDataManager: SensorDataManager
) {
    private val accelerometerData = sensorDataManager.accelerometerData
    private val gyroscopeData = sensorDataManager.gyroscopeData
    private val lightData = sensorDataManager.lightData

    private val scope = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null

    fun startSendingData(userInterval: Int): Job {
        firestore.clearPersistence()

        job = scope.launch {
            while (isActive) {
                val accelerometerValue = accelerometerData.value
                val gyroscopeValue = gyroscopeData.value
                val lightValue = lightData.value

                val document = hashMapOf(
                    "accelerometer" to accelerometerValue,
                    "gyroscope" to gyroscopeValue,
                    "light" to lightValue,
                    "timestamp" to FieldValue.serverTimestamp()
                )

                firestore.collection("user_interval_sensor_data").add(document)
                delay(userInterval * 1000L)  // Convert interval from seconds to milliseconds
            }
        }
        return job!!
    }

    fun stopSendingData() {
        Log.d("FirebaseDataSender", "Stopping data sender")
        job?.cancel()  // Cancel the job
        job = null
        firestore.clearPersistence()
    }
}