package com.register.app.dto

data class Payment(
    val imageUrl: String, val membershipId: String, val payerEmail: String, val payerFullName: String, val eventTitle: String, val eventId: Int, val groupName: String
)
