package com.register.app.model

import java.io.Serializable

data class Event(
    val eventId: Int?,
    val eventTitle: String?,
    val eventDescription: String?,
    val dateCreated: String?,
    val expirationDate: String?,
    val venue: String?,
    val imageUrlList: List<String>?,
    val eventComments: List<EventCommentDto>?,
    val eventReactionsList: List<EventReactionDto>?,
    val eventCreator: String?,
    val groupName: String?,
    val groupId: Int?,
    val levyAmount: Double?,
    val eventBudget: Double?,
    val amountRealized: Double?,
    val contributions: List<ContributionDto>?,
    val eventStatus: String?
)

data class EventReactionDto(
    val reactionId: Int?,
    val author: String?,
    val dateOfReaction: String?,
    val reactionType: String?,
    val eventId: Int?
)

data class ContributionDto(
    val id: Int?,
    val membershipId: String?,
    val memberEmail: String?,
    val eventId: Int?,
    val groupId: Int?,
    val groupName: String?,
    val amountPaid: Double?,
    val outstandingAmount: Double?,
    val dateOfPayment: String?,
    val methodOfPayment: String?,
    val confirmedBy: String?
)

data class EventCommentDto(
    val commentId: Int?,
    val userName: String?,
    val dateOfComment: String?,
    val comment: String?,
    val commentReplies: List<CommentReplyDto>?,
    val eventTitle: String?
)

data class CommentReplyDto(
    val replyId: Int?,
    val userName: String?,
    val dateOfReply: String?,
    val replyText: String?,
    val commentId: Int?
)

