package com.example.sensorapp


import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.media.MediaPlayer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {

            val navController = rememberNavController()
            NavHost(navController, startDestination = "main") {
                composable("main") {
                    MainScreen(navController)
                }
                composable("register") {
                    RegisterScreen(navController)
                }
                composable("login") {
                    LoginScreen(navController)
                }
                composable("measure_select") {
                    MeasurementSelectScreen(navController)
                }
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavController) {
    Surface() {
        BackgroundPicture()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Space between text and buttons
            Spacer(modifier = Modifier.height(54.dp)) // Add a spacer with 16dp height
            //Welcome text
            Text(
                text = "Welcome to SensorApp!",
                style = MaterialTheme.typography.headlineLarge
            )

            //Space between text and buttons
            Spacer(modifier = Modifier.height(16.dp)) // Add a spacer with 16dp height
            Text(
                text = "Please log in or register:",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp)) // Add a spacer with 16dp height
            //Register button
            Button(onClick = { navController.navigate("register") }) {
                Text("Register")
            }
            //Login button
            Button(onClick = { navController.navigate("login") }) {
                Text("Log in")
            }

        }
    }
}

@Composable
fun RegisterScreen(navController: NavController) {
    Surface() {
        BackgroundPicture()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val email = remember { mutableStateOf("") }
            val password = remember { mutableStateOf("") }
            val viewModel = viewModel<RegisterViewModel>()

            Spacer(modifier = Modifier.height(128.dp)) // Add a spacer with 16dp height
            //Welcome text
            Text(
                text = "Registration form",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(54.dp)) // Add a spacer with 16dp height
            TextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                maxLines = 1
            )

            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") },
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(24.dp)) // Add a spacer with 16dp height
            Button(onClick = {
                // Submit the registration form
                viewModel.registerUser(email.value, password.value)
                navController.navigateUp()

            }) {
                Text("Register")
            }

            Button(onClick = {
                // Go back to welcome page
                navController.navigateUp()

            }) {
                Text("Return to welcome page")
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    Surface() {
        BackgroundPicture()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val email = remember { mutableStateOf("") }
            val password = remember { mutableStateOf("") }
            val viewModel = viewModel<LoginViewModel>()

            Spacer(modifier = Modifier.height(128.dp)) // Add a spacer with 16dp height
            //Welcome text
            Text(
                text = "Login form",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(54.dp)) // Add a spacer with 16dp height
            TextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                maxLines = 1
            )

            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") },
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(24.dp)) // Add a spacer with 16dp height
            Button(onClick = {
                // Submit the login form
                viewModel.loginUser(email.value, password.value) { isSuccess ->
                    if (isSuccess) {
                        navController.navigate("measure_select")
                    } else {
                        // Show an error message
                    }
                }
            }) {
                Text("Login")
            }

            Button(onClick = {
                // Go back to welcome page
                navController.navigateUp()
            }) {
                Text("Return to welcome page")
            }
        }
    }
}

@Composable
fun BackgroundPicture( modifier: Modifier = Modifier){
    val image = painterResource(id = R.drawable.android_bg)

    Box(modifier) {
        Image(
            painter = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.5F,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        )

    }
}


//Screen for measurement
@SuppressLint("ServiceCast")
@Composable
fun MeasurementSelectScreen(navController: NavController) {

        //Sensor calculation related
        val context = LocalContext.current
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        val accelerometerData = remember { mutableStateOf("") }
        val gyroscopeData = remember { mutableStateOf("") }
        val lightData = remember { mutableStateOf("") }

        //Update displayed values when sensor detects a change
        sensorManager.registerListener(object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> accelerometerData.value = "Accelerometer: x=${event.values[0]}, y=${event.values[1]}, z=${event.values[2]}"
                    Sensor.TYPE_GYROSCOPE -> gyroscopeData.value = "Gyroscope: x=${event.values[0]}, y=${event.values[1]}, z=${event.values[2]}"
                    Sensor.TYPE_LIGHT -> lightData.value = "Light: ${event.values[0]} lux"
                }
                // Send data to Firestore
                FirestoreHelper.sendDataToFirestore(accelerometerData.value, gyroscopeData.value, lightData.value)


            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> accelerometerData.value = "Accelerometer: x=${event.values[0]}, y=${event.values[1]}, z=${event.values[2]}"
                    Sensor.TYPE_GYROSCOPE -> gyroscopeData.value = "Gyroscope: x=${event.values[0]}, y=${event.values[1]}, z=${event.values[2]}"
                    Sensor.TYPE_LIGHT -> lightData.value = "Light: ${event.values[0]} lux"
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> accelerometerData.value = "Accelerometer: x=${event.values[0]}, y=${event.values[1]}, z=${event.values[2]}"
                    Sensor.TYPE_GYROSCOPE -> gyroscopeData.value = "Gyroscope: x=${event.values[0]}, y=${event.values[1]}, z=${event.values[2]}"
                    Sensor.TYPE_LIGHT -> lightData.value = "Light: ${event.values[0]} lux"
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }, light, SensorManager.SENSOR_DELAY_NORMAL)


        //UI Part
        BackgroundPicture()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)

        ) {
            Text(
                text = accelerometerData.value,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp)) // Add a spacer with 16dp height

            Text(
                text = gyroscopeData.value,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp)) // Add a spacer with 16dp height

            Text(
                text = lightData.value,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp)) // Add a spacer with 16dp height
        }
    }

