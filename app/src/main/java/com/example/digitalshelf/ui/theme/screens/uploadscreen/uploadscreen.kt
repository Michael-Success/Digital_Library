package com.example.digitalshelf.ui.theme.screens.uploadscreen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.digitalshelf.viewmodels.PersonalLibraryViewModel





@Composable
fun UploadScreen(navController: NavHostController, viewModel: PersonalLibraryViewModel = viewModel()) {
    val context = LocalContext.current
    val uploadStatus by viewModel.uploadStatus.collectAsState()
    val uploadProgress by viewModel.uploadProgress.collectAsState()

    var selectedTab by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var resourceTitle by remember { mutableStateOf("") }  // Field to input the resource title
    val availableTabs = listOf("Books", "Audios", "Videos", "Notes", "Photos", "Certificates")

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.uploadResource(uri, selectedTab, resourceTitle, context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select Tab to Upload Resource", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // Input field for the resource title
        TextField(
            value = resourceTitle,
            onValueChange = { resourceTitle = it },
            label = { Text("Resource Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Button to open the dialog
        Button(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Text(if (selectedTab.isEmpty()) "Select Tab" else selectedTab)
        }

        // AlertDialog for selecting the tab
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Select Tab") },
                text = {
                    Column {
                        availableTabs.forEach { tab ->
                            TextButton(
                                onClick = {
                                    selectedTab = tab
                                    showDialog = false
                                }
                            ) {
                                Text(tab)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Upload button
        Button(
            onClick = {
                if (selectedTab.isNotEmpty()) {
                    launcher.launch("*/*")
                } else {
                    // Show a message to select a tab first
                    viewModel.updateUploadStatus("Please select a tab to upload the resource")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Upload Resource")
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (uploadProgress > 0 && uploadProgress < 100) {
            LinearProgressIndicator(progress = uploadProgress / 100f)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(uploadStatus) // Display the upload status
    }
}
