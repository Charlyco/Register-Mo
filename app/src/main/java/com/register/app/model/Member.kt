package com.register.app.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
@Entity(tableName = "members")
data class Member(
    @PrimaryKey
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