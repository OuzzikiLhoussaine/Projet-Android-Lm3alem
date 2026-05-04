package com.lm3alem.app.data.model

data class ArtisanProfile(
    val userId: String = "",
    val job: String = "",
    val description: String = "",
    val experience: Int = 0,
    val city: String = "",
    val price: Double = 0.0,
    val rating: Double = 0.0,
    val reviewCount: Int = 0
)
