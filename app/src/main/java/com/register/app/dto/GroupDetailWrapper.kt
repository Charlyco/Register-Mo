package com.register.app.dto

import com.register.app.model.Group

data class GroupDetailWrapper(val message: String, val status: Boolean, val data: Group?)
