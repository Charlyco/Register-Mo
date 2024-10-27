package com.register.app.dto

import com.register.app.model.Event

data class GroupUserEventsResponse(
    val paidEvents: List<Event>,
    val unpaidEvents: List<Event>
    )
