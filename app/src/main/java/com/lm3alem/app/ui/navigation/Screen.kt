package com.lm3alem.app.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object RoleSelection : Screen("role_selection")
    object ClientHome : Screen("client_home")
    object ArtisanHome : Screen("artisan_home")
    object EditArtisanProfile : Screen("edit_artisan_profile")
    object ArtisanDetails : Screen("artisan_details/{artisanId}") {
        fun createRoute(artisanId: String) = "artisan_details/$artisanId"
    }
}
