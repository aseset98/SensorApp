package com.example.sensorapp


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object FirestoreHelper {
    private val firestore = FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(Dispatchers.IO)

    fun sendDataToFirestore(bufferedData: List<Triple<String, String, String>>) {
        scope.async {
            for ((accelerometerData, gyroscopeData, lightData) in bufferedData) {
                val data = hashMapOf(
                    "Accelometer" to accelerometerData,
                    "Gyroscope" to gyroscopeData,
                    "Light" to lightData
                )

                firestore.collection("sensor_data").add(data)
                    .addOnSuccessListener { documentReference ->
                        Log.d("Firestore", "DocumentSnapshot written with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error adding document", e)
                    }
            }
        }
    }
}