package com.register.app.dto

data class QuestionnaireWrapper(
    val message: String,
    val status: Boolean,
    val data: List<QuestionnaireData>?

)

data class QuestionnaireData(
    val formId: Int?,
    val title: String?,
    val groupName: String?,
    val groupId: Int?,
    val status: String?,
    val responders: List<String>,
    val questionnaire: List<QuestionnaireEntry>?
)
