package com.register.app.dto

import com.register.app.model.Event

data class ActivityRate(val eventsPaidFor: List<Event>, val eventsDue: List<Event>)
