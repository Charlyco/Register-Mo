package com.register.app.model

import com.register.app.dto.EventComment
import com.register.app.dto.Payment
import java.io.Serializable

data class Event(
    val eventId: Long,
    val eventTitle: String,
    val eventDescription: String,
    val dateCreated: String,
    val eventDate: String?,
    val imageUrlList: List<String>?,
    val eventComments: List<EventComment>?,
    val eventReactionsList: List<EventReactionDto>?,
    val eventCreator: String,
    val groupName: String?,
    val groupId: Int?,
    val levyAmount: Double?,
    val amountRealized: Double?,
    val contributions: List<ContributionDto>?,
    val pendingEvidenceOfPayment: List<Payment>?,
    val eventStatus: String?,
    val eventType: String?
)

data class EventReactionDto(
    val reactionId: Int?,
    val author: String?,
    val dateOfReaction: String?,
    val reactionType: String?,
    val eventId: Int?
)

data class ContributionDto(
    val id: Long?,
    val membershipId: String?,
    val memberEmail: String?,
    val payerName: String,
    val eventId: Int?,
    val groupId: Int?,
    val groupName: String?,
    val amountPaid: Double?,
    val outstandingAmount: Double?,
    val dateOfPayment: String?,
    val methodOfPayment: String?,
    val confirmedBy: String?
)

