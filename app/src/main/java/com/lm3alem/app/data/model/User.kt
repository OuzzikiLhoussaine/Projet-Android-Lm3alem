package com.lm3alem.app.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = UserRole.CLIENT.name,
    val city: String = "",
    val imageUrl: String = ""
) {
    val userRole: UserRole
        get() = try {
            UserRole.valueOf(role)
        } catch (e: Exception) {
            UserRole.CLIENT
        }
}

enum class UserRole {
    CLIENT, ARTISAN
}
