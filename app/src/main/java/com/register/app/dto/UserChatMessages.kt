package com.register.app.dto

import com.google.gson.annotations.SerializedName

class UserChatMessages(
    val message: String,
    val code: Int,
    val data: List<MessageData>?,
    val status: Boolean
)

data class MessageData(
    val url: String?,
    val time: String,
    val id: String,
    val message: String?,
    val username: String,
    val mine: Boolean
)
