package com.register.app.api

import com.register.app.dto.AllResponsesWrapper
import com.register.app.dto.CreateFormModel
import com.register.app.dto.FormUserResponseDto
import com.register.app.dto.GenericResponse
import com.register.app.dto.QuestionnaireWrapper
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface QuestionnaireService {
    @POST("form-service/api/v1/")
    fun publishQuestionnaire(@Body questionnaireModel: CreateFormModel): Call<GenericResponse>
    @GET("form-service/api/v1/{groupId}")
    fun getQuestionnaires(@Path("groupId") groupId: Int): Call<QuestionnaireWrapper>
    @POST("form-service/api/v1/response")
    fun submitQuestionnaireResponse(@Body userResponseModel: FormUserResponseDto): Call<GenericResponse>
    @DELETE("form-service/api/v1/{formId}")
    fun deleteQuestionnaire(@Path("formId") formId: Int): Call<GenericResponse>
    @PUT("form-service/api/v1/{formId}/status")
    fun endQuestionnaire(@Path("formId") formId: Int, @Query("status") status: String): Call<GenericResponse>
    @GET("form-service/api/v1/responses/{formId}/{responseId}")
    fun downloadResponse(@Path("formId") formId: Int, @Path("responseId") responseId: Int): Call<ResponseBody>
    @GET("form-service/api/v1/{formId}/responses")
    fun getUserResponses(@Path("formId") formId: Int): Call<AllResponsesWrapper>
}