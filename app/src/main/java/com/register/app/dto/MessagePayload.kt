package com.register.app.dto

data class MessagePayload(
    val message: String,
    val membershipId: String,
    val senderName: String,
    val imageUrl: String?,
    val groupName: String,
    val groupId: Int,
    val sendTime: String,
    val originalMessageId: Long?
)
