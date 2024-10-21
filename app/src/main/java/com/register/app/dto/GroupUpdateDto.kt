package com.register.app.dto

data class GroupUpdateDto(
    val groupName: String,
    val groupDescription: String,
    val groupType: String,
    val logoUrl: String,
    val groupEmail: String,
    val phoneNumber: String,
    val address: String
)
