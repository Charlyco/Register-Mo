package com.register.app.dto

import com.google.gson.annotations.SerializedName
import com.register.app.model.Member
import kotlinx.serialization.Serializable

data class AuthResponse(
    val authToken: String,
    val refreshToken: RefreshToken,
    @SerializedName("userDto")
    val member: Member
)

@Serializable
data class RefreshToken(
    val refreshToken: String,
    val validity: Long,
    val issueDate: String
)