package com.register.app.dto

data class BulkPaymentModel(
    val id: Long?,
    val imageUrl: String,
    val eventItemDtos: MutableSet<EventItemDto> = mutableSetOf(),
    val membershipId: String,
    val payerEmail: String,
    val payerFullName: String,
    val groupName: String,
    val groupId: Int,
    val amountToPay: Double,
    val paymentMethod: String
)

data class EventItemDto(
    val eventTitle: String,
    val eventId: Long,
    val amountPaid: Double
)
