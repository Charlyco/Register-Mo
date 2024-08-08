package com.register.app.dto

data class Contestant(
    val id: Long?,
    val contestantName: String?,
    val imageUrl: String?,
    val membershipId: String?,
    val contestantEmail: String?,
    val electionTitle: String?,
    val voteCount: Int?,
    val office: String?,
    val status: String?,
    val details: String?
)