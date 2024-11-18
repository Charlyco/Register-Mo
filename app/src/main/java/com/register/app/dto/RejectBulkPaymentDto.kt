package com.register.app.dto

data class RejectBulkPaymentDto(
    val paymentId: Long,
    val imageUrl: String,
    val membershipId: String,
    val payerFullName: String,
    val groupName: String,
    val groupId: Int,
    val confirmedBy: String,
    val reason: String
)
