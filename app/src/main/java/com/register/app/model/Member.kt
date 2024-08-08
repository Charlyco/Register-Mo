package com.register.app.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
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
  ) : Parcelable