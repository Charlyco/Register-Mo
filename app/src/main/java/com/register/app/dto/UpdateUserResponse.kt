package com.register.app.dto

import com.register.app.model.Member

data class UpdateUserResponse(
    val message: String,
    val status: Boolean,
    val data: Member?
)
