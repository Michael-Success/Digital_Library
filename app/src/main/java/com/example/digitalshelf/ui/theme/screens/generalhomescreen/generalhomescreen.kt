package com.example.digitalshelf.ui.theme.screens.generalhomescreen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.digitalshelf.navigation.ROUTE_PERSONAL_LIBRARY
import com.example.digitalshelf.navigation.ROUTE_SETTINGS
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import androidx.compose.ui.platform.LocalContext
import com.example.digitalshelf.R

// Lighter background for the TopBar and Footer
val LightBlue = Color(0xFF9ECAE1) // Lighter Blue color for TopBar
val DarkNavyBlue = Color(0xFF001F3D) // Dark Navy Blue for buttons and footer

@Composable
fun GeneralHomeScreen(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf("Books") }
    var resources by remember { mutableStateOf<List<String>>(listOf()) }
    var searchQuery by remember { mutableStateOf("") }

    // Fetch resources from Firebase
    val context = LocalContext.current // Get context for Toast
    val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("resources")

    LaunchedEffect(selectedTab) {
        fetchResources(database, selectedTab, context) { newResources ->
            resources = newResources
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            TopBar(navController)

            Spacer(modifier = Modifier.height(20.dp))

            // Tab Selection
            TabBar(selectedTab) { newTab ->
                selectedTab = newTab
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Resources Display
            ResourcesList(resources)

            // Footer
            Spacer(modifier = Modifier.weight(1f)) // Push footer to bottom
            Footer(navController)
        }
    }
}

@Composable
fun TopBar(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(LightBlue) // Set background color for TopBar
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App logo (replace "R.drawable.logo" with your actual resource)
            Image(
                painter = painterResource(id = R.drawable.applogo),
                contentDescription = "App Logo",
                modifier = Modifier.size(40.dp) // Set size for logo
            )

            // Search Bar in the center
            Spacer(modifier = Modifier.weight(1f)) // Pushes the search bar to the center
            SearchBar(query = "") { /* Implement search query handling */ }
            Spacer(modifier = Modifier.weight(1f)) // Pushes the search bar to the center

            // Icons (Notifications, Menu)
            Row {
                IconButton(onClick = { /* Handle notifications */ }) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                }
                IconButton(onClick = { navController.navigate(ROUTE_SETTINGS) }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onSearchQueryChange: (String) -> Unit) {
    BasicTextField(
        value = query,
        onValueChange = onSearchQueryChange,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                // Implement search action
            }
        ),
        modifier = Modifier
            .fillMaxWidth(0.5f) // Narrow the width for centering
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(1.dp, Color.Gray)
            .padding(16.dp)
    )
}

@Composable
fun TabBar(selectedTab: String, onTabSelected: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        listOf("Books", "Videos", "Audios").forEach { tab ->
            Button(
                onClick = { onTabSelected(tab) },
                modifier = Modifier.padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == tab) DarkNavyBlue else Color.Gray,
                    contentColor = Color.White
                )
            ) {
                Text(text = tab, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun ResourcesList(resources: List<String>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        resources.forEach { resource ->
            Text(text = resource)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun Footer(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(DarkNavyBlue), // Set background color for footer
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = { /* Handle Home action */ }) {
            Text(text = "Home", color = Color.White)
        }
        TextButton(onClick = { navController.navigate(ROUTE_PERSONAL_LIBRARY) }) {
            Text(text = "Personal Library", color = Color.White)
        }
        TextButton(onClick = { /* Handle Logout action */ }) {
            Text(text = "Logout", color = Color.White)
        }
    }
}

fun fetchResources(database: DatabaseReference, selectedTab: String, context: android.content.Context, onResult: (List<String>) -> Unit) {
    // Fetch resources based on selected tab
    database.child(selectedTab).get().addOnSuccessListener { snapshot ->
        val resources = snapshot.children.mapNotNull { it.getValue<String>() }
        onResult(resources)
    }.addOnFailureListener {
        Toast.makeText(context, "Error fetching data", Toast.LENGTH_SHORT).show()
    }
}
