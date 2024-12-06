package com.example.digitalshelf.ui.theme.screens.signup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.digitalshelf.navigation.ROUTE_LOGIN
import com.example.digitalshelf.navigation.ROUTE_SIGNUP
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth



@Composable
fun SignUpScreen(navController: NavHostController, onRegisterSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) } // State for loading

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Register",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // First Name Input
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Email Input
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Password Input
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Input
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Error Message Display
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Show Circular Progress Indicator if loading is true
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp)) // Show progress bar
            } else {
                // Register Button
                Button(
                    onClick = {
                        loading = true // Set loading to true when registration starts
                        registerUser(
                            firstName = firstName,
                            email = email,
                            password = password,
                            confirmPassword = confirmPassword,
                            onRegisterSuccess = {
                                loading = false // Set loading to false on success
                                showSnackbar = true
                                navController.navigate(ROUTE_LOGIN) {
                                    popUpTo(ROUTE_SIGNUP) { inclusive = true }
                                }
                            },
                            onError = { message ->
                                loading = false // Set loading to false in case of error
                                errorMessage = message // Update the errorMessage variable with the received message
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Register")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigate to Login Button
            TextButton(onClick = { navController.navigate(ROUTE_LOGIN) }) {
                Text("Already have an account? Login")
            }
        }

        // Success Snackbar
        if (showSnackbar) {
            Snackbar(
                modifier = Modifier.align(Alignment.BottomCenter),
                action = {
                    TextButton(onClick = { showSnackbar = false }) {
                        Text("DISMISS", color = Color.White)
                    }
                }
            ) {
                Text("Sign Up Successful! Please log in.")
            }
        }
    }
}

fun registerUser(
    firstName: String,
    email: String,
    password: String,
    confirmPassword: String,
    onRegisterSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    // Input validation
    if (firstName.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
        onError("All fields are required.")
        return
    }

    if (password != confirmPassword) {
        onError("Passwords do not match.")
        return
    }

    if (password.length < 6) {
        onError("Password must be at least 6 characters long.")
        return
    }

    val auth = FirebaseAuth.getInstance()

    // Create user with Firebase Authentication
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Get the newly created user's UID
                val userId = auth.currentUser?.uid
                if (userId == null) {
                    onError("Unable to retrieve user ID.")
                    return@addOnCompleteListener
                }

                // Prepare user data for Firebase Realtime Database
                val database = Firebase.database.reference
                val user = mapOf(
                    "firstName" to firstName,
                    "email" to email,
                    "role" to "user" // Default role assignment
                )

                // Store user data in the database
                database.child("users").child(userId).setValue(user)
                    .addOnSuccessListener {
                        onRegisterSuccess() // Notify success
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure to save data in the database
                        onError("Failed to save user data: ${exception.message}")
                    }
            } else {
                // Handle Firebase Authentication errors
                val errorMessage = task.exception?.message ?: "Registration failed."
                onError(errorMessage)
            }
        }
        .addOnFailureListener { exception ->
            // General fallback error handler
            onError("Unexpected error: ${exception.message}")
        }
}
