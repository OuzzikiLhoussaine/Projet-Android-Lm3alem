package com.lm3alem.app.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = UserRole.UNDEFINED.name,
    val city: String = "",
    val imageUrl: String = ""
) {
    val userRole: UserRole
        get() = try {
            UserRole.valueOf(role)
        } catch (e: Exception) {
            UserRole.UNDEFINED
        }
}

enum class UserRole {
    CLIENT, ARTISAN, UNDEFINED
}
