package com.example.sensorapp


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Job
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign


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
                composable("periodic_measure") {
                    PeriodicMeasurementSending(navController)
                }
                composable("user_defined_measure") {
                    UserDefinedIntervalMeasurementSending(navController)
                }
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavController) {
    // Load logo image from drawable resource
    val logoModifier = Modifier
        .fillMaxWidth()
        .height(200.dp) // Adjust height as needed
        .background(Color.White)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Set background color to white
    ) {



        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .align(Alignment.BottomCenter), // Align content to bottom to leave space for logo
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.sensorapplogo), // Load the image resource
                contentDescription = null,
                modifier = logoModifier,
                contentScale = ContentScale.Crop // Crop or fit as needed
            )
            Spacer(modifier = Modifier.height(24.dp)) // Space between text and buttons

            // Description text with styling
            Text(
                text = "Please log in or register:",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.Black
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp)) // Space between text and buttons

            // Register button with styling
            RoundedButton(
                onClick = { navController.navigate("register") },
                text = "Register",
                //backgroundColor = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp)) // Space between buttons

            // Login button with styling
            RoundedButton(
                onClick = { navController.navigate("login") },
                text = "Log in",
                //backgroundColor = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun RegisterScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White // Background color for the Surface
    ) {
        BackgroundPicture() // Assumes this function sets a full-screen background image

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val email = remember { mutableStateOf("") }
            val password = remember { mutableStateOf("") }
            val confirmPassword = remember { mutableStateOf("") }
            val passwordVisibility = remember { mutableStateOf(false) }
            val confirmPasswordVisibility = remember { mutableStateOf(false) }
            val passwordsMatch = remember { mutableStateOf(true) }
            val viewModel = viewModel<RegisterViewModel>()

            // Observe changes to passwords to check if they match
            LaunchedEffect(password.value, confirmPassword.value) {
                snapshotFlow { password.value to confirmPassword.value }
                    .collect { (pw, cpw) ->
                        passwordsMatch.value = pw == cpw
                    }
            }

            Spacer(modifier = Modifier.height(32.dp)) // Space above content

            // Welcome text with styling
            Text(
                text = "Registration Form",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(54.dp)) // Space between header and fields

            // Email TextField with styling
            TextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(4.dp)) // Space between fields

            // Password TextField with visibility toggle
            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") },
                maxLines = 1,
                visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisibility.value) Icons.Filled.Info else Icons.Filled.Info

                    IconButton(onClick = {
                        passwordVisibility.value = !passwordVisibility.value
                    }) {
                        Icon(imageVector = image, contentDescription = "Toggle password visibility")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp)),

            )

            Spacer(modifier = Modifier.height(4.dp)) // Space between fields

            // Confirm Password TextField with visibility toggle
            TextField(
                value = confirmPassword.value,
                onValueChange = { confirmPassword.value = it },
                label = { Text("Confirm Password") },
                maxLines = 1,
                visualTransformation = if (confirmPasswordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (confirmPasswordVisibility.value) Icons.Filled.Info else Icons.Filled.Info

                    IconButton(onClick = {
                        confirmPasswordVisibility.value = !confirmPasswordVisibility.value
                    }) {
                        Icon(imageVector = image, contentDescription = "Toggle confirm password visibility")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            )

            // Show a message if passwords do not match
            if (!passwordsMatch.value) {
                Text(
                    text = "Passwords do not match",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp)) // Space between fields and buttons

            // Register button with styling, enabled only if passwords match
            Button(
                onClick = {
                    if (passwordsMatch.value) {
                        viewModel.registerUser(email.value, password.value)
                        navController.navigate("measure_select")
                    }
                },
                enabled = passwordsMatch.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(8.dp),

            ) {
                Text("Register")
            }

            Spacer(modifier = Modifier.height(16.dp)) // Space between buttons

            // Return to welcome page button with styling
            Button(
                onClick = {
                    navController.navigateUp()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(8.dp),

            ) {
                Text("Return to Welcome Page")
            }
        }
    }
}



