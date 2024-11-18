package com.register.app.dto

import com.register.app.model.MembershipDto

data class MembershipDtoWrapper(
    val message: String,
    val status: Boolean,
    val data: MembershipDto?
)