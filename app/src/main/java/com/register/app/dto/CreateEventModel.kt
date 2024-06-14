package com.register.app.dto

data class CreateEventModel(
    val eventTitle: String?,
    val eventDescription: String?,
    val dateCreated: String?,
    val eventDate: String?,
    val venue: String?,
    val imageUrlList: List<String>?,
    var eventCreator: String?,
    var groupName: String?,
    var groupId: Int?,
    val levyAmount: Double?,
    val eventBudget: Double?,
    val eventStatus: String?
)
