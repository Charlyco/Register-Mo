package com.register.app.dto

data class CreateGroupModel(
    val groupName: String,
    val groupDescription: String,
    val creatorEmail: String,
    val creatorFirstName: String,
    val creatorLastName: String,
    val logoUrl: String,
    val groupType: String
    )
