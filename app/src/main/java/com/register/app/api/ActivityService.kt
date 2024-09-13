package com.register.app.api

import com.register.app.dto.BulkPaymentModel
import com.register.app.dto.BulkPaymentWrapper
import com.register.app.dto.CommentReply
import com.register.app.dto.ConfirmBulkPaymentDto
import com.register.app.dto.ConfirmPaymentModel
import com.register.app.dto.CreateEventModel
import com.register.app.dto.EventComment
import com.register.app.dto.EventCommentResponse
import com.register.app.dto.EventDetailWrapper
import com.register.app.dto.GenericResponse
import com.register.app.dto.ImageUploadResponse
import com.register.app.dto.Payment
import com.register.app.dto.RejectBulkPaymentDto
import com.register.app.dto.RejectedPayment
import com.register.app.dto.SpecialLeviesWrapper
import com.register.app.dto.SpecialLevy
import com.register.app.model.Event
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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
    @PUT("event-service/api/v1/event/{eventId}/status")
    fun changeEventStatus(@Path("eventId") eventId: Long, @Query("status") status: String): Call<EventDetailWrapper>
    @DELETE("event-service/api/v1/event/{eventId}")
    fun deleteActivity(@Path("eventId") eventId: Long): Call<GenericResponse>
    @POST("event-service/api/v1/event/payment/bulk")
    fun submitBulkPaymentEvidence(@Body payment: BulkPaymentModel): Call<GenericResponse>
    @GET("event-service/api/v1/event/payment/bulk")
    fun getPendingBulkPayments(@Query("groupId") groupId: Int?): Call<BulkPaymentWrapper>
    @PUT("event-service/api/v1/event/payment/bulk")
    fun confirmBulkPayment(@Body confirmBulkPaymentDto: ConfirmBulkPaymentDto): Call<GenericResponse>
    @PUT("event-service/api/v1/event/payment/bulk/reject")
    fun rejectBulkPayment(@Body rejectedBulkPaymentDto: RejectBulkPaymentDto): Call<GenericResponse>
    @PUT("event-service/api/v1/event/payment/reject")
    fun rejectPayment(@Body rejectedPayment: RejectedPayment): Call<GenericResponse>
    @GET("event-service/api/v1/event/{activityId}/report")
    fun generateReport(@Path("activityId") eventId: Long?): Call<ResponseBody>
    @PUT("event-service/api/v1/event/comment")
    fun postComment(@Body commentModel: EventComment): Call<EventCommentResponse>
    @PUT("event-service/api/v1/event/comment/reply")
    fun postCommentReply(@Body replyModel: CommentReply): Call<EventCommentResponse>
    @GET("event-service/api/v1/event/{eventId}/details")
    fun getEventDetails(@Path("eventId") eventId: Long): Call<Event?>
    @POST("event-service/api/v1/event/specialLevy")
    fun assignSpecialLevy(@Body levy: SpecialLevy): Call<GenericResponse>
    @GET("event-service/api/v1/event/specialLevy")
    fun getSpecialLevies(@Query("payeeEmail") emailAddress: String?): Call<SpecialLeviesWrapper>
    @POST("event-service/api/v1/event/specialLevy/pay")
    fun paySpecialLevy(@Body payment: Payment): Call<GenericResponse>
    @POST("event-service/api/v1/event/specialLevy/payment/confirm")
    fun confirmSpecialLevyPayment(@Body payment: ConfirmPaymentModel): Call<GenericResponse>
    @GET("event-service/api/v1/event/specialLevy/{groupId}")
    fun getAllSpecialLeviesForGroup(@Path("groupId") groupId: Int): Call<List<SpecialLevy>>
    @PUT("event-service/api/v1/event/specialLevy/payment/reject")
    fun rejectSpecialLevyPayment(@Body rejectedPayment: RejectedPayment): Call<GenericResponse>
    @Multipart
    @POST("event-service/api/v1/event/batch/{eventId}")
    fun uploadBatchPaymentRecord(@Path("eventId") eventId: Long, @Query("groupId") groupId: Int, @Part file: MultipartBody.Part): Call<GenericResponse>
}