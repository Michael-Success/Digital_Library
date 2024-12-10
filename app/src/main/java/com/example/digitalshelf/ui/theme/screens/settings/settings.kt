package com.example.digitalshelf.ui.theme.screens.settings


import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.digitalshelf.repository.AuthRepository



@Composable
fun SettingsScreen(navController: NavController) {
    val authRepository = AuthRepository()
    val user = authRepository.getCurrentUser() // Fetch the current user details from the database

    var name by rememberSaveable { mutableStateOf(user.name ?: "") }
    var email by rememberSaveable { mutableStateOf(user.email ?: "") }
    var currentPassword by rememberSaveable { mutableStateOf(user.password ?: "") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var feedback by rememberSaveable { mutableStateOf("") }
    var userRole by rememberSaveable { mutableStateOf("") }
    var userId by rememberSaveable { mutableStateOf("") }

    // Fetch user details when screen is opened
    authRepository.loginUser(user.email, user.password,
        onLoginSuccess = { role, id ->
            userRole = role
            userId = id
        },
        onError = { errorMessage ->
            println(errorMessage) // Handle the error (e.g., show a snackbar)
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center // Centering vertically
    ) {
        Text(
            text = "Update Profile",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Profile Information Fields
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            label = { Text("Current Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Update Profile Button
        Button(
            onClick = {
                // Handle profile update logic
                if (name.isNotEmpty() && email.isNotEmpty() && currentPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                    authRepository.updateUserProfile(name, email, currentPassword, newPassword)
                    // You can add navigation or other logic after updating the profile
                } else {
                    // Handle the case where the fields are not complete
                    println("Please fill out all fields")
                }
            }
        ) {
            Text(text = "Update Profile")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Send Feedback Form
        TextField(
            value = feedback,
            onValueChange = { feedback = it },
            label = { Text("Your Feedback") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Handle feedback submission
            if (feedback.isNotEmpty()) {
                authRepository.sendFeedback(feedback)
                // You can add logic to handle the feedback submission (e.g., send it to a server)
            } else {
                // Handle the case where the feedback field is empty
                println("Please provide feedback")
            }
        }) {
            Text(text = "Send Feedback")
        }
    }
}

