package com.register.app.model

data class Member(
    val userId: Int?,
    val fullName: String,
    val phoneNumber: String,
    val emailAddress: String,
    val address: String,
    val imageUrl: String?,
    val status: String,
    val memberPost: String,
    val walletBalance: Double,
    val signupDateTime: String,
    val role: String,
    val groupIds: List<Int>
  )
