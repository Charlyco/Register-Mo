package com.register.app.dto


data class ForumMessages(
    val message: String,
    val status: Boolean,
    val data: List<MessageData>?
)

data class MessageData(
    val id: Long,
    val groupId: Int?,
    val groupName: String?,
    val message: String?,
    val membershipId: String?,
    val senderName: String?,
    val imageUrl: String?,
    val sendTime: String?,
    val originalMessageId: Long? // the id of the message this replies to. If not null, this is a reply to a previous message
)
