package com.register.app.dto

data class SignUpModel(
    val fullName: String,
    val phoneNumber: String,
    val emailAddress: String,
    var username: String,
    var password: String,
    var address: String
)
