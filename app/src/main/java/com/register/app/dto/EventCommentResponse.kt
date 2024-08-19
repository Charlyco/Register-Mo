package com.register.app.dto

import com.register.app.model.Event

data class EventCommentResponse(
    val message: String?,
    val status: Boolean,
    val data: Event?
)
