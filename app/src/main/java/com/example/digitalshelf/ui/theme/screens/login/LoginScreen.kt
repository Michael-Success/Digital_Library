package com.example.digitalshelf.ui.theme.screens.login

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.digitalshelf.navigation.ROUTE_SIGNUP
import com.example.digitalshelf.repository.AuthRepository  // Import the repository



@Composable
fun LoginScreen(navController: NavHostController, onLoginSuccess: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") } // `var` is correct here
    var loading by remember { mutableStateOf(false) } // State to track loading
    val authRepository = AuthRepository()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Centering vertically
    ) {
        Text(text = "Login", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        // Email TextField
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Password TextField
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Error message display
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Show Circular Progress Indicator if loading
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        } else {
            // Login Button
            Button(
                onClick = {
                    loading = true // Set loading to true when login starts
                    authRepository.loginUser(
                        email = email,
                        password = password,
                        onLoginSuccess = { userRole ->
                            loading = false // Set loading to false when login is successful
                            // Handle success, navigate based on user role
                            if (userRole == "admin") {
                                navController.navigate("admin_dashboard")
                            } else {
                                navController.navigate("general_home")
                            }
                        },
                        onError = { message ->
                            loading = false // Set loading to false in case of error
                            errorMessage = message // Correctly assign the error message
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation to SignUpScreen
        TextButton(onClick = { navController.navigate(ROUTE_SIGNUP) }) {
            Text("Don't have an account? Sign Up")
        }
    }
}
