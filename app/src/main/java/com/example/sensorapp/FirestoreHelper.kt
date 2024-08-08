package com.example.sensorapp

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreHelper {
    private val firestore = FirebaseFirestore.getInstance()

    fun sendDataToFirestore(accelerometerData: String, gyroscopeData: String, lightData: String) {
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