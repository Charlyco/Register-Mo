package com.register.app.dto

data class SupportMessageDto(
    val id: Long?,
    val email: String,
    val fullName: String,
    val message: String,
    val messageType: String,
    val dateTime: String,
    val sender: String,
    val recipient: String,
    val token: String
)
