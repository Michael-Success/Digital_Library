package com.example.digitalshelf.ui.theme.screens.admin



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.digitalshelf.navigation.ROUTE_ADMIN_DASHBOARD
import com.example.digitalshelf.navigation.ROUTE_GENERAL_HOME

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSelectionScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Admin Options") }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { navController.navigate(ROUTE_ADMIN_DASHBOARD) }
                ) {
                    Text("Go to Admin Dashboard")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate(ROUTE_GENERAL_HOME) }
                ) {
                    Text("Go to General Home")
                }
            }
        }
    )
}
