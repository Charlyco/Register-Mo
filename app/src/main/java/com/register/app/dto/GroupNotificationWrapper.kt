package com.register.app.dto

data class GroupNotificationWrapper(
    val message: String,
    val status: Boolean,
    val data: List<GroupNotification>?
)
