package com.register.app.repository

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
import okhttp3.RequestBody
import okhttp3.ResponseBody

interface ActivityRepository {
    suspend fun submitEvidenceOfPayment(payment: Payment): GenericResponse
    suspend fun uploadImage(image: RequestBody, name: String): ImageUploadResponse
    suspend fun createNewActivity(newActivity: CreateEventModel): GenericResponse
    suspend fun confirmPayment(contribution: ConfirmPaymentModel): GenericResponse
    suspend fun getMemberDetails(memberEmail: String?): Member?
    suspend fun changeEventStatus(eventId: Long, status: String): EventDetailWrapper
    suspend fun deleteActivity(eventId: Long): GenericResponse
    suspend fun submitBulkPaymentEvidence(payment: BulkPaymentModel): GenericResponse
    suspend fun getPendingBulkPayments(groupId: Int?): BulkPaymentWrapper
    suspend fun confirmBulkPayment(confirmBulkPaymentDto: ConfirmBulkPaymentDto): GenericResponse
    suspend fun rejectBulkPayment(rejectedBulkPaymentDto: RejectBulkPaymentDto): GenericResponse
    suspend fun rejectPayment(rejectedPayment: RejectedPayment): GenericResponse
    suspend fun generateReport(eventId: Long?): ResponseBody?
    suspend fun postComment(commentModel: EventComment): EventCommentResponse
    suspend fun postCommentReply(replyModel: CommentReply): EventCommentResponse
    suspend fun getEventDetails(eventId: Long): Event?
    suspend fun assignSpecialLevy(levy: SpecialLevy): GenericResponse
    suspend fun getSpecialLevies(emailAddress: String?): SpecialLeviesWrapper
    suspend fun submitSpecialLevyPayment(payment: Payment): GenericResponse
    suspend fun getAllSpecialLeviesForGroup(groupId: Int, emailAddress: String): SpecialLeviesWrapper
    suspend fun confirmSpecialLevyPayment(payment: ConfirmPaymentModel): GenericResponse
    suspend fun rejectSpecialLevyPayment(rejectedPayment: RejectedPayment): GenericResponse
    suspend fun uploadBatchPaymentRecord(
        requestBody: RequestBody?,
        fileName: String?,
        eventId: Long,
        groupId: Int
    ): GenericResponse?
}
