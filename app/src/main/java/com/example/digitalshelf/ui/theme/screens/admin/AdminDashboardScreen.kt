package com.example.digitalshelf.ui.theme.screens.admin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.digitalshelf.viewmodels.AdminViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.digitalshelf.models.Resource

@Composable
fun AdminDashboardScreen(navController: NavHostController, viewModel: AdminViewModel = viewModel()) {
    var resourceName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var fileType by remember { mutableStateOf("") }
    var fileUri by remember { mutableStateOf<Uri?>(null) }  // File URI state
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Register for the result of the file selection intent
    val getFileResult = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        // Update the fileUri when a file is selected
        fileUri = uri
    }

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

            // File Type Input (Dropdown for PDF, Video, Audio, Audiobook)
            TextField(
                value = fileType,
                onValueChange = { fileType = it },
                label = { Text("File Type (PDF, Video, Audio, Audiobook)") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            // Select File Button
            Button(
                onClick = {
                    getFileResult.launch("application/pdf")  // Launch file picker for PDF files only
                },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(text = "Select File (PDF Only)")
            }

            // Upload Button
            Button(
                onClick = {
                    if (resourceName.isBlank() || description.isBlank() || fileType.isBlank() || fileUri == null) {
                        errorMessage = "All fields are required, and a file must be selected."
                    } else {
                        val resource = Resource(resourceName, description, fileType)
                        viewModel.uploadResource(resource, fileUri!!) // Upload the resource
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
