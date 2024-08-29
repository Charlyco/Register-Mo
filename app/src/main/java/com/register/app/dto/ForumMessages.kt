package com.register.app.dto


data class ForumMessages(
    val message: String,
    val status: Boolean,
    val data: List<MessageData>?
)

data class MessageData(
    val groupId: Int?,
    val groupName: String?,
    val message: String?,
    val membershipId: String?,
    val senderName: String?,
    val imageUrl: String?,
    val sendTime: String?,
)
