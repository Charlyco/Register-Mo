package com.register.app.dto

data class ConfirmBulkPaymentDto(
    val paymentId: Long,
    val membershipId: String,
    val payerEmail: String,
    val payerName: String,
    val eventItemDtoList: MutableList<EventItemDto> = mutableListOf(),
    val groupId: Int,
    val groupName: String,
    val outstandingAmount: Double,
    val methodOfPayment: String,
    val confirmedBy: String
)
