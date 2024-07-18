package com.register.app.dto

import com.register.app.model.Group

data class GroupsWrapper(
    val message: String,
    val status: Boolean,
    val data: List<Group>?
)