package com.register.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.register.app.dto.CommentReply
import com.register.app.dto.ConfirmPaymentModel
import com.register.app.dto.CreateEventModel
import com.register.app.dto.EventComment
import com.register.app.dto.EventDetailWrapper
import com.register.app.dto.GenericResponse
import com.register.app.dto.ImageUploadResponse
import com.register.app.dto.Payment
import com.register.app.enums.EventStatus
import com.register.app.model.Event
import com.register.app.model.Member
import com.register.app.repository.ActivityRepository
import com.register.app.util.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val dataStoreManager: DataStoreManager
): ViewModel() {
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
        val payerEmail = dataStoreManager.readUserData()?.emailAddress;
        val payerFullName = dataStoreManager.readUserData()?.fullName
        val payment = Payment(imageUrl!!, membershipId, payerEmail!!, payerFullName!!, eventTitle!!, groupName)
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
            selectedPayment.eventTitle,
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
}