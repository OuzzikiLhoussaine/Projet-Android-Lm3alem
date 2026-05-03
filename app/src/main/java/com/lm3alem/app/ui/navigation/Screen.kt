package com.lm3alem.app.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ClientHome : Screen("client_home")
    object ArtisanHome : Screen("artisan_home")
}
