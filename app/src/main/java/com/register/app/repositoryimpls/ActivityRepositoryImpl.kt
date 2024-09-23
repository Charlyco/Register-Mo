package com.register.app.repositoryimpls

import com.register.app.api.ActivityService
import com.register.app.api.UserService
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
import com.register.app.model.Member
import com.register.app.repository.ActivityRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
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
                    }else{
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
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(ImageUploadResponse("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(ImageUploadResponse("Please check Internet connection and try again", false, null))
                        }
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
                    }else{
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
                    }else{
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

    override suspend fun getMemberDetails(memberEmail: String?): Member? {
        return suspendCoroutine { continuation ->
            val call = userService.getMemberDetails(memberEmail!!)
            call.enqueue(object : Callback<Member?> {
                override fun onResponse(call: Call<Member?>, response: Response<Member?>) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body())
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume( null)
                            }
                            500 -> continuation.resume(  null)
                        }
                    }
                }

                override fun onFailure(call: Call<Member?>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun changeEventStatus(eventId: Long, status: String): EventDetailWrapper {
        return suspendCoroutine { continuation ->
            val call = activityService.changeEventStatus(eventId, status)
            call.enqueue(object : Callback<EventDetailWrapper> {
                override fun onResponse(
                    call: Call<EventDetailWrapper>,
                    response: Response<EventDetailWrapper>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(EventDetailWrapper("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(EventDetailWrapper("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<EventDetailWrapper>, t: Throwable) {
                   continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun deleteActivity(eventId: Long): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = activityService.deleteActivity(eventId)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
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

    override suspend fun submitBulkPaymentEvidence(payment: BulkPaymentModel): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = activityService.submitBulkPaymentEvidence(payment)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
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

    override suspend fun getPendingBulkPayments(groupId: Int?): BulkPaymentWrapper {
        return suspendCoroutine { continuation ->
            val call = activityService.getPendingBulkPayments(groupId)
            call.enqueue(object : Callback<BulkPaymentWrapper> {
                override fun onResponse(
                    call: Call<BulkPaymentWrapper>,
                    response: Response<BulkPaymentWrapper>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(BulkPaymentWrapper("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(BulkPaymentWrapper("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<BulkPaymentWrapper>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun confirmBulkPayment(confirmBulkPaymentDto: ConfirmBulkPaymentDto):
            GenericResponse {
        return suspendCoroutine { continuation ->
            val call = activityService.confirmBulkPayment(confirmBulkPaymentDto)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
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

    override suspend fun rejectBulkPayment(rejectedBulkPaymentDto: RejectBulkPaymentDto): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = activityService.rejectBulkPayment(rejectedBulkPaymentDto)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
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

    override suspend fun rejectPayment(rejectedPayment: RejectedPayment): GenericResponse {
        return suspendCoroutine { continuation ->
            val call  = activityService.rejectPayment(rejectedPayment)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
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

    override suspend fun generateReport(eventId: Long?): ResponseBody? {
        return suspendCoroutine { continuation ->
            val call = activityService.generateReport(eventId)
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

    override suspend fun postComment(commentModel: EventComment): EventCommentResponse {
        return suspendCoroutine { continuation ->
            val call = activityService.postComment(commentModel)
            call.enqueue(object : Callback<EventCommentResponse> {
                override fun onResponse(
                    call: Call<EventCommentResponse>,
                    response: Response<EventCommentResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(EventCommentResponse("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(EventCommentResponse("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<EventCommentResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun postCommentReply(replyModel: CommentReply): EventCommentResponse {
        return suspendCoroutine { continuation ->
            val call = activityService.postCommentReply(replyModel)
            call.enqueue(object : Callback<EventCommentResponse> {
                override fun onResponse(
                    call: Call<EventCommentResponse>,
                    response: Response<EventCommentResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(EventCommentResponse("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(EventCommentResponse("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<EventCommentResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun getEventDetails(eventId: Long): Event? {
        return suspendCoroutine { continuation ->
            val call = activityService.getEventDetails(eventId)
            call.enqueue(object : Callback<Event?> {
                override fun onResponse(call: Call<Event?>, response: Response<Event?>) {
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

                override fun onFailure(call: Call<Event?>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun assignSpecialLevy(levy: SpecialLevy): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = activityService.assignSpecialLevy(levy)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
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

    override suspend fun getSpecialLevies(emailAddress: String?): SpecialLeviesWrapper {
        return suspendCoroutine { continuation ->
            val call = activityService.getSpecialLevies(emailAddress)
            call.enqueue(object : Callback<SpecialLeviesWrapper> {
                override fun onResponse(
                    call: Call<SpecialLeviesWrapper>,
                    response: Response<SpecialLeviesWrapper>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(SpecialLeviesWrapper("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(SpecialLeviesWrapper("Please check Internet connection and try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<SpecialLeviesWrapper>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun submitSpecialLevyPayment(payment: Payment): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = activityService.paySpecialLevy(payment)
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

    override suspend fun getAllSpecialLeviesForGroup(groupId: Int, emailAddress: String): SpecialLeviesWrapper {
        return suspendCoroutine { continuation ->
            val call = activityService.getAllSpecialLeviesForGroup(groupId, emailAddress)
            call.enqueue(object : Callback<SpecialLeviesWrapper> {
                override fun onResponse(
                    call: Call<SpecialLeviesWrapper>,
                    response: Response<SpecialLeviesWrapper>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else {
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(SpecialLeviesWrapper("Invalid Credentials", false, null))
                            }

                            500 -> continuation.resume(SpecialLeviesWrapper("An error occurred", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<SpecialLeviesWrapper>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

    override suspend fun confirmSpecialLevyPayment(payment: ConfirmPaymentModel): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = activityService.confirmSpecialLevyPayment(payment)
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

    override suspend fun rejectSpecialLevyPayment(rejectedPayment: RejectedPayment): GenericResponse {
        return suspendCoroutine { continuation ->
            val call  = activityService.rejectSpecialLevyPayment(rejectedPayment)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
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

    override suspend fun uploadBatchPaymentRecord(
        requestBody: RequestBody?,
        fileName: String?,
        eventId: Long,
        groupId: Int
    ): GenericResponse? {
        return suspendCoroutine { continuation ->
            val file = MultipartBody.Part.createFormData("file", fileName, requestBody!!)
            val call = activityService.uploadBatchPaymentRecord(eventId, groupId, file)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body())
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(GenericResponse("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(GenericResponse("An error has occurred, please try again", false, null))
                        }
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }
}