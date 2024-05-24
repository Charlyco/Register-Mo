package com.register.app.dto

import com.register.app.model.Member

data class AuthResponse(
    val authToken: String,
    val member: Member
)