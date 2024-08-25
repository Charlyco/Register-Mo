package com.register.app.dto

data class AllResponsesWrapper(
    val message: String,
    val status: Boolean,
    val data: List<FormUserResponseDto>?
)
