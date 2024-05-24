package com.register.app.dto

data class VerifyOtpModel(
    val phoneNumber: String,
    val otp: Int
)
