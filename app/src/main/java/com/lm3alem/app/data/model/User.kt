package com.lm3alem.app.data.model

data class User(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.CLIENT,
    val city: String = "",
    val imageUrl: String = ""
)

enum class UserRole {
    CLIENT, ARTISAN
}
