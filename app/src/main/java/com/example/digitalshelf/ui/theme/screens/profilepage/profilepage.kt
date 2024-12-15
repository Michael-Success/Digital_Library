package com.example.digitalshelf.ui.theme.screens.profilepage

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.digitalshelf.navigation.ROUTE_PROFILE_CHECK
import com.example.digitalshelf.navigation.ROUTE_PERSONAL_LIBRARY

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfilePage(navController: NavHostController) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var nationality by remember { mutableStateOf("") }
    var profilePictureUri by remember { mutableStateOf<Uri?>(null) }
    val tabs = listOf("Books", "Photos", "Audios", "Certificates", "Videos", "Notes")
    val selectedTabs = remember { mutableStateListOf<String>() }
    var isLoading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            profilePictureUri = uri
        }
    )

    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val database = FirebaseDatabase.getInstance().reference

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Profile Setup", style = MaterialTheme.typography.titleLarge, modifier = Modifier.align(Alignment.CenterHorizontally))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (profilePictureUri == null) {
                    Text("Profile Picture", color = Color.Gray)
                } else {
                    Image(
                        painter = rememberImagePainter(data = profilePictureUri),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(100.dp).clip(CircleShape)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Full Name Field
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            isError = showError && fullName.isBlank()
        )

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        // Age Field
        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age") },
            modifier = Modifier.fillMaxWidth(),
            isError = showError && age.isBlank()
        )

        // Nationality Field
        OutlinedTextField(
            value = nationality,
            onValueChange = { nationality = it },
            label = { Text("Nationality") },
            modifier = Modifier.fillMaxWidth()
        )

        // Tabs Selection in Flow Format
        Text("Select Tabs to Include in Your Library:", style = MaterialTheme.typography.titleMedium)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEach { tab ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = selectedTabs.contains(tab),
                        onCheckedChange = {
                            if (it) selectedTabs.add(tab) else selectedTabs.remove(tab)
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(tab)
                }
            }
        }

        // Save Button with Validation
        Button(
            onClick = {
                if (fullName.isNotBlank() && age.isNotBlank()) {
                    isLoading = true
                    showError = false
                    userId?.let {
                        val profileData = mapOf(
                            "fullName" to fullName,
                            "email" to email,
                            "age" to age,
                            "nationality" to nationality,
                            "profilePictureUri" to (profilePictureUri?.toString() ?: ""),
                            "selectedTabs" to selectedTabs.toList()
                        )

                        database.child("PROFILES").child(it).setValue(profileData)
                            .addOnSuccessListener {
                                isLoading = false
                                navController.navigate(ROUTE_PROFILE_CHECK) {
                                    popUpTo(ROUTE_PROFILE_CHECK) { inclusive = true }
                                }
                            }
                            .addOnFailureListener {
                                isLoading = false
                                // Show error message (could use Snackbar or a dialog)
                            }
                    }
                } else {
                    showError = true
                    // Show an error message indicating the necessary fields are empty
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Save and Proceed")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // "You already set up your profile, click here" text
        Text(
            text = "You already set up your profile, click here.",
            color = Color.Blue,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                    navController.navigate(ROUTE_PROFILE_CHECK) {
                        popUpTo(ROUTE_PROFILE_CHECK) { inclusive = true }
                    }
                }
        )
    }
}
