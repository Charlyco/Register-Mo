package com.register.app.viewmodel

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.register.app.dto.BulkPaymentModel
import com.register.app.dto.CommentReply
import com.register.app.dto.ConfirmBulkPaymentDto
import com.register.app.dto.ConfirmPaymentModel
import com.register.app.dto.CreateEventModel
import com.register.app.dto.EventComment
import com.register.app.dto.EventDetailWrapper
import com.register.app.dto.EventItemDto
import com.register.app.dto.GenericResponse
import com.register.app.dto.ImageUploadResponse
import com.register.app.dto.Payment
import com.register.app.dto.RejectBulkPaymentDto
import com.register.app.dto.RejectedPayment
import com.register.app.enums.EventStatus
import com.register.app.model.Event
import com.register.app.model.Member
import com.register.app.repository.ActivityRepository
import com.register.app.util.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val dataStoreManager: DataStoreManager
): ViewModel() {
    private val _pendingBulkPayments: MutableLiveData<List<BulkPaymentModel>?> = MutableLiveData(listOf())
    val pendingBulkPayments: LiveData<List<BulkPaymentModel>?> = _pendingBulkPayments
    private val _bulkPaymentSelection: MutableLiveData<List<Event>> = MutableLiveData(listOf())
    val bulkPaymentSelection: LiveData<List<Event>> = _bulkPaymentSelection
    private val _fileName: MutableLiveData<String?> = MutableLiveData()
    val fileName: LiveData<String?> = _fileName
    private val _paymentEvidence: MutableLiveData<String?> = MutableLiveData()
    val paymentEvidence: LiveData<String?> = _paymentEvidence
    private val _selectedEvent: MutableLiveData<Event> = MutableLiveData()
    val selectedEvent: LiveData<Event> = _selectedEvent
    private val _loadingState: MutableLiveData<Boolean> = MutableLiveData()
    val loadingState: LiveData<Boolean> = _loadingState
    private val _activityImages: MutableLiveData<MutableList<String>?> = MutableLiveData(mutableListOf())
    val activityImageList: LiveData<MutableList<String>?> = _activityImages
    private val _unapprovedPayments: MutableLiveData<List<Payment>?> = MutableLiveData()
    val unapprovedPayments: LiveData<List<Payment>?> = _unapprovedPayments
    private val _hasUserPaid: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasPaid: LiveData<Boolean> = _hasUserPaid
    private val _paidMembersList: MutableLiveData<List<Member>?> = MutableLiveData(listOf())
    val paidMembersList: LiveData<List<Member>?> = _paidMembersList


    suspend fun setSelectedEvent(eventFeed: Event) {
        _loadingState.value = true
        _selectedEvent.value = eventFeed
        if (eventFeed.contributions?.any { contributionDto ->
            contributionDto.memberEmail == dataStoreManager.readUserData()?.emailAddress } == true) {
            _hasUserPaid.value = true
        }
        viewModelScope.launch {
            getMembershipIdByGroupId(eventFeed.groupId!!)
            val paidMembers = paidMembersList.value?.toMutableList()
            eventFeed.contributions?.forEach { contributionDto ->
                val member = activityRepository.getMemberDetails(contributionDto.memberEmail)
                paidMembers?.add(member!!)
            }
            _paidMembersList.value = paidMembers
            _loadingState.value = false
        }
    }

    private fun getMembershipIdByGroupId(groupId: Int) {

    }

    fun postCommentReply(commentReply: String, eventCommentId: Int): CommentReply? {
        return null
    }

    fun postComment(comment: String, eventId: Int?): EventComment? {
        return null
    }

    suspend fun submitEvidenceOfPayment(groupName: String, membershipId: String): GenericResponse {
        val imageUrl = paymentEvidence.value
        val eventTitle = selectedEvent.value?.eventTitle
        val eventId = selectedEvent.value?.eventId
        val payerEmail = dataStoreManager.readUserData()?.emailAddress;
        val payerFullName = dataStoreManager.readUserData()?.fullName
        val payment = Payment(imageUrl!!, membershipId, payerEmail!!, payerFullName!!, eventTitle!!, eventId!!, groupName)
        _loadingState.value = true
        val response = activityRepository.submitEvidenceOfPayment(payment)
        _loadingState.value = false
        return response
    }
    suspend fun uploadActivityImages(
        inputStream: InputStream,
        mimeType: String?,
        fileNameFromUri: String?) {
        _loadingState.value = true
        val requestBody = inputStream.readBytes().toRequestBody(mimeType?.toMediaTypeOrNull())
        val response: ImageUploadResponse = activityRepository.uploadImage(requestBody, fileNameFromUri!!)
        val images = activityImageList.value
        images?.add(response.data.secureUrl)
        _activityImages.value = images
        _loadingState.value = false
    }

    suspend fun createNewActivity(
        activityTitle: String,
        activityDescription: String,
        levyAmount: Double,
        eventDate: String,
        groupName: String,
        groupId: Int,
        eventType: String,
    ): GenericResponse {
        _loadingState.value = true
        val newActivity = CreateEventModel(
            activityTitle,
            activityDescription,
            LocalDateTime.now().toString(),
            eventDate,
            activityImageList.value,
            dataStoreManager.readUserData()?.fullName,
            groupName,
            groupId,
            levyAmount,
            EventStatus.CURRENT.name,
            eventType)
        val response = activityRepository.createNewActivity(newActivity)
        _loadingState.value = false
        return response
    }

    suspend fun confirmPayment(
        selectedPayment: Payment?,
        amountPaid: Double,
        outstanding: Double,
        groupId: Int,
        paymentMethod: String
    ): GenericResponse {
        _loadingState.value = true
        val contribution = ConfirmPaymentModel(
            selectedPayment?.membershipId!!,
            selectedPayment.payerEmail,
            selectedPayment.payerFullName,
            selectedPayment.eventTitle,
            selectedPayment.eventId,
            groupId,
            selectedPayment.groupName,
            amountPaid,
            outstanding,
            paymentMethod,
            dataStoreManager.readUserData()?.fullName!!
            )
        val response = activityRepository.confirmPayment(contribution)
        _loadingState.value = false
        return response
    }

    suspend fun uploadEvidenceOfPayment(
        inputStream: InputStream,
        mimeType: String?,
        fileNameFromUri: String?
    ) {
        _loadingState.value = true
        val requestBody = inputStream.readBytes().toRequestBody(mimeType?.toMediaTypeOrNull())
        val response: ImageUploadResponse = activityRepository.uploadImage(requestBody, fileNameFromUri!!)
        _paymentEvidence.value = response.data.secureUrl
        _fileName.value = fileNameFromUri
        _loadingState.value = false
    }

    suspend fun markActivityCompleted(event: Event): EventDetailWrapper {
        _loadingState.value = true
        val response = activityRepository.changeEventStatus(event.eventId, EventStatus.COMPLETED.name)
        _loadingState.value = false
        setSelectedEvent(event)
        return response
    }

    suspend fun archiveActivity(event: Event) : EventDetailWrapper {
        _loadingState.value = true
        val response = activityRepository.changeEventStatus(event.eventId, EventStatus.ARCHIVED.name)
        _loadingState.value = false
        setSelectedEvent(event)
        return response
    }

    suspend fun deleteActivity(event: Event) : GenericResponse{
        _loadingState.value = true
        val response = activityRepository.deleteActivity(event.eventId)
        _loadingState.value = false
        return response
    }

    fun setBulkPaymentSelection(selectedEvents: MutableList<Event>) {
        _bulkPaymentSelection.value = selectedEvents
    }

    suspend fun submitBulkPaymentEvidence(
        groupName: String,
        groupId: Int,
        membershipId: String,
        totalAmount: Double
    ): GenericResponse {
        val eventList = mutableSetOf<EventItemDto>()
        bulkPaymentSelection.value?.forEach { item ->
            eventList.add(EventItemDto(item.eventTitle, item.eventId, item.levyAmount?: 0.0))
        }

        val imageUrl = paymentEvidence.value
        val payerEmail = dataStoreManager.readUserData()?.emailAddress;
        val payerFullName = dataStoreManager.readUserData()?.fullName
        val payment = BulkPaymentModel(null, imageUrl!!, eventList, membershipId, payerEmail!!, payerFullName!!, groupName, groupId, totalAmount)
        _loadingState.value = true
        val response = activityRepository.submitBulkPaymentEvidence(payment)
        _loadingState.value = false
        return response
    }

    suspend fun getBulkPayments(groupId: Int?) {
        _loadingState.value = true
            val response = activityRepository.getPendingBulkPayments(groupId)
            _pendingBulkPayments.value = response.data
            _loadingState.value = false
    }

    suspend fun confirmBulkPayment(selectedPayment: BulkPaymentModel?, paymentMethod: String): GenericResponse {
        val eventList = mutableListOf<EventItemDto>()
        selectedPayment?.eventItemDtos?.let { eventList.addAll(it) }
        val confirmPaymentModel = ConfirmBulkPaymentDto(
            selectedPayment?.id!!,
            selectedPayment.membershipId,
            selectedPayment.payerEmail,
            selectedPayment.payerFullName,
            eventList,
            selectedPayment.groupId,
            selectedPayment.groupName,
            0.0,
            paymentMethod,
            dataStoreManager.readUserData()?.fullName!!)
        _loadingState.value = true
        val response = activityRepository.confirmBulkPayment(confirmPaymentModel)
        return response
    }

    suspend fun rejectBulkPayment(selectedPayment: BulkPaymentModel?, reason: String): GenericResponse {
        val rejectedBulkPayment = RejectBulkPaymentDto(
            selectedPayment?.id!!,
            selectedPayment.imageUrl,
            selectedPayment.membershipId,
            selectedPayment.payerFullName,
            selectedPayment.groupName,
            selectedPayment.groupId,
            dataStoreManager.readUserData()?.fullName!!,
            reason)
        _loadingState.value = true
        val response =  activityRepository.rejectBulkPayment(rejectedBulkPayment)
        _loadingState.value = false
        return response
    }

    suspend fun rejectPayment(selectedPayment: Payment?, reason: String): GenericResponse {
        val rejectedPayment = RejectedPayment(
            selectedPayment?.eventTitle,
            selectedPayment?.eventId,
            selectedPayment?.imageUrl,
            selectedPayment?.membershipId,
            selectedPayment?.payerFullName,
            selectedPayment?.groupName,
            dataStoreManager.readUserData()?.fullName!!,
            reason
        )
        _loadingState.value = true
        val response = activityRepository.rejectPayment(rejectedPayment)
        _loadingState.value = false
        return response
    }

    suspend fun generateReport(event: Event?, context: Context) {
        try {
            _loadingState.value = true
            val response = activityRepository.generateReport(event?.eventId)
            val pdfBytes = response.byteStream()
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "report${LocalDateTime.now()}.pdf")
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
            }
            val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
            val outputStream = context.contentResolver.openOutputStream(uri!!)
            val buffer = ByteArray(1024)
            var read: Int
            withContext(Dispatchers.IO) {
                while (pdfBytes.read(buffer).also { read = it } != -1) {
                    outputStream!!.write(buffer, 0, read)
                }
                outputStream!!.flush()
                outputStream.close()
            }
            _loadingState.value = false

            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("Activity report generated")
            dialog.setMessage("Report saved successfully. Do you want to open or share it?")
            dialog.setPositiveButton("Open") { _, _ ->
                // Open file
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "application/pdf")
                startActivity(context, intent, null)
            }
            dialog.setNegativeButton("Share") { _, _ ->
                // Share file
                val intent = Intent(Intent.ACTION_SEND)
                intent.setType("application/pdf")
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(context, Intent.createChooser(intent, "Share file"), null)
            }
            dialog.setNeutralButton("Cancel") { _, _ -> }
            dialog.show()
        }catch (e: Exception) {
            Toast.makeText(context, "Error saving file: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}