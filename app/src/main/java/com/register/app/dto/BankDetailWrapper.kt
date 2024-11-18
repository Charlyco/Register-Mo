package com.register.app.dto

data class BankDetailWrapper(
    val message: String,
    val status: Boolean,
    val data: BankDetail?
)