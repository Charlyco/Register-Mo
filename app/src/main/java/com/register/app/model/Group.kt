package com.register.app.model

import com.register.app.dto.BankDetail
import com.register.app.enums.MemberOffice
import com.register.app.enums.MemberStatus

data class Group(
    val groupId: Int,
    val groupName: String,
    val groupDescription: String?,
    val groupEmail: String?,
    val phoneNumber: String?,
    val address: String?,
    val creatorName: String,
    val dateCreated: String,
    val memberList: List<MembershipDto>?,
    val pendingMemberRequests: List<MembershipRequest>?,
    val logoUrl: String?,
    val walletBalance:String?,
    val bankDetails: BankDetail?,
    val groupType: String?,
    val status: String?)

data class MembershipDto(
    val membershipId: String,
    val emailAddress: String,
    val joinedDateTime: String,
    val designation: String,
    val memberOffice: String,
    val memberStatus: String)

data class MembershipRequest(
    val id: Int,
    val memberEmail: String,
    val memberFullName: String?,
    val groupId: Int,
    val timeOfRequest: String)

data class AdminDto(
    val name: String,
    val membershipId: String,
    val memberOffice: MemberOffice
)