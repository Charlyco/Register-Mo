package com.register.app.dto

data class CreateFormModel(
    val title: String,
    val groupName: String,
    val groupId: Int?,
    val creationDate: String,
    val questionnaire: List<QuestionnaireEntry>
)

data class QuestionnaireEntry(
    val question: String,
    val options: List<String>
)
