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
    object SendRequest : Screen("send_request/{artisanId}") {
        fun createRoute(artisanId: String) = "send_request/$artisanId"
    }
    object ArtisanRequests : Screen("artisan_requests")
    object AddReview : Screen("add_review/{artisanId}") {
        fun createRoute(artisanId: String) = "add_review/$artisanId"
    }
}
