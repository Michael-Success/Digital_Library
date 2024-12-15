package com.example.digitalshelf.ui.theme.screens.personallibrary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.digitalshelf.R
import com.example.digitalshelf.models.UserResource
import com.example.digitalshelf.navigation.ROUTE_ABOUT_SCREEN
import com.example.digitalshelf.navigation.ROUTE_CONTACT_SCREEN
import com.example.digitalshelf.navigation.ROUTE_GENERAL_HOME
import com.example.digitalshelf.navigation.ROUTE_HELP_SCREEN
import com.example.digitalshelf.navigation.ROUTE_LOGIN
import com.example.digitalshelf.navigation.ROUTE_SETTINGS_SCREEN
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.digitalshelf.viewmodels.PersonalLibraryViewModel
import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import com.example.digitalshelf.navigation.ROUTE_SHARING_SCREEN
import com.example.digitalshelf.navigation.ROUTE_UPLOAD_SCREEN
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState

import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberImagePainter
import com.example.digitalshelf.navigation.ROUTE_DELETE_ACCOUNT_SCREEN
import com.example.digitalshelf.navigation.ROUTE_PROFILE



@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
@Composable
fun PersonalLibraryScreen(navController: NavHostController, viewModel: PersonalLibraryViewModel = viewModel()) {
    val context = LocalContext.current
    val selectedTabs by viewModel.selectedTabs.collectAsState()
    val resources by viewModel.resources.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("Books") } // Default to "Books"
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    // Log selectedTabs for debugging
    LaunchedEffect(selectedTabs) {
        Log.d("PersonalLibraryScreen", "Selected Tabs: $selectedTabs")
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Actions", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        scope.launch { bottomSheetScaffoldState.bottomSheetState.collapse() }
                        navController.navigate(ROUTE_UPLOAD_SCREEN)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Upload Resource")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        scope.launch { bottomSheetScaffoldState.bottomSheetState.collapse() }
                        navController.navigate("sharing")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Share Resource")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        scope.launch { bottomSheetScaffoldState.bottomSheetState.collapse() }
                        showDialog = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Tab")
                }
            }
        },
        sheetPeekHeight = 0.dp,
        content = { paddingValues ->
            Scaffold(
                topBar = {
                    TopAppBar(
                        modifier = Modifier.background(MaterialTheme.colorScheme.primary),
                        title = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.applogo),
                                    contentDescription = "App Logo",
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Personal Library",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "Menu", tint = Color.Black)
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("About") },
                                    onClick = {
                                        expanded = false
                                        navController.navigate(ROUTE_ABOUT_SCREEN)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Contact") },
                                    onClick = {
                                        expanded = false
                                        navController.navigate(ROUTE_CONTACT_SCREEN)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Help") },
                                    onClick = {
                                        expanded = false
                                        navController.navigate(ROUTE_HELP_SCREEN)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Settings") },
                                    onClick = {
                                        expanded = false
                                        navController.navigate(ROUTE_SETTINGS_SCREEN)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete Account", color = Color.Red) },
                                    onClick = {
                                        expanded = false
                                        navController.navigate(ROUTE_DELETE_ACCOUNT_SCREEN)
                                    }
                                )
                            }
                        }
                    )
                },
                bottomBar = {
                    BottomAppBar {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TextButton(onClick = { /* Home */ }) { Text("Personal Library", color = Color.Blue) }
                            TextButton(onClick = { navController.navigate(ROUTE_PROFILE) }) {
                                Text("Personal Library", color = Color.Black)
                            }
                            TextButton(onClick = {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate(ROUTE_LOGIN) {
                                    popUpTo(0)
                                }
                            }) {
                                Text("Logout", color = Color.Black)
                            }
                        }
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = { scope.launch { bottomSheetScaffoldState.bottomSheetState.expand() } }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Resource")
                    }
                }
            ) { paddingValues ->
                Column(modifier = Modifier.padding(paddingValues)) {
                    // Display Selected Tabs
                    if (selectedTabs.isNotEmpty()) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            selectedTabs.forEach { tab ->
                                Button(
                                    onClick = { selectedTab = tab },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = if (selectedTab == tab) Color.Black else Color.White
                                    ),
                                    modifier = Modifier
                                        .background(if (selectedTab == tab) Color.White else Color.Gray)
                                ) {
                                    Text(text = tab)
                                }
                            }
                        }
                    } else {
                        Text("No tabs selected.")
                    }

                    // Display Resources based on selected tab
                    val filteredResources = resources.filter { it.description == selectedTab }
                    if (filteredResources.isNotEmpty()) {
                        LazyColumn {
                            items(filteredResources.size) { index ->
                                val resource = filteredResources[index]
                                ResourceCard(
                                    resource = resource,
                                    onDelete = { viewModel.deleteResource(resource) },
                                    onDownload = { viewModel.downloadResource(resource) },
                                    onPlay = { if (selectedTab == "Audios" || selectedTab == "Videos") viewModel.playResource(resource, context) },
                                    onShare = { viewModel.shareResource(resource, context) },
                                    showPlayButton = (selectedTab == "Audios" || selectedTab == "Videos"),
                                    isBook = selectedTab == "Books"
                                )
                            }
                        }
                    } else {
                        Text("No resources available for $selectedTab.")
                    }
                }
            }
        }
    )

    // Display AddTabDialog when showDialog is true
    AddTabDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onAddTab = { tabName -> viewModel.addNewTab(tabName) }
    )
}


