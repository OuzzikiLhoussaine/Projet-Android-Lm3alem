package com.lm3alem.app.data.model

import com.google.firebase.Timestamp
import java.util.Date

data class ServiceRequest(
    val id: String = "",
    val clientId: String = "",
    val artisanId: String = "",
    val description: String = "",
    val status: RequestStatus = RequestStatus.PENDING,
    val date: Any? = null
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

enum class RequestStatus {
    PENDING, ACCEPTED, REFUSED, DONE
}
