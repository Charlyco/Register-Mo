package com.register.app.dto

data class ActivityRate(val message: String,
                        val status: Boolean,
                        val data: RateData?)

data class RateData (val eventsDue: Int, val eventsPaid: Int)
