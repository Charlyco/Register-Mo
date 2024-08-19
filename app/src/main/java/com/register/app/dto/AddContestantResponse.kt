package com.register.app.dto

data class AddContestantResponse(
    val message: String,
    val status: Boolean,
    val data: Election?
)
