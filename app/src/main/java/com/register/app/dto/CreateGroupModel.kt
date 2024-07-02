package com.register.app.dto

data class CreateGroupModel(
    val groupName: String,
    val groupDescription: String,
    val creatorEmail: String,
    val creatorName: String,
    val creatorOffice: String,
    val groupType: String,
    val logoUrl: String
    )
