package com.register.app.dto

import com.register.app.model.Event

data class ActivityRate(val message: String,
                        val status: Boolean,
                        val data: RateData?)

data class RateData (val eventsDue: Int, val eventsPaid: Int)
