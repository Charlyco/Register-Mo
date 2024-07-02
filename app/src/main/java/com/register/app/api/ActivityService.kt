package com.register.app.api

import android.app.Activity
import com.register.app.dto.ConfirmPaymentModel
import com.register.app.dto.CreateEventModel
import com.register.app.dto.GenericResponse
import com.register.app.dto.ImageUploadResponse
import com.register.app.dto.Payment
import com.register.app.dto.RemoveMemberModel
import com.register.app.model.Event
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ActivityService {
    @GET("event-service/api/v1/event/{groupId}")
    fun getAllActivitiesForGroup(@Path("groupId") groupId: Int): Call<List<Event>?>
    @POST("event-service/api/v1/event/")
    fun createNewActivity(@Body newActivity: CreateEventModel): Call<GenericResponse>

    @Multipart
    @POST("event-service/api/v1/event/image/upload")
    fun uploadImage(@Part file: MultipartBody.Part): Call<ImageUploadResponse>
    @POST("event-service/api/v1/event/payment/evidence")
    fun submitEvidenceOfPayment(@Body payment: Payment): Call<GenericResponse>
    @POST("event-service/api/v1/event/payment/confirm")
    fun confirmPayment(@Body payment: ConfirmPaymentModel): Call<GenericResponse>
}