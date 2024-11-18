package com.register.app.dto

data class RejectedPayment(
    val eventTitle: String?,
    val eventId: Long?,
    val imageUrl: String?,
    val membershipId: String?,
    val payerFullName: String?,
    val groupName: String?,
    val confirmedBy: String,
    val reason: String
)
