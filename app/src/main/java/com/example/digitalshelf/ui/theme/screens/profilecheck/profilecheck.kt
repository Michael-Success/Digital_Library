package com.example.digitalshelf.ui.theme.screens.profilecheck

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.digitalshelf.viewmodels.ProfileCheckViewModel
import com.example.digitalshelf.navigation.ROUTE_PERSONAL_LIBRARY
import com.example.digitalshelf.navigation.ROUTE_PROFILE
import com.example.digitalshelf.navigation.ROUTE_PROFILE_CHECK


@Composable
fun ProfileCheckScreen(navController: NavHostController, viewModel: ProfileCheckViewModel = viewModel()) {
    val profileStatus by viewModel.profileStatus.collectAsState()

    LaunchedEffect(profileStatus) {
        when (profileStatus) {
            "setup" -> navController.navigate(ROUTE_PERSONAL_LIBRARY) {
                popUpTo(ROUTE_PROFILE_CHECK) { inclusive = true }
            }
            "not_setup" -> navController.navigate(ROUTE_PROFILE) {
                popUpTo(ROUTE_PROFILE_CHECK) { inclusive = true }
            }
        }
    }

    Scaffold(
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    )
}
