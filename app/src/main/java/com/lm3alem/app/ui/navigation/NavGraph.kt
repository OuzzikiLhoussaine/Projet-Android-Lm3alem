package com.lm3alem.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lm3alem.app.ui.screens.auth.LoginScreen
import com.lm3alem.app.ui.screens.auth.RegisterScreen
import com.lm3alem.app.ui.screens.client.ClientHomeScreen
import com.lm3alem.app.ui.screens.artisan.ArtisanHomeScreen

import androidx.compose.ui.Modifier

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
        composable(Screen.ClientHome.route) {
            ClientHomeScreen(navController)
        }
        composable(Screen.ArtisanHome.route) {
            ArtisanHomeScreen(navController)
        }
    }
}