//Login screen
@Composable
fun LoginScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White // Background color for the Surface
    ) {
        BackgroundPicture()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val email = remember { mutableStateOf("") }
            val password = remember { mutableStateOf("") }
            val passwordVisibility = remember { mutableStateOf(false) }
            val viewModel = viewModel<LoginViewModel>()

            Spacer(modifier = Modifier.height(32.dp)) // Add space above

            // Welcome text with styling
            Text(
                text = "Login Form",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(54.dp)) // Space between header and fields

            // Email TextField with styling
            TextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp)),

            )

            Spacer(modifier = Modifier.height(4.dp)) // Space between fields

            // Password TextField with visibility toggle
            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") },
                maxLines = 1,
                visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisibility.value) Icons.Filled.Info else Icons.Filled.Info

                    IconButton(onClick = {
                        passwordVisibility.value = !passwordVisibility.value
                    }) {
                        Icon(imageVector = image, contentDescription = "Toggle password visibility")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp)),
            )

            Spacer(modifier = Modifier.height(24.dp)) // Space between fields and buttons

            // Login button with styling
            Button(
                onClick = {
                    // Submit the login form
                    viewModel.loginUser(email.value, password.value) { isSuccess ->
                        if (isSuccess) {
                            navController.navigate("measure_select")
                        } else {
                            // Show an error message
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Login", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp)) // Space between buttons

            // Return to welcome page button with styling
            Button(
                onClick = {
                    navController.navigateUp()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(8.dp),
              ) {
                Text("Return to welcome page", color = Color.White)
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

@Composable
fun MeasurementSelectScreen(navController: NavController) {
    val context = LocalContext.current
    val sensorDataManager = remember { SensorDataManager(context) }

    LaunchedEffect(Unit) {
        sensorDataManager.start()
    }

    DisposableEffect(Unit) {
        onDispose {
            sensorDataManager.stop()
        }
    }

    // Collect StateFlow values as State objects
    val accelerometerData = sensorDataManager.accelerometerData.collectAsState(initial = "")
    val gyroscopeData = sensorDataManager.gyroscopeData.collectAsState(initial = "")
    val lightData = sensorDataManager.lightData.collectAsState(initial = "")

    // UI Part
    BackgroundPicture()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val timestamp = System.currentTimeMillis() // get current time in milliseconds
        val formattedTimestamp =
            SimpleDateFormat("dd-MM-yyyy, HH:mm:ss", Locale.getDefault()).format(timestamp)

        // Display the timestamp with styling
        Text(
            text = "Date and time of last change: $formattedTimestamp",
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Display sensor data with styling
        SensorDataCard(title = "Accelerometer Data", data = accelerometerData.value)
        Spacer(modifier = Modifier.height(24.dp))
        SensorDataCard(title = "Gyroscope Data", data = gyroscopeData.value)
        Spacer(modifier = Modifier.height(24.dp))
        SensorDataCard(title = "Light Data", data = lightData.value)

        Spacer(modifier = Modifier.height(64.dp))

        // Measurement type selection text with styling
        Text(
            text = "Please select desired measurement type:",
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Buttons with styling
        RoundedButton(onClick = {
            navController.navigate("periodic_measure")
        }, text = "Periodic Measuring")

        Spacer(modifier = Modifier.height(16.dp))

        RoundedButton(onClick = {
            navController.navigate("user_defined_measure")
        }, text = "User Defined Interval Measuring")
    }
}

@Composable
fun SensorDataCard(title: String, data: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
                //.background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = data,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RoundedButton(onClick: () -> Unit, text: String) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp)
    ) {
        Text(text)
    }
}


@Composable
fun PeriodicMeasurementSending(navController: NavController) {
    BackgroundPicture()

    // Firestore related
    val firestore = Firebase.firestore
    val context = LocalContext.current
    val sensorDataManager = remember { SensorDataManager(context) }
    val firebaseDataSender = FirebaseDataSender(firestore, sensorDataManager)
    var job: Job? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        sensorDataManager.start()
    }

    DisposableEffect(Unit) {
        onDispose {
            sensorDataManager.stop()
            job?.cancel() // Cancel the job when the composable is disposed
        }
    }

    val isSendingData = remember { mutableStateOf(false) }

    // Collect StateFlow values as State objects
    val accelerometerData = sensorDataManager.accelerometerData.collectAsState(initial = "")
    val gyroscopeData = sensorDataManager.gyroscopeData.collectAsState(initial = "")
    val lightData = sensorDataManager.lightData.collectAsState(initial = "")

    // UI Part
    BackgroundPicture()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val timestamp = System.currentTimeMillis() // get current time in milliseconds
        val formattedTimestamp =
            SimpleDateFormat("dd-MM-yyyy, HH:mm:ss", Locale.getDefault()).format(timestamp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                .padding(16.dp)
        ) {
            Text(
                text = "Periodic measurement sending",
                style = MaterialTheme.typography.headlineLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        BoxWithBackground {
            Text(
                text = accelerometerData.value,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        BoxWithBackground {
            Text(
                text = gyroscopeData.value,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        BoxWithBackground {
            Text(
                text = lightData.value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        BoxWithBackground {
            if(isSendingData.value == true){
                Text(
                    text = "Status: sending data...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }else{
                Text(
                    text = "Status: not sending data",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(onClick = {
            if (!isSendingData.value) {
                job = firebaseDataSender.startSendingData()
                isSendingData.value = true
            }
        }) {
            Text("Start sending data")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (isSendingData.value) {
                job?.cancel()
                firebaseDataSender.stopSendingData()
                isSendingData.value = false
            }
        }) {
            Text("Stop sending data")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigateUp()
        }) {
            Text("Go back to measurement select screen")
        }
    }
}

@Composable
fun BoxWithBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun UserDefinedIntervalMeasurementSending(navController: NavController){
    BackgroundPicture()
    Button(onClick = {
        navController.navigateUp()
    }) {
        Text("Go back to measurement select screen")
    }
}