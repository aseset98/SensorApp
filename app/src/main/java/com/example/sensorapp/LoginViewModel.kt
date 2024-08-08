package com.example.sensorapp

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class LoginViewModel : ViewModel() {
    private val firebaseAuth = Firebase.auth

    fun loginUser(email: String, password: String, callback: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login successful
                    Log.d("LoginViewModel", "signInWithEmail:success")
                    callback(true)
                } else {
                    // Login failed
                    Log.w("LoginViewModel", "signInWithEmail:failure", task.exception)
                    callback(false)
                }
            }
    }
}