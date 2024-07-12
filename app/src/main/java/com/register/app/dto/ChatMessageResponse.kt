package com.register.app.dto

import com.google.gson.annotations.SerializedName

data class ChatMessageResponse(
    @SerializedName("url")
    val url: String?,
    @SerializedName("time")
    val time: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("username")
    val username: String?,
    @SerializedName("mine")
    val isMine: Boolean?
)
