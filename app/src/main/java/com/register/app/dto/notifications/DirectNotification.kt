package com.register.app.dto.notifications

data class DirectNotification(
    val groupName: String,
    val groupId: Int,
    val message: String,
    val recipientEmail: String,
    val senderName: String,
    val sendTime: String,
    val token: String
)
