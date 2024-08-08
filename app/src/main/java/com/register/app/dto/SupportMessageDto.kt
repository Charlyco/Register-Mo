package com.register.app.dto

data class SupportMessageDto(
    val email: String,
    val fullName: String,
    val message: String,
    val messageType: String,
    val dateTime: String
)
