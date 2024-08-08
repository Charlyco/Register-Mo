package com.register.app.repository

import com.register.app.dto.BulkPaymentModel
import com.register.app.dto.BulkPaymentWrapper
import com.register.app.dto.ConfirmBulkPaymentDto
import com.register.app.dto.ConfirmPaymentModel
import com.register.app.dto.CreateEventModel
import com.register.app.dto.EventDetailWrapper
import com.register.app.dto.GenericResponse
import com.register.app.dto.ImageUploadResponse
import com.register.app.dto.Payment
import com.register.app.dto.RejectBulkPaymentDto
import com.register.app.dto.RejectedPayment
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
    suspend fun changeEventStatus(eventId: Int, status: String): EventDetailWrapper
    suspend fun deleteActivity(eventId: Int): GenericResponse
    suspend fun submitBulkPaymentEvidence(payment: BulkPaymentModel): GenericResponse
    suspend fun getPendingBulkPayments(groupId: Int?): BulkPaymentWrapper
    suspend fun confirmBulkPayment(confirmBulkPaymentDto: ConfirmBulkPaymentDto): GenericResponse
    suspend fun rejectBulkPayment(rejectedBulkPaymentDto: RejectBulkPaymentDto): GenericResponse
    suspend fun rejectPayment(rejectedPayment: RejectedPayment): GenericResponse
    suspend fun generateReport(eventId: Int?): ResponseBody
}
