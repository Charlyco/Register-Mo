package com.register.app.dto

data class ConfirmPaymentModel(
    val membershipId: String,
    val payerEmail: String,
    val payerName: String,
    val eventTitle: String,
    val eventId: Long,
    val groupId: Int,
    val groupName: String,
    val amountPaid: Double,
    val outstandingAmount: Double,
    val methodOfPayment: String,
    val confirmedBy: String
)
