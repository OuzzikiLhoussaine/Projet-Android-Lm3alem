package com.lm3alem.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lm3alem.app.ui.screens.auth.ChooseRoleScreen
import com.lm3alem.app.ui.screens.auth.LoginScreen
import com.lm3alem.app.ui.screens.auth.RegisterScreen
import com.lm3alem.app.ui.screens.client.ClientHomeScreen
import com.lm3alem.app.ui.screens.artisan.ArtisanHomeScreen

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
        composable(Screen.ChooseRole.route) {
            ChooseRoleScreen(navController)
        }
        composable(
            route = Screen.Register.route,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role")
            RegisterScreen(navController, role)
        }
        composable(Screen.ClientHome.route) {
            ClientHomeScreen(navController)
        }
        composable(Screen.ArtisanHome.route) {
            ArtisanHomeScreen(navController)
        }
    }
}
