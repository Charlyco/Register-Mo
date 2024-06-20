package com.register.app.dto

data class SignUpModel(
    val fullName: String,
    val phoneNumber: String,
    val emailAddress: String,
    val username: String,
    val password: String,
    val address: String
)
