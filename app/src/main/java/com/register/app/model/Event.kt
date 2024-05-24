package com.register.app.model

import java.io.Serializable

data class Event(
    val eventId: Int,
    val eventTitle: String,
    val eventDescription: String,
    val dateCreated: String,
    val expirationDate: String?,
    val venue: String?,
    val imageUrlList: List<String?>,
    val eventCommentsCount: Int?,
    val eventReactions: List<EventReaction>?,
    val eventCreator: String,
    val groupName: String,
    val levyAmount: Double?,
    val eventBudget: Double?,
    val amountRealized: Double?,
    val paidMembersList: List<Int>?,
    val eventStatus: String?
)

data class EventReaction(
    val reactionId: Int?,
    val userId: Int?,
    val dateOfReaction: String?,
    val reactionType: String?
) : Serializable
