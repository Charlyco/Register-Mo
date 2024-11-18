package com.register.app.dto.notifications

data class ElectionNotification(
    val electionTitle: String,
    val dateOfElection: String,
    val groupId: Int,
    val groupName: String,
    val electionEvent: String,
)
