package com.example.digitalshelf.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.digitalshelf.ui.theme.screens.Home.Home
import com.example.digitalshelf.ui.theme.screens.admin.AdminDashboardScreen
import com.example.digitalshelf.ui.theme.screens.generalhomescreen.GeneralHomeScreen
import com.example.digitalshelf.ui.theme.screens.login.LoginScreen
import com.example.digitalshelf.ui.theme.screens.signup.SignUpScreen

//// Define your route constants here
//const val ROUTE_HOME = "home"
//const val ROUTE_SIGNUP = "signup"
//const val ROUTE_LOGIN = "login"
//const val ROUTE_ADMIN_DASHBOARD = "admin_dashboard"
//const val ROUTE_GENERAL_HOME = "general_home"

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_HOME // Start with "Home"
    ) {
        // Home/Landing Page
        composable(ROUTE_HOME) {
            Home(
                onGoToLibraryClick = { navController.navigate(ROUTE_SIGNUP) },
                onLoginClick = { navController.navigate(ROUTE_LOGIN) }
            )
        }

        // Admin Dashboard Page
        composable(ROUTE_ADMIN_DASHBOARD) {
            AdminDashboardScreen(navController)
        }

        // Sign-Up Page
        composable(ROUTE_SIGNUP) {
            SignUpScreen(navController = navController, onRegisterSuccess = {
                // Handle successful registration here
            })
        }

        // Login Page
        composable(ROUTE_LOGIN) {
            LoginScreen(
                navController = navController, // Pass navController to LoginScreen
                onLoginSuccess = { userRole ->
                    if (userRole == "admin") {
                        navController.navigate(ROUTE_ADMIN_DASHBOARD) // Navigate to Admin Dashboard
                    } else {
                        navController.navigate(ROUTE_GENERAL_HOME) // Navigate to General Home for regular users
                    }
                }
            )
        }

        composable(ROUTE_GENERAL_HOME){
            GeneralHomeScreen(navController)
        }

        // Add General Home or any other screens here as needed
    }
}
