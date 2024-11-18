package com.register.app.dto

import com.register.app.model.Group

data class AdminUpdateResponse(
    val message: String,
    val status: Boolean,
    val data: Group?
)