// AddTabDialog Component
@Composable
fun AddTabDialog(showDialog: Boolean, onDismiss: () -> Unit, onAddTab: (String) -> Unit) {
    var newTabName by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Add New Tab") },
            text = {
                Column {
                    TextField(
                        value = newTabName,
                        onValueChange = { newTabName = it },
                        label = { Text("Tab Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTabName.isNotBlank()) {
                            onAddTab(newTabName)
                            onDismiss()
                        }
                    }
                ) {
                    Text("Add Tab")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
fun ResourceCard(
    resource: UserResource,
    onDelete: () -> Unit,
    onDownload: () -> Unit,
    onPlay: () -> Unit,
    onShare: () -> Unit,
    showPlayButton: Boolean,
    isBook: Boolean
) {
    // Array of drawable resources for book images
    val bookImages = listOf(
        R.drawable.book_placeholder1,
        R.drawable.book_placeholder2,
        R.drawable.book_placeholder3,
        R.drawable.book_placeholder4
    )

    // Select a random image resource ID from the array for books
    val imageRes = if (isBook) bookImages.random() else null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            if (imageRes != null) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp), // Full image height
                    contentScale = ContentScale.FillBounds // Ensure full image is shown
                )
            } else {
                Image(
                    painter = rememberImagePainter(
                        data = resource.storagePath, // Use resource path or a placeholder
                        builder = {
                            crossfade(true)
                            placeholder(R.drawable.placeholder)
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp), // Full image height
                    contentScale = ContentScale.FillBounds // Ensure full image is shown
                )
            }
            Spacer(modifier = Modifier.height(4.dp)) // Reduced space
            Text(text = resource.title ?: "Untitled", style = MaterialTheme.typography.bodySmall) // Reduced text size
            Spacer(modifier = Modifier.height(4.dp)) // Reduced space
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onDelete, modifier = Modifier.weight(1f)) {
                    Text("Delete")
                }
                Spacer(modifier = Modifier.width(4.dp)) // Reduced space
                Button(onClick = onDownload, modifier = Modifier.weight(1f)) {
                    Text("Download")
                }
                if (showPlayButton) {
                    Spacer(modifier = Modifier.width(4.dp)) // Reduced space
                    Button(onClick = onPlay, modifier = Modifier.weight(1f)) {
                        Text("Play")
                    }
                }
                Spacer(modifier = Modifier.width(4.dp)) // Reduced space
                Button(onClick = onShare, modifier = Modifier.weight(1f)) {
                    Text("Share")
                }
            }
        }
    }
}
