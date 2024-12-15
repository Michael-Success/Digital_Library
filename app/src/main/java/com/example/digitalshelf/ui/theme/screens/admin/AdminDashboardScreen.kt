package com.example.digitalshelf.ui.theme.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.digitalshelf.R
import com.example.digitalshelf.models.Resource
import com.example.digitalshelf.viewmodels.AdminViewModel

@Composable
fun AdminDashboardScreen(navController: NavHostController, viewModel: AdminViewModel = viewModel()) {
    var resourceName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var fileType by remember { mutableStateOf("") }
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    val uploadProgress by viewModel.uploadProgress.collectAsState()
    val uploadStatus by viewModel.uploadStatus.collectAsState()

    val getFileResult = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        fileUri = uri
    }

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

            TextField(
                value = resourceName,
                onValueChange = { resourceName = it },
                label = { Text("Resource Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            TextField(
                value = fileType,
                onValueChange = { fileType = it },
                label = { Text("File Type (PDF, Video, Audio, Word)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    getFileResult.launch("application/pdf, application/msword, application/vnd.openxmlformats-officedocument.wordprocessingml.document, audio/*, video/*")
                },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(text = "Select File")
            }

            UploadResourceButton(
                viewModel = viewModel,
                resourceName = resourceName,
                description = description,
                fileType = fileType,
                fileUri = fileUri,
                onFieldsCleared = clearFields
            )

            // Progress bar to show upload progress
            if (uploadProgress > 0f && uploadProgress < 100f) {
                LinearProgressIndicator(
                    progress = uploadProgress / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
                Text(text = "Uploading... ${uploadProgress.toInt()}%", fontSize = 16.sp)
            }

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
            }

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
    onFieldsCleared: () -> Unit
) {
    var errorMessage by remember { mutableStateOf("") }
    val uploadStatus by viewModel.uploadStatus.collectAsState()
    var loading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val adminViewModel: AdminViewModel = viewModel()

    LaunchedEffect(uploadStatus) {
        if (uploadStatus == "Upload successfully!") {
            onFieldsCleared()
            errorMessage = ""
        } else if (uploadStatus.isNotBlank() && uploadStatus != "Upload successfully!") {
            errorMessage = uploadStatus
        }
    }

    val drawableResIds = when (fileType) {
        "Books" -> listOf(R.drawable.book_image1, R.drawable.book_image2, R.drawable.book_image3, R.drawable.book_image4)
        "Audios" -> listOf(R.drawable.audio_image)
        "Videos" -> listOf(R.drawable.video_image)
        else -> listOf(R.drawable.image_placeholder) // Ensure you have this placeholder image
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
                        adminViewModel.uploadFileToStorageWithPreview(
                            context = context,
                            fileUri = uri,
                            onSuccess = { fileUrl, previewUrl ->
                                val resource = Resource(
                                    name = resourceName,
                                    description = description,
                                    fileType = fileType,
                                    fileUrl = fileUrl,
                                    previewUrl = previewUrl,
                                    drawableResIds = drawableResIds // Include drawableResIds
                                )
                                adminViewModel.uploadResourceMetadata(resource)
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
        }
    }

    if (errorMessage.isNotBlank()) {
        Text(
            text = errorMessage,
            color = Color.Red,
            style = MaterialTheme.typography.titleLarge
        )
    }
}
