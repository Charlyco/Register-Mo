package com.register.app.dto

data class GroupNotification(
    val id: Long,
    val groupId: Int,
    val groupName: String,
    val title: String,
    val content: String,
    val type: String,
    val date: String
)