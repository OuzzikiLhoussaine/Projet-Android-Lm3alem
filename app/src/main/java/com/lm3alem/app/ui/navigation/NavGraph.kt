package com.lm3alem.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lm3alem.app.ui.screens.auth.LoginScreen
import com.lm3alem.app.ui.screens.auth.RegisterScreen
import com.lm3alem.app.ui.screens.auth.RoleSelectionScreen
import com.lm3alem.app.ui.screens.auth.CompleteProfileScreen
import com.lm3alem.app.ui.screens.auth.VerifyEmailScreen
import com.lm3alem.app.ui.screens.auth.ForgotPasswordScreen
import com.lm3alem.app.ui.screens.client.ClientHomeScreen
import com.lm3alem.app.ui.screens.client.ArtisanDetailsScreen
import com.lm3alem.app.ui.screens.artisan.ArtisanHomeScreen
import com.lm3alem.app.ui.screens.artisan.EditArtisanProfileScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

import com.lm3alem.app.ui.screens.client.SendRequestScreen
import com.lm3alem.app.ui.screens.client.AddReviewScreen
import com.lm3alem.app.ui.screens.artisan.ArtisanRequestsScreen
import com.lm3alem.app.ui.screens.profile.ProfileScreen
import com.lm3alem.app.ui.screens.profile.EditProfileScreen

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
        composable(Screen.CompleteProfile.route) {
            CompleteProfileScreen(navController)
        }
        composable(Screen.VerifyEmail.route) {
            VerifyEmailScreen(navController)
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController)
        }
        composable(Screen.ClientHome.route) {
            ClientHomeScreen(navController)
        }
        composable(
            route = Screen.ArtisanDetails.route,
            arguments = listOf(navArgument("artisanId") { type = NavType.StringType })
        ) { backStackEntry ->
            val artisanId = backStackEntry.arguments?.getString("artisanId")
            ArtisanDetailsScreen(navController, artisanId)
        }
        composable(Screen.ArtisanHome.route) {
            ArtisanHomeScreen(navController)
        }
        composable(Screen.EditArtisanProfile.route) {
            EditArtisanProfileScreen(navController)
        }
        composable(
            route = Screen.SendRequest.route,
            arguments = listOf(navArgument("artisanId") { type = NavType.StringType })
        ) { backStackEntry ->
            val artisanId = backStackEntry.arguments?.getString("artisanId")
            SendRequestScreen(navController, artisanId)
        }
        composable(Screen.ArtisanRequests.route) {
            ArtisanRequestsScreen(navController)
        }
        composable(
            route = Screen.AddReview.route,
            arguments = listOf(navArgument("artisanId") { type = NavType.StringType })
        ) { backStackEntry ->
            val artisanId = backStackEntry.arguments?.getString("artisanId")
            AddReviewScreen(navController, artisanId)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController)
        }
    }
}
