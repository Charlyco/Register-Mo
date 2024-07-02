package com.register.app.dto

data class ConfirmPaymentModel(
    val membershipId: String,
    val payerEmail: String,
    val eventTitle: String,
    val groupId: Int,
    val groupName: String,
    val amountPaid: Double,
    val outstandingAmount: Double,
    val methodOfPayment: String,
    val confirmedBy: String
)
