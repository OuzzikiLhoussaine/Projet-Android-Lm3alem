package com.lm3alem.app.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.CLIENT
)

enum class UserRole {
    CLIENT, ARTISAN
}
