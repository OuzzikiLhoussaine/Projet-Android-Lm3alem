package com.lm3alem.app.data.model

import java.util.Date

data class ServiceRequest(
    val id: String = "",
    val clientId: String = "",
    val artisanId: String = "",
    val description: String = "",
    val status: RequestStatus = RequestStatus.PENDING,
    val date: Date = Date()
)

enum class RequestStatus {
    PENDING, ACCEPTED, REFUSED, DONE
}
