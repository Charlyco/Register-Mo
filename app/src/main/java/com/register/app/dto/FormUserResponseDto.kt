package com.register.app.dto

data class FormUserResponseDto(
    val responseId: Int?,
    val fullName: String?,
    val membershipId: String?,
    val submissionTime: String,
    val formId: Int?,
    val formTitle: String?,
    val groupId: Int?,
    val content: List<QuestionnaireResponse>?
)

data class QuestionnaireResponse(
    val question: String,
    val response: String
)

