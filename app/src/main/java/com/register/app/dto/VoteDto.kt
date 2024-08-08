package com.register.app.dto

data class VoteDto(
    val voteId: Int?,
    val voterName: String?,
    val groupId: Int?,
    val contestantName: String?,
    val contestantId: Long?,
    val electionId: Int?,
    val office: String?,
    val dateAndTime: String?,
    val status: String?
)
