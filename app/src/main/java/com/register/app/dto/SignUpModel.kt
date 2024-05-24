package com.register.app.dto

data class SignUpModel(
    val fullName: String,
    val email: String,
    val password: String,
    val rePassword: String,
    val phoneNumber: String?
)
