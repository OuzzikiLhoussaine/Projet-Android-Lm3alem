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
    object ArtisanProfile : Screen("artisan_profile")
    object AddReview : Screen("add_review/{artisanId}") {
        fun createRoute(artisanId: String) = "add_review/$artisanId"
    }
    object Profile : Screen("profile")
    object BookingHistory : Screen("booking_history")
    object Explore : Screen("explore?category={category}") {
        fun createRoute(category: String? = null) = if (category != null) "explore?category=$category" else "explore"
    }
    object EditProfile : Screen("edit_profile")
    object Settings : Screen("settings")
    object TermsOfService : Screen("terms_of_service")
    object Support : Screen("support")
    object CompleteProfile : Screen("complete_profile")
    object VerifyEmail : Screen("verify_email")
    object ForgotPassword : Screen("forgot_password")
    object Messages : Screen("messages")
    object ArtisanMessages : Screen("artisan_messages")
    object AdminDashboard : Screen("admin_dashboard")
    object ChatDetail : Screen("chat_detail/{chatRoomId}/{otherUserId}") {
        fun createRoute(chatRoomId: String, otherUserId: String) = "chat_detail/$chatRoomId/$otherUserId"
    }
    object MapPicker : Screen("map_picker")
    object Notifications : Screen("notifications")
}
