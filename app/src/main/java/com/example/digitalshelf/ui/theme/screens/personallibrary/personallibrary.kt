package com.example.digitalshelf.ui.theme.screens.personallibrary


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.digitalshelf.R
import com.example.digitalshelf.models.Resource
import com.example.digitalshelf.viewmodels.PersonalLibraryViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import com.example.digitalshelf.navigation.ROUTE_ABOUT_SCREEN
import com.example.digitalshelf.navigation.ROUTE_CONTACT_SCREEN
import com.example.digitalshelf.navigation.ROUTE_GENERAL_HOME
import com.example.digitalshelf.navigation.ROUTE_HELP_SCREEN
import com.example.digitalshelf.navigation.ROUTE_SETTINGS_SCREEN


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalLibraryScreen(navController: NavHostController, viewModel: PersonalLibraryViewModel = viewModel()) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val resources by viewModel.resources.collectAsState()

    var expanded by remember { mutableStateOf(false) } // For dropdown menu

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.applogo),
                                contentDescription = "App Logo",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Text(
                            text = "Personal Library",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.align(Alignment.Center)
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
                    TextButton(onClick = { ROUTE_GENERAL_HOME }) {
                        Text("General Library", color = Color.Black)
                    }
                    TextButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login_route") {
                            popUpTo(0)
                        }
                    }) {
                        Text("Logout", color = Color.Black)
                    }
                }
            }
        },
        floatingActionButton = {
            if (selectedTab != "Compress") {
                FloatingActionButton(onClick = { viewModel.startUpload(selectedTab) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Upload")
                }
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                PersonalLibraryTabs(
                    selectedTab = selectedTab,
                    onTabSelected = { viewModel.updateSelectedTab(it) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                when (selectedTab) {
                    "Photos" -> PhotosTab(resources = resources)
                    "Certificates" -> CertificatesTab(resources = resources)
                    "Books" -> BooksTab(resources = resources)
                    "Audios" -> AudiosTab(resources = resources)
                    "Videos" -> VideosTab(resources = resources)
                    "Notes" -> NotesTab(resources = resources)
                    "Compress" -> CompressTab()
                }
            }
        }
    )
}


@Composable
fun PersonalLibraryTabs(selectedTab: String, onTabSelected: (String) -> Unit) {
    val tabs = listOf("Photos", "Certificates", "Books", "Audios", "Videos", "Notes", "Compress")
    TabRow(
        selectedTabIndex = tabs.indexOf(selectedTab)
    ) {
        tabs.forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = tab,
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == tab) Color.Black else Color.DarkGray
                    )
                }
            )
        }
    }
}

@Composable
fun BooksTab(resources: List<Resource>) {
    val viewModel: PersonalLibraryViewModel = viewModel()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(resources.filter { it.fileType == "Book" }) { resource ->
            ResourceCard(resource)
        }
    }
}



@Composable
fun AudiosTab(resources: List<Resource>) {
    val viewModel: PersonalLibraryViewModel = viewModel()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(resources.filter { it.fileType == "Audio" }) { resource ->
            ResourceCard(resource)
        }
    }
}


@Composable
fun VideosTab(resources: List<Resource>) {
    val viewModel: PersonalLibraryViewModel = viewModel()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(resources.filter { it.fileType == "Video" }) { resource ->
            ResourceCard(resource)
        }
    }
}



@Composable
fun NotesTab(resources: List<Resource>) {
    val viewModel: PersonalLibraryViewModel = viewModel()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(resources.filter { it.fileType == "Note" }) { resource ->
            ResourceCard(resource)
        }
    }
}

