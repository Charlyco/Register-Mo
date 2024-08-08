package com.register.app.dto

data class Election(
    val electionId: Int?,
    val electionTitle: String,
    val electionDate: String,
    val description: String,
    val office: String?,
    val contestantList: MutableList<Contestant>,
    val groupId: Int?,
    val groupName: String?,
    val admin: String?,
    val winnerId: Long?,
    val electionStatus: String?
)
