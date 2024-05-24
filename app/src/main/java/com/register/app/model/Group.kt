package com.register.app.model

data class Group(
    val groupId: Int,
    val groupName: String,
    val groupDescription: String?,
    val email: String?,
    val phone: String?,
    val address: String?,
    val creatorName: String,
    val dateCreated: String,
    val memberList: List<MembershipDto>?,
    val pendingMemberRequests: List<MembershipRequest>?,
    val adminList: List<String>,
    val logoUrl: String,
    val walletBalance:String?,
    val status: String)

data class MembershipDto(val email: String, val membershipId: String)
data class MembershipRequest(
    val id: Int,
    val memberEmail: String,
    val timeOfRequest: String)