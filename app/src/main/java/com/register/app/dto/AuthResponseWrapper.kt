package com.register.app.dto

data class AuthResponseWrapper(
    val message: String,
    val status: Boolean,
    val data: AuthResponse?
)
