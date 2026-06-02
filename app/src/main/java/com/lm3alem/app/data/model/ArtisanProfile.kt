package com.lm3alem.app.data.model

data class ArtisanProfile(
    val userId: String = "",
    val job: String = "",
    val description: String = "",
    val experience: Any? = 0,
    val city: String = "",
    val price: Any? = 0.0,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val successRate: Int = 100,
    val availability: String = "Mon - Sat, 8AM - 6PM",
    val specializations: List<String> = emptyList(),
    val isAvailable: Boolean = true,
    val projectCount: Int = 0
) {
    fun getExperienceInt(): Int {
        return when (experience) {
            is Number -> experience.toInt()
            is String -> experience.toIntOrNull() ?: 0
            else -> 0
        }
    }

    fun getPriceDouble(): Double {
        return when (price) {
            is Number -> price.toDouble()
            is String -> price.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
    }
}
