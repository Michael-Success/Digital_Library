package com.example.digitalshelf.ui.theme.screens.admin


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavHostController
import com.example.digitalshelf.viewmodels.AdminViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.digitalshelf.models.Resource
import androidx.compose.runtime.collectAsState





@Composable
fun AdminDashboardScreen(navController: NavHostController, viewModel: AdminViewModel = viewModel()) {
    // State for resource details and error messages
    var resourceName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var fileType by remember { mutableStateOf("") }
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf("") }


    // State for monitoring upload progress and status
    val uploadProgress by viewModel.uploadProgress.collectAsState()
    val uploadStatus by viewModel.uploadStatus.collectAsState()

    // File selection launcher
    val getFileResult = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        fileUri = uri
    }

    // Callback to clear fields after successful upload
    val clearFields = {
        resourceName = ""
        description = ""
        fileType = ""
        fileUri = null
        errorMessage = ""
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

            // File Type Input (e.g., PDF, Video, Audio, Word)
            TextField(
                value = fileType,
                onValueChange = { fileType = it },
                label = { Text("File Type (PDF, Video, Audio, Word)") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            // Select File Button
            Button(
                onClick = {
                    // Launch file picker for multiple file types (PDFs, Word Docs, Audio, Video)
                    getFileResult.launch("application/pdf, application/msword, application/vnd.openxmlformats-officedocument.wordprocessingml.document, audio/*, video/*")
                },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(text = "Select File")
            }

            // Use UploadResourceButton composable for the upload action
            UploadResourceButton(
                viewModel = viewModel,
                resourceName = resourceName,
                description = description,
                fileType = fileType,
                fileUri = fileUri,
                onFieldsCleared = clearFields
            )

            // Progress Bar for file upload
            if (uploadProgress > 0f) {
                LinearProgressIndicator(
                    progress = uploadProgress / 100f,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )
                Text(text = "Uploading... ${uploadProgress.toInt()}%", fontSize = 16.sp)
            }

            // Error Message
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
            }

            // Success Message from ViewModel
            if (uploadStatus.isNotEmpty()) {
                Text(text = uploadStatus, color = Color.Green, modifier = Modifier.padding(bottom = 8.dp))
            }
        }
    }
}


@Composable
fun UploadResourceButton(
    viewModel: AdminViewModel,
    resourceName: String,
    description: String,
    fileType: String,
    fileUri: Uri?,
    onFieldsCleared: () -> Unit // Callback to clear fields after successful upload
) {
    var errorMessage by remember { mutableStateOf("") }
    val uploadStatus by viewModel.uploadStatus.collectAsState()
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(uploadStatus) {
        if (uploadStatus == "Upload successfully!") {
            onFieldsCleared()
            errorMessage = ""
        } else if (uploadStatus.isNotBlank() && uploadStatus != "Upload successfully!") {
            errorMessage = uploadStatus
        }
    }
    if (loading) {
        CircularProgressIndicator(modifier = Modifier.size(48.dp))
    } else {

        Button(
            onClick = {
                if (resourceName.isBlank() || description.isBlank() || fileType.isBlank() || fileUri == null) {
                    errorMessage = "All fields are required, and a file must be selected."
                } else {
                    fileUri?.let { uri ->
                        // Using uploadFileToStorage
                        viewModel.uploadFileToStorage(
                            fileUri = uri,
                            onSuccess = { fileUrl ->
                                val resource = Resource(
                                    name = resourceName,
                                    description = description,
                                    fileType = fileType,
                                    fileUrl = fileUrl
                                )
                                // Save resource metadata to the database
                                viewModel.uploadResourceMetadata(resource)
                            },
                            onFailure = { failureMessage ->
                                errorMessage = failureMessage
                            }
                        )
                    } ?: run {
                        errorMessage = "File URI is null. Please select a valid file."
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Upload Resource")
        }}

        if (errorMessage.isNotBlank()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.titleLarge
            )
        }


}


