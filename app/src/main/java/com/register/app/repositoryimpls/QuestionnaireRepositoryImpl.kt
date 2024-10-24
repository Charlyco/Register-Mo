package com.register.app.repositoryimpls

import com.register.app.api.QuestionnaireService
import com.register.app.dto.AllResponsesWrapper
import com.register.app.dto.CreateFormModel
import com.register.app.dto.FormUserResponseDto
import com.register.app.dto.GenericResponse
import com.register.app.dto.QuestionnaireWrapper
import com.register.app.repository.QuestionnaireRepository
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class QuestionnaireRepositoryImpl @Inject constructor(
    private val questionnaireService: QuestionnaireService
): QuestionnaireRepository {

    override suspend fun postQuestionnaire(questionnaireModel: CreateFormModel): GenericResponse? {
        return suspendCoroutine { continuation ->
            val call = questionnaireService.publishQuestionnaire(questionnaireModel)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else {
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(GenericResponse("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(GenericResponse("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun getQuestionnaires(groupId: Int): QuestionnaireWrapper {
        return suspendCoroutine { continuation ->
            val call = questionnaireService.getQuestionnaires(groupId)
            call.enqueue(object : Callback<QuestionnaireWrapper> {
                override fun onResponse(
                    call: Call<QuestionnaireWrapper>,
                    response: Response<QuestionnaireWrapper>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else {
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(QuestionnaireWrapper("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(QuestionnaireWrapper("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<QuestionnaireWrapper>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun submitQuestionnaireResponse(userResponseModel: FormUserResponseDto): GenericResponse? {
        return suspendCoroutine { continuation ->
            val call = questionnaireService.submitQuestionnaireResponse(userResponseModel)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else {
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(
                                    GenericResponse(
                                        "Invalid Credentials",
                                        false,
                                        null
                                    )
                                )
                            }

                            500 -> continuation.resume(
                                GenericResponse(
                                    "Please check Internet connection and try again",
                                    false,
                                    null
                                )
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun deleteQuestionnaire(formId: Int): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = questionnaireService.deleteQuestionnaire(formId)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else {
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(
                                    GenericResponse(
                                        "Invalid Credentials",
                                        false,
                                        null
                                    )
                                )
                            }

                            500 -> continuation.resume(
                                GenericResponse(
                                    "Please check Internet connection and try again",
                                    false,
                                    null
                                )
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun endQuestionnaire(formId: Int, status: String): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = questionnaireService.endQuestionnaire(formId, status)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else {
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(
                                    GenericResponse(
                                        "Invalid Credentials",
                                        false,
                                        null
                                    )
                                )
                            }

                            500 -> continuation.resume(
                                GenericResponse(
                                    "Please check Internet connection and try again",
                                    false,
                                    null
                                )
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun downloadResponse(formId: Int, responseId: Int): ResponseBody? {
       return suspendCoroutine { continuation ->
           val call = questionnaireService.downloadResponse(formId, responseId)
           call.enqueue(object : Callback<ResponseBody> {
               override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                   if (response.isSuccessful) {
                       continuation.resume(response.body()!!)
                   }else{
                       val responseCode = response.code()
                       when (responseCode) {
                           401 -> {
                               continuation.resume(null)
                           }
                           500 -> continuation.resume( null)
                       }
                   }
               }

               override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                   continuation.resumeWithException(t)
               }

           })
       }
    }

    override suspend fun getUserResponses(formId: Int): AllResponsesWrapper? {
        return suspendCoroutine { continuation ->
            val call = questionnaireService.getUserResponses(formId)
            call.enqueue(object : Callback<AllResponsesWrapper> {
                override fun onResponse(
                    call: Call<AllResponsesWrapper>,
                    response: Response<AllResponsesWrapper>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body())
                    }else {
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(
                                    AllResponsesWrapper(
                                        "Invalid Credentials",
                                        false,
                                        null
                                    )
                                )
                            }

                            500 -> continuation.resume(
                                AllResponsesWrapper(
                                    "Please check Internet connection and try again",
                                    false,
                                    null
                                )
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<AllResponsesWrapper>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun downloadSummery(formId: Int?): ResponseBody? {
        return suspendCoroutine { continuation ->
            val call = questionnaireService.downloadSummery(formId)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body())
                    }else continuation.resume(null)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }
}