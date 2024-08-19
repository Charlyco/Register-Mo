package com.register.app.dto

data class EventComment(
    val commentId: Int?,
    val username: String?,
    val dateOfComment: String,
    val comment: String?,
    val commentReplies: List<CommentReply>?,
    val eventId: Int?
)

data class CommentReply(
    val replyId: Int?,
    val username: String?,
    val dateOfReply: String?,
    val replyText: String?,
    val commentId: Int?
)
