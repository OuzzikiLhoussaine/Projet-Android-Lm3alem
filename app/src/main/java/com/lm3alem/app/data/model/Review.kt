package com.lm3alem.app.data.model

import java.util.Date

data class Review(
    val id: String = "",
    val clientId: String = "",
    val artisanId: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val date: Date = Date()
)
