package com.register.app.dto

data class GenericResponse(
    val message: String,
    val status: Boolean,
    val data: Any?
)
