package com.example.digitalshelf.ui.theme.screens.about

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text

@Composable
fun AboutScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "About Us",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Welcome to the Digital Library, a platform where users can explore a wide variety of resources ranging from books to audios and videos. Our mission is to make learning accessible to everyone.",
            fontSize = 16.sp
        )
    }
}
