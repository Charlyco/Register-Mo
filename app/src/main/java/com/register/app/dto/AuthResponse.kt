package com.register.app.dto

import com.google.gson.annotations.SerializedName
import com.register.app.model.Member

data class AuthResponse(
    val authToken: String,
    @SerializedName("userDto")
    val member: Member
)