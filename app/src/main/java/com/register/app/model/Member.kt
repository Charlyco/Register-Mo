package com.register.app.model

import kotlinx.serialization.Serializable

@Serializable
data class Member(
    val userId: Int?,
    var fullName: String,
    var userName: String?,
    var phoneNumber: String,
    var emailAddress: String,
    var address: String?,
    var imageUrl: String?,
    val status: String?,
    val memberPost: String?,
    val signupDateTime: String?,
    val role: String?,
    val groupIds: List<Int>?
  )
