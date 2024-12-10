package com.example.digitalshelf.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.digitalshelf.ui.theme.screens.Home.Home
import com.example.digitalshelf.ui.theme.screens.about.AboutScreen
import com.example.digitalshelf.ui.theme.screens.admin.AdminDashboardScreen
import com.example.digitalshelf.ui.theme.screens.admin.AdminSelectionScreen
import com.example.digitalshelf.ui.theme.screens.contact.ContactScreen
import com.example.digitalshelf.ui.theme.screens.generalhomescreen.GeneralHomeScreen
import com.example.digitalshelf.ui.theme.screens.login.LoginScreen
import com.example.digitalshelf.ui.theme.screens.signup.SignUpScreen
import com.example.digitalshelf.ui.theme.screens.help.HelpScreen
import com.example.digitalshelf.ui.theme.screens.personallibrary.PersonalLibraryScreen
import com.example.digitalshelf.ui.theme.screens.settings.SettingsScreen


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
                navController = navController,
                onLoginSuccess = { userRole ->
                    if (userRole == "admin") {
                        navController.navigate(ROUTE_ADMIN_SELECTION)
                    } else {
                        navController.navigate(ROUTE_GENERAL_HOME) // Navigate to General Home for regular users
                    }
                }
            )
        }

        composable(ROUTE_ADMIN_SELECTION) {
            AdminSelectionScreen(navController = navController)
        }

        // General Home Page
        composable(ROUTE_GENERAL_HOME) {
            GeneralHomeScreen(navController)
        }

        // About Screen
        composable(ROUTE_ABOUT_SCREEN) {
            AboutScreen(navController)
        }

        // Contact Screen
        composable(ROUTE_CONTACT_SCREEN) {
            ContactScreen(navController)
        }

        // Help Screen
        composable(ROUTE_HELP_SCREEN) {
            HelpScreen(navController)
        }

        // Settings Screen
        composable(ROUTE_SETTINGS_SCREEN) {
            SettingsScreen(navController)
        }
        composable(ROUTE_PERSONAL_LIBRARY) {
            PersonalLibraryScreen(navController)
        }
    }
}
