package com.register.app.dto

import com.register.app.model.Member

data class MemberDetailWrapper(val message: String, val status: Boolean, val data: List<Member>)