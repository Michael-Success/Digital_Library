package com.example.digitalshelf.ui.theme.screens.generalhomescreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.digitalshelf.R
import com.example.digitalshelf.models.Resource
import com.example.digitalshelf.navigation.ROUTE_ABOUT_SCREEN
import com.example.digitalshelf.navigation.ROUTE_CONTACT_SCREEN
import com.example.digitalshelf.navigation.ROUTE_HELP_SCREEN
import com.example.digitalshelf.navigation.ROUTE_PERSONAL_LIBRARY
import com.example.digitalshelf.navigation.ROUTE_SETTINGS_SCREEN
import com.example.digitalshelf.viewmodels.AdminViewModel
import com.example.digitalshelf.viewmodels.GeneralHomeViewModel



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralHomeScreen(navController: NavHostController, viewModel: GeneralHomeViewModel = viewModel()) {
    // Observe the selected tab and resources
    val selectedTab by viewModel.selectedTab.collectAsState()
    val resources by viewModel.resources.collectAsState()

    var expanded by remember { mutableStateOf(false) } // For the menu dropdown

    // Call fetchResources whenever the selected tab changes
    LaunchedEffect(selectedTab) {
        viewModel.fetchResources(selectedTab) // Fetch resources based on the selected tab
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.background(MaterialTheme.colorScheme.primary),
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
                            text = "General Library",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.align(Alignment.Center) // Center the text
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
                    TextButton(onClick = { /* Home */ }) { Text("Home (Active)", color = Color.Black) }
                    TextButton(onClick = { navController.navigate(ROUTE_PERSONAL_LIBRARY) }) {
                        Text("Personal Library", color = Color.Black)
                    }
                    TextButton(onClick = { /* Logout */ }) { Text("Logout", color = Color.Black) }
                }
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                CategoryTabs(selectedTab = selectedTab, onTabSelected = { viewModel.updateSelectedTab(it) })
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(resources) { resource ->
                        ResourceCard(resource)
                    }
                }
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(query: String, onSearchQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onSearchQueryChange,
        placeholder = { Text("Search for resources...") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Gray
        )
    )
}

@Composable
fun CategoryTabs(selectedTab: String, onTabSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf("Books", "Audios", "Videos").forEach { tab ->
            TextButton(
                onClick = { onTabSelected(tab) },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (selectedTab == tab) Color.White else Color.LightGray
                )
            ) {
                Text(text = tab, fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Composable
fun ResourceCard(resource: Resource) {
    val context = LocalContext.current // Get the current context

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                // Load the image from Firebase Storage
                resource.fileUrl?.let { url ->
                    Image(
                        painter = rememberImagePainter(url),
                        contentDescription = "Resource Image",
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: run {
                    Text("Image Placeholder", color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = resource.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = resource.description,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { handleDownload(resource.fileUrl, context) }, // Pass the context explicitly
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Download")
            }
        }
    }
}

fun handleDownload(fileUrl: String?, context: Context) {
    if (fileUrl != null) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(fileUrl)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open file", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "Invalid file URL", Toast.LENGTH_SHORT).show()
    }
}

