package com.lm3alem.app.data.model

import com.google.firebase.Timestamp
import java.util.Date

data class Review(
    val id: String = "",
    val clientId: String = "",
    val artisanId: String = "",
    val requestId: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val date: Any? = null,
    val readByArtisan: Boolean = true
) {
    fun getFormattedDate(): Date {
        return when (date) {
            is Timestamp -> date.toDate()
            is Long -> Date(date)
            is Date -> date
            else -> Date()
        }
    }
}
