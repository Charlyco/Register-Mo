package com.register.app.repositoryimpls

import com.register.app.api.ActivityService
import com.register.app.api.UserService
import com.register.app.dto.ConfirmPaymentModel
import com.register.app.dto.CreateEventModel
import com.register.app.dto.EventDetailWrapper
import com.register.app.dto.GenericResponse
import com.register.app.dto.ImageUploadResponse
import com.register.app.dto.Payment
import com.register.app.model.Event
import com.register.app.model.Member
import com.register.app.repository.ActivityRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ActivityRepositoryImpl @Inject constructor(
    private val activityService: ActivityService,
    private val userService: UserService
): ActivityRepository {

    override suspend fun submitEvidenceOfPayment(payment: Payment): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = activityService.submitEvidenceOfPayment(payment)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun uploadImage(image: RequestBody, name: String): ImageUploadResponse {
        return suspendCoroutine { continuation ->
            val file = MultipartBody.Part.createFormData("file", name, image)
            val call = activityService.uploadImage(file)
            call.enqueue(object : Callback<ImageUploadResponse> {
                override fun onResponse(
                    call: Call<ImageUploadResponse>,
                    response: Response<ImageUploadResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<ImageUploadResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun createNewActivity(newActivity: CreateEventModel): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = activityService.createNewActivity(newActivity)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun confirmPayment(contribution: ConfirmPaymentModel): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = activityService.confirmPayment(contribution)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun getMemberDetails(memberEmail: String?): Member? {
        return suspendCoroutine { continuation ->
            val call = userService.getMemberDetails(memberEmail!!)
            call.enqueue(object : Callback<Member?> {
                override fun onResponse(call: Call<Member?>, response: Response<Member?>) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body())
                    }
                }

                override fun onFailure(call: Call<Member?>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun changeEventStatus(eventId: Int, status: String): EventDetailWrapper {
        return suspendCoroutine { continuation ->
            val call = activityService.changeEventStatus(eventId, status)
            call.enqueue(object : Callback<EventDetailWrapper> {
                override fun onResponse(
                    call: Call<EventDetailWrapper>,
                    response: Response<EventDetailWrapper>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<EventDetailWrapper>, t: Throwable) {
                   continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun deleteActivity(eventId: Int): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = activityService.deleteActivity(eventId)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

}