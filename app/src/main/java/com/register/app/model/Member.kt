package com.register.app.model

import kotlinx.serialization.Serializable

@Serializable
data class Member(
    val userId: Int?,
    val fullName: String,
    val userName: String?,
    val phoneNumber: String,
    val emailAddress: String,
    val address: String?,
    val imageUrl: String?,
    val status: String?,
    val memberPost: String?,
    val signupDateTime: String?,
    val role: String?,
    val groupIds: List<Int>?
  )
