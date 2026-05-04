package com.lm3alem.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lm3alem.app.ui.screens.auth.LoginScreen
import com.lm3alem.app.ui.screens.auth.RegisterScreen
import com.lm3alem.app.ui.screens.auth.RoleSelectionScreen
import com.lm3alem.app.ui.screens.client.ClientHomeScreen
import com.lm3alem.app.ui.screens.artisan.ArtisanHomeScreen
import com.lm3alem.app.ui.screens.artisan.EditArtisanProfileScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(navController)
        }
        composable(Screen.ClientHome.route) {
            ClientHomeScreen(navController)
        }
        composable(Screen.ArtisanHome.route) {
            ArtisanHomeScreen(navController)
        }
        composable(Screen.EditArtisanProfile.route) {
            EditArtisanProfileScreen(navController)
        }
    }
}
