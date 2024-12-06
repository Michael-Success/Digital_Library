package com.example.digitalshelf.ui.theme.screens.admin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.database.FirebaseDatabase


@Composable
fun AdminDashboardScreen(navController: NavHostController) {
    var fileType by remember { mutableStateOf("") } // Holds the type of file selected
    var resourceName by remember { mutableStateOf("") } // Name of the resource
    var description by remember { mutableStateOf("") } // Description of the resource
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var fileUri by remember { mutableStateOf<Uri?>(null) } // File URI

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center) // Aligning the content to the center
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Admin Dashboard",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Resource Name Input
            TextField(
                value = resourceName,
                onValueChange = { resourceName = it },
                label = { Text("Resource Name") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            // Resource Description Input
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            // File Type Input (TextField instead of Dropdown)
            TextField(
                value = fileType,
                onValueChange = { fileType = it },
                label = { Text("File Type (e.g., PDF, Video, Audio, Audiobook)") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            // Select File Button
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "*/*" // Allows selection of any file type
                    }
                    (context as Activity).startActivityForResult(intent, 1234) // Replace with your logic to handle file selection
                },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(text = "Select File")
            }

            // Upload Button
            Button(
                onClick = {
                    if (resourceName.isBlank() || description.isBlank() || fileType.isBlank() || fileUri == null) {
                        errorMessage = "All fields are required, and a file must be selected."
                    } else {
                        uploadResourceToFirebase(
                            resourceName = resourceName,
                            description = description,
                            fileType = fileType,
                            fileUri = fileUri!!,
                            onSuccess = { successMessage = it },
                            onError = { errorMessage = it }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text(text = "Upload Resource")
            }

            // Error Message
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
            }

            // Success Message
            if (successMessage.isNotEmpty()) {
                Text(text = successMessage, color = Color.Green, modifier = Modifier.padding(bottom = 8.dp))
            }
        }
    }
}

// Function to upload file to Firebase
fun uploadResourceToFirebase(
    resourceName: String,
    description: String,
    fileType: String,
    fileUri: Uri,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    val storageReference = FirebaseStorage.getInstance().reference
    val databaseReference = FirebaseDatabase.getInstance().reference

    val fileRef = storageReference.child("Media/$fileType/${System.currentTimeMillis()}")
    val resourceMetadata = mapOf(
        "name" to resourceName,
        "description" to description,
        "fileType" to fileType
    )

    fileRef.putFile(fileUri)
        .addOnSuccessListener { taskSnapshot ->
            fileRef.downloadUrl.addOnSuccessListener { uri ->
                databaseReference.child("resources").push().setValue(resourceMetadata + ("url" to uri.toString()))
                    .addOnSuccessListener {
                        onSuccess("Resource uploaded successfully!")
                    }
                    .addOnFailureListener { exception ->
                        onError("Failed to save metadata: ${exception.message}")
                    }
            }
        }
        .addOnFailureListener { exception ->
            onError("File upload failed: ${exception.message}")
        }
}
