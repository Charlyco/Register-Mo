package com.register.app.dto

data class EventComment( val commentId: Int, val username: String, val dateOfComment: String, val comment: String, val reply: List<CommentReply>)

data class CommentReply(val replyId: Int, val username: String, val dateOfComment: String, val reply: String,)
