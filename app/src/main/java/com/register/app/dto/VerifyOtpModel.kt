package com.register.app.dto

data class VerifyOtpModel(
    val emailAddress: String,
    val otp: Int
)
