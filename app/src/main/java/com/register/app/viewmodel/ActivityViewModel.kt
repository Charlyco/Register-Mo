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
import co.yml.charts.common.extensions.isNotNull
import com.register.app.dto.BulkPaymentModel
import com.register.app.dto.CommentReply
import com.register.app.dto.ConfirmBulkPaymentDto
import com.register.app.dto.ConfirmPaymentModel
import com.register.app.dto.CreateEventModel
import com.register.app.dto.EventComment
import com.register.app.dto.EventDetailWrapper
import com.register.app.dto.EventItemDto
import com.register.app.dto.GenericResponse
import com.register.app.dto.GroupUserEventsResponse
import com.register.app.dto.ImageUploadResponse
import com.register.app.dto.Payment
import com.register.app.dto.RateData
import com.register.app.dto.RejectBulkPaymentDto
import com.register.app.dto.RejectedPayment
import com.register.app.dto.SpecialLevy
import com.register.app.enums.EventStatus
import com.register.app.enums.EventType
import com.register.app.model.Event
import com.register.app.model.Group
import com.register.app.model.Member
import com.register.app.model.MembershipDto
import com.register.app.repository.ActivityRepository
import com.register.app.repository.GroupRepository
import com.register.app.util.AN_ERROR_OCCURRED
import com.register.app.util.DataStoreManager
import com.register.app.util.PAID
import com.register.app.util.UNPAID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.InputStream
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val groupRepository: GroupRepository,
    private val dataStoreManager: DataStoreManager
): ViewModel() {
    private val _allGroupSpecialLevies: MutableLiveData<List<SpecialLevy>> = MutableLiveData()
    val allGroupSpecialLevies: LiveData<List<SpecialLevy>> = _allGroupSpecialLevies
    private val _pendingBulkPayments: MutableLiveData<List<BulkPaymentModel>?> = MutableLiveData(listOf())
    val pendingBulkPayments: LiveData<List<BulkPaymentModel>?> = _pendingBulkPayments
    private val _bulkPaymentSelection: MutableLiveData<List<Event>> = MutableLiveData(listOf())
    val bulkPaymentSelection: LiveData<List<Event>> = _bulkPaymentSelection
    private val _fileName: MutableLiveData<String?> = MutableLiveData()
    val fileName: LiveData<String?> = _fileName
    private val _paymentEvidence: MutableLiveData<String?> = MutableLiveData()
    val paymentEvidence: LiveData<String?> = _paymentEvidence
    private val _selectedEvent: MutableLiveData<Event?> = MutableLiveData()
    val selectedEvent: LiveData<Event?> = _selectedEvent
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
    private val _eventFeeds: MutableLiveData<List<Event>?> = MutableLiveData()
    val eventFeeds: LiveData<List<Event>?> = _eventFeeds
    private val _memberUnpaidSpecialLevyList: MutableLiveData<List<SpecialLevy>?> = MutableLiveData()
    val memberUnpaidSpecialLevyList: LiveData<List<SpecialLevy>?> = _memberUnpaidSpecialLevyList
    private val _memberPaidSpecialLevyList: MutableLiveData<List<SpecialLevy>?> = MutableLiveData()
    val memberPaidSpecialLevyList: LiveData<List<SpecialLevy>?> = _memberPaidSpecialLevyList
    private val _unpaidSpecialLevyList: MutableLiveData<List<SpecialLevy>?> = MutableLiveData()
    val unpaidSpecialLevyList: LiveData<List<SpecialLevy>?> = _unpaidSpecialLevyList
    private val _paidSpecialLevyList: MutableLiveData<List<SpecialLevy>?> = MutableLiveData()
    val paidSpecialLevyList: LiveData<List<SpecialLevy>?> = _paidSpecialLevyList
    private val _errorLiveData: MutableLiveData<String?> = MutableLiveData()
    val errorLiveData: LiveData<String?> = _errorLiveData
    private val _paidActivities: MutableLiveData<List<Event>?> = MutableLiveData()
    val paidActivities: LiveData<List<Event>?> = _paidActivities
    private val _unpaidActivities: MutableLiveData<List<Event>?> = MutableLiveData()
    val unpaidActivities: LiveData<List<Event>?> = _unpaidActivities
    private val _paymentRateLiveData: MutableLiveData<RateData?> = MutableLiveData()
    val paymentRateLiveData: LiveData<RateData?> = _paymentRateLiveData
    private val _activityRateLiveData: MutableLiveData<Float?> = MutableLiveData(100.0f)
    val activityRateLiveData: LiveData<Float?> = _activityRateLiveData
    private val _groupEvents: MutableLiveData<GroupUserEventsResponse?> = MutableLiveData()
    val groupEvents: LiveData<GroupUserEventsResponse?> = _groupEvents
    private val _selectedSpecialLevy: MutableLiveData<SpecialLevy?> = MutableLiveData()
    val selectedSpecialLevy: LiveData<SpecialLevy?> = _selectedSpecialLevy


    init {
        viewModelScope.launch {
            getEventFeeds()

        }
    }

    suspend fun getActivitiesForGroup(group: Group, membershipId: String?, joinedDateTime: String) {
        val member = getMember(group.memberList)
        // Get all events for for group and filter into paid and unpaid for user
        _groupEvents.value = activityRepository.getAllActivitiesForGroup(group.groupId, membershipId!!, joinedDateTime)
        Log.d("EVENTS", groupEvents.value.toString())
        //get user activity rate
        getActivityRate(member?.membershipId, member?.joinedDateTime, group.groupId)
    }

    fun populateActivities(type: String, userEmail: String?) {
        when (type) {
            PAID -> {
                Log.d("EVENTS", groupEvents.value.toString())
                _paidActivities.value = groupEvents.value?.paidEvents
            }

            UNPAID -> {
                Log.d("EVENTS", groupEvents.value.toString())
                _unpaidActivities.value = groupEvents.value?.unpaidEvents
            }

            else -> {}
        }
    }

    suspend fun getActivityRate(membershipId: String?, joinedDateTime: String?, groupId: Int) {
        val activityRate = activityRepository.getMemberActivityRate(
            membershipId, joinedDateTime, groupId
        ).data
        _paymentRateLiveData.value = activityRate
        _activityRateLiveData.value = activityRate?.let { calculateActivityRate(it) }
    }

    private fun calculateActivityRate(activityRate: RateData): Float {
        return if (activityRate.eventsDue > 0) {
            ((activityRate.eventsPaid / activityRate.eventsDue) * 100).toFloat()
        } else {
            0.0f
        }
    }

    private suspend fun getMember(memberList: List<MembershipDto>?): MembershipDto? {
        return memberList?.find { it.emailAddress == dataStoreManager.readUserData()?.emailAddress }
    }

    private suspend fun getEventFeeds() {
        val userGroups = dataStoreManager.readUserData()?.groupIds
        val tempList = mutableListOf<Event>()
        _loadingState.value = true
        userGroups?.forEach { groupId ->
            val groupResponse = groupRepository.getGroupDetails(groupId)
            val member = groupResponse?.data?.memberList?.find { it.emailAddress ==
                    dataStoreManager.readUserData()?.emailAddress }
            if (member != null) {
                val activities = activityRepository.getAllActivitiesForGroup(groupId, member.membershipId,
                    member.joinedDateTime)
                activities?.unpaidEvents?.let { tempList.addAll(it) }
            }
        }
        _eventFeeds.value = tempList
        // get special levies if any
        val specialLevyResponse =
            activityRepository.getSpecialLevies(dataStoreManager.readUserData()?.emailAddress).data
        val unpaid = specialLevyResponse?.filter { levy ->
            levy.confirmedPayments?.none { it.memberEmail == dataStoreManager.readUserData()?.emailAddress } == true
        }
        _unpaidSpecialLevyList.value = unpaid
        _loadingState.value = false
    }

    suspend fun getEventDetails(eventId: Long) {
        _loadingState.value = true
        val event = activityRepository.getEventDetails(eventId)
        if (event != null) {
            setSelectedEvent(event)
        }
    }

    fun refreshHomeContents() {
        viewModelScope.launch {
            getEventFeeds()
        }
    }


    suspend fun setSelectedEvent(eventFeed: Event) {
        _selectedEvent.value = eventFeed
        _hasUserPaid.value = eventFeed.contributions?.any { contributionDto ->
            contributionDto.memberEmail == dataStoreManager.readUserData()?.emailAddress }

        viewModelScope.launch {
            getMembershipIdByGroupId(eventFeed.groupId!!)
            val paidMembers = mutableListOf<Member>()
            eventFeed.contributions?.forEach { contributionDto ->
                _loadingState.value = true
                val member = activityRepository.getMemberDetails(contributionDto.memberEmail)
                if (member != null) {
                    paidMembers.add(member)
                }else _errorLiveData.value = AN_ERROR_OCCURRED
            }
            _paidMembersList.value = paidMembers
            _loadingState.value = false
        }
    }

    private fun getMembershipIdByGroupId(groupId: Int) {

    }

    suspend fun postCommentReply(commentReply: String, eventCommentId: Int) {
        val replyModel = CommentReply(
            null,
            dataStoreManager.readUserData()?.fullName,
            LocalDateTime.now().toString(),
            commentReply,
            eventCommentId)
        _loadingState.value = true
        val response = activityRepository.postCommentReply(replyModel)
        _loadingState.value = false
        if (response.status) {
            _selectedEvent.value = response.data
        }else _errorLiveData.value = AN_ERROR_OCCURRED
    }

    suspend fun postComment(comment: String, eventId: Long?) {
        val user = dataStoreManager.readUserData()?.fullName
        val commentModel = EventComment(null, user, LocalDateTime.now().toString(), comment, null, eventId)
        _loadingState.value = true
        val response = activityRepository.postComment(commentModel)
        _loadingState.value = false
        if (response.status) {
            _selectedEvent.value = response.data!!
        }else _errorLiveData.value = AN_ERROR_OCCURRED
    }

    suspend fun submitEvidenceOfPayment(
        groupName: String,
        groupId: Int,
        membershipId: String,
        modeOfPayment: String,
        amountPaid: String? = null
    ): GenericResponse {
        val imageUrl = paymentEvidence.value
        val eventTitle = selectedEvent.value?.eventTitle
        val eventId = selectedEvent.value?.eventId
        val payerEmail = dataStoreManager.readUserData()?.emailAddress;
        val payerFullName = dataStoreManager.readUserData()?.fullName
        val payment = Payment(
            imageUrl?: "",
            membershipId,
            payerEmail!!,
            payerFullName!!,
            eventTitle!!,
            eventId!!,
            modeOfPayment,
            groupName,
            groupId,
            amountPaid?.toDouble())
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
        images?.add(response.data?.secureUrl?: "")
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
        _activityImages.value = mutableListOf()
        _loadingState.value = false
        return response
    }

    suspend fun confirmPayment(
        selectedPayment: Payment?,
        outstanding: Double,
        groupId: Int,
    ): GenericResponse {
        _loadingState.value = true
        val contribution = ConfirmPaymentModel(
            selectedPayment?.membershipId!!,
            selectedPayment.payerEmail,
            selectedPayment.payerFullName,
            selectedPayment.eventTitle,
            selectedPayment.eventId!!,
            groupId,
            selectedPayment.groupName,
            selectedPayment.amountPaid!!,
            outstanding,
            selectedPayment.modeOfPayment,
            dataStoreManager.readUserData()?.fullName!!
            )
        val response = activityRepository.confirmPayment(contribution)
        if (response.status) {
            getEventDetails(selectedPayment.eventId)
        }
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
        _loadingState.value = false
        if (response.status) {
            _paymentEvidence.value = response.data?.secureUrl
            _fileName.value = fileNameFromUri
        }else _errorLiveData.value = AN_ERROR_OCCURRED
    }

    suspend fun markActivityCompleted(event: Event): EventDetailWrapper {
        _loadingState.value = true
        val response = activityRepository.changeEventStatus(event.eventId, EventStatus.COMPLETED.name)
        if (response.status) {
            getEventDetails(event.eventId)
        }
        _loadingState.value = false
        return response
    }

    suspend fun archiveActivity(event: Event) : EventDetailWrapper {
        _loadingState.value = true
        val response = activityRepository.changeEventStatus(event.eventId, EventStatus.ARCHIVED.name)
        if (response.status) {
            getEventDetails(event.eventId)
        }
        _loadingState.value = false
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
        totalAmount: Double,
        modeOfPayment: String,
        imageUrl: String?
    ): GenericResponse {
        val eventList = mutableSetOf<EventItemDto>()
        bulkPaymentSelection.value?.forEach { item ->
            eventList.add(EventItemDto(item.eventTitle, item.eventId, item.levyAmount?: 0.0))
        }

        val payerEmail = dataStoreManager.readUserData()?.emailAddress;
        val payerFullName = dataStoreManager.readUserData()?.fullName
        val payment = BulkPaymentModel(null, imageUrl!!, eventList, membershipId, payerEmail!!,
            payerFullName!!, groupName, groupId, totalAmount, modeOfPayment)
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
        _loadingState.value = false
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
        if (response.status) {
            getEventDetails(selectedPayment?.eventId!!)
        }
        _loadingState.value = false
        return response
    }

    suspend fun generateReport(event: Event?, context: Context) {
        try {
            _loadingState.value = true
            val response = activityRepository.generateReport(event?.eventId)
            if (response != null) {
                val pdfBytes = response.byteStream()
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "report${LocalDateTime.now()}.pdf")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
                }
                val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                val outputStream = context.contentResolver.openOutputStream(uri!!)
                val buffer = ByteArray(4096)
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
            }else {
                _loadingState.value = false
            _errorLiveData.value = AN_ERROR_OCCURRED
            }
        }catch (e: Exception) {
            Toast.makeText(context, "Error saving file: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    suspend fun assignSpecialLevy(
        levyTitle: String,
        levyDescription: String,
        amount: Double,
        group: Group?,
        selectedMember: Member?
    ): GenericResponse {
        val membershipId = group?.memberList?.find { it.emailAddress == selectedMember?.emailAddress }?.membershipId
        val levy = SpecialLevy(
            null,
            levyTitle,
            levyDescription,
            LocalDateTime.now().toString(),
            dataStoreManager.readUserData()?.fullName,
            group?.groupId,
            group?.groupName,
            amount,
            selectedMember?.emailAddress,
            membershipId,
            listOf(),
            listOf()
        )
        _loadingState.value = true
        val response = activityRepository.assignSpecialLevy(levy)
        _loadingState.value = false
        return response
    }

    fun setSelectedSpecialLevy(levy: SpecialLevy) {
        _selectedSpecialLevy.value = levy
    }

    suspend fun paySpecialLevy(
        specialLevy: SpecialLevy?,
        modeOfPayment: String,
        amountPaid: String
    ): GenericResponse {
        val imageUrl = paymentEvidence.value
        val fullName = dataStoreManager.readUserData()?.fullName
        val payment = Payment(
            imageUrl?: "",
            specialLevy?.payeeMembershipId!!,
            specialLevy.payeeEmail!!,
            fullName!!,
            specialLevy.levyTitle!!,
            specialLevy.id,
            modeOfPayment,
            specialLevy.groupName!!,
            specialLevy.groupId!!,
            amountPaid.toDouble())

        _loadingState.value = true
        val response = activityRepository.submitSpecialLevyPayment(payment)
        _loadingState.value = false
        return response
    }

    suspend fun uploadEvidenceOfPaymentFromCamera(jpegImage: File?) {
        _loadingState.value = true
        val requestBody = jpegImage?.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        Log.d("PHOTO", requestBody.toString())
        val response: ImageUploadResponse = activityRepository.uploadImage(requestBody!!, "file")
        _loadingState.value = false
        if (response.status) {
            _paymentEvidence.value = response.data?.secureUrl
        }else _errorLiveData.value = AN_ERROR_OCCURRED
    }

    suspend fun getAllSpecialLeviesForGroup(groupId: Int, emailAddress: String) {
        // get special levies if any
        _loadingState.value = true
        val specialLevyResponse =
            activityRepository.getAllSpecialLeviesForGroup(groupId, emailAddress).data
        val paid = specialLevyResponse?.filter { levy ->
            levy.confirmedPayments?.any { it.memberEmail == emailAddress } == true
        }
        val unpaid = specialLevyResponse?.filter { levy ->
            levy.confirmedPayments?.none { it.memberEmail == emailAddress } == true
        }
        _memberUnpaidSpecialLevyList.value = unpaid
        _memberPaidSpecialLevyList.value = paid
        _loadingState.value = false
    }

    suspend fun confirmSpecialLevyPayment(
        selectedPayment: Payment?,
        amountPaid: Double,
        outstanding: Double,
        groupId: Int,
    ): GenericResponse {
        _loadingState.value = true
        val contribution = ConfirmPaymentModel(
            selectedPayment?.membershipId!!,
            selectedPayment.payerEmail,
            selectedPayment.payerFullName,
            selectedPayment.eventTitle,
            selectedPayment.eventId!!,
            groupId,
            selectedPayment.groupName,
            selectedPayment.amountPaid ?: amountPaid,
            outstanding,
            selectedPayment.modeOfPayment,
            dataStoreManager.readUserData()?.fullName!!
        )
        val response = activityRepository.confirmSpecialLevyPayment(contribution)
        _loadingState.value = false
        return response
    }

    suspend fun rejectSpecialLevyPayment(paymentToReject: Payment?, reason: String): GenericResponse {
        val rejectedPayment = RejectedPayment(
            paymentToReject?.eventTitle,
            paymentToReject?.eventId,
            paymentToReject?.imageUrl,
            paymentToReject?.membershipId,
            paymentToReject?.payerFullName,
            paymentToReject?.groupName,
            dataStoreManager.readUserData()?.fullName!!,
            reason
        )
        _loadingState.value = true
        val response = activityRepository.rejectSpecialLevyPayment(rejectedPayment)
        _loadingState.value = false
        return response
    }

    suspend fun uploadBatchPaymentRecord(
        inputStream: InputStream?,
        mimeType: String?,
        fileName: String?,
        groupId: Int
    ): GenericResponse? {
        _loadingState.value = true
        val eventId = selectedEvent.value?.eventId
        val requestBody = inputStream?.readBytes()?.toRequestBody(mimeType?.toMediaTypeOrNull())
        val response: GenericResponse? = activityRepository.uploadBatchPaymentRecord(requestBody, fileName, eventId!!, groupId)
        _loadingState.value = false
        return response
    }

    suspend fun getSpecialLeviesForUser(emailAddress: String?) {
        // get special levies if any
        _loadingState.value = true
        val specialLevyResponse =
            activityRepository.getSpecialLevies(emailAddress).data
        val paid = specialLevyResponse?.filter { levy ->
            levy.confirmedPayments?.any { it.memberEmail == emailAddress } == true
        }
        val unpaid = specialLevyResponse?.filter { levy ->
            levy.confirmedPayments?.none { it.memberEmail == emailAddress } == true
        }
        _unpaidSpecialLevyList.value = unpaid
        _paidSpecialLevyList.value = paid
        _loadingState.value = false
    }

    suspend fun downloadExcelTemplate(context: Context) {
        try {
            _loadingState.value = true
            val response = activityRepository.downloadExcelTemplate()
            if (response != null) {
                val excelBytes = response.byteStream()
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "register_template.xlsx")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
                }
                val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                val outputStream = context.contentResolver.openOutputStream(uri!!)
                val buffer = ByteArray(4096)
                var read: Int
                withContext(Dispatchers.IO) {
                    while (excelBytes.read(buffer).also { read = it } != -1) {
                        outputStream!!.write(buffer, 0, read)
                    }
                    outputStream!!.flush()
                    outputStream.close()
                }
                _loadingState.value = false

                val dialog = AlertDialog.Builder(context)
                dialog.setTitle("Template downloaded")
                dialog.setMessage("The excel template has been downloaded successfully. Do you want to open or share it?")
                dialog.setPositiveButton("Open") { _, _ ->
                    // Open file
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    startActivity(context, intent, null)
                }
                dialog.setNegativeButton("Share") { _, _ ->
                    // Share file
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                    startActivity(context, Intent.createChooser(intent, "Share file"), null)
                }
                dialog.setNeutralButton("Cancel") { _, _ -> }
                dialog.show()
            }else {
                _loadingState.value = false
                _errorLiveData.value = AN_ERROR_OCCURRED
            }
        }catch (e: Exception) {
            Toast.makeText(context, "Error saving file: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun deleteImage(image: String) {
        val images = _activityImages.value?.toMutableList()
        images?.remove(image)
        _activityImages.value = images
    }

    fun deleteEvidence() {
        _paymentEvidence.value = null
    }
}