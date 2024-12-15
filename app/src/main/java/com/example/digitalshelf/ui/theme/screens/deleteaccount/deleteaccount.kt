package com.example.digitalshelf.ui.theme.screens.deleteaccount

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.digitalshelf.viewmodels.PersonalLibraryViewModel


const val ROUTE_LOGIN = "login"

@Composable
fun DeleteAccountScreen(navController: NavController) {
    val viewModel: PersonalLibraryViewModel = viewModel()
    val deleteState by viewModel.deleteState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Delete Account",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Are you sure you want to delete your account? This action cannot be undone.",
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.deleteAccount()
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
        ) {
            Text(text = "Delete Account", color = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text(text = "Cancel")
        }

        deleteState?.let { state ->
            when (state) {
                is PersonalLibraryViewModel.DeleteState.Success -> {
                    navController.popBackStack()
                    navController.navigate(ROUTE_LOGIN) // Redirect to login screen after deletion
                }
                is PersonalLibraryViewModel.DeleteState.Error -> {
                    Text(text = "Error: ${state.message}", color = Color.Red)
                }
            }
        }
    }
}
