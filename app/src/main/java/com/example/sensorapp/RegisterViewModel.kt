package com.example.sensorapp

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class RegisterViewModel : ViewModel() {
    private val firebaseAuth = Firebase.auth

    fun registerUser(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration successful, navigate to the next screen
                    Log.d("RegisterViewModel", "createUserWithEmail:success")
                    val user = firebaseAuth.currentUser
                } else {
                    // Registration failed, display an error message
                    Log.w("RegisterViewModel", "createUserWithEmail:failure", task.exception)
                    val exception = task.exception
                }
            }
    }
}