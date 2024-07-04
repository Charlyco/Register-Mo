package com.register.app.dto

import com.register.app.model.Event

data class EventDetailWrapper(val message: String, val status: Boolean, val data: Event?)
