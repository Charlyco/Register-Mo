package com.register.app.dto

data class DirectChatMessages(
    val message: String,
    val status: Boolean,
    val data: List<DirectChatMessageData>?
)


data class DirectChatMessageData(
    val id: Long?,
    val message: String?,
    val senderName: String?,
    val senderId: String?,
    val recipientId: String?,
    val recipientEmail: String?,
    val imageUrl: String?,
    val sendTime: String?,
    val groupName: String?,
    val groupId: Int,
    val token: String?
)
