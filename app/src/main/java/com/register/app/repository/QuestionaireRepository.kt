package com.register.app.repository

import com.register.app.dto.AllResponsesWrapper
import com.register.app.dto.CreateFormModel
import com.register.app.dto.FormUserResponseDto
import com.register.app.dto.GenericResponse
import com.register.app.dto.QuestionnaireWrapper
import okhttp3.ResponseBody

interface QuestionnaireRepository {
    suspend fun postQuestionnaire(questionnaireModel: CreateFormModel): GenericResponse?
    suspend fun getQuestionnaires(groupId: Int): QuestionnaireWrapper
    suspend fun submitQuestionnaireResponse(userResponseModel: FormUserResponseDto): GenericResponse?
    suspend fun deleteQuestionnaire(formId: Int): GenericResponse
    suspend fun endQuestionnaire(formId: Int, status: String): GenericResponse
    suspend fun downloadResponse(formId: Int, responseId: Int): ResponseBody?
    suspend fun getUserResponses(formId: Int): AllResponsesWrapper?
}
