package com.register.app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.register.app.dto.BankDetail
import com.register.app.dto.ChangeMemberStatusDto
import com.register.app.dto.ComplianceRate
import com.register.app.dto.CreateGroupModel
import com.register.app.dto.EventComment
import com.register.app.dto.GenericResponse
import com.register.app.dto.GroupUpdateDto
import com.register.app.dto.MembershipDtoWrapper
import com.register.app.dto.RateData
import com.register.app.dto.ReactionType
import com.register.app.dto.RemoveMemberModel
import com.register.app.enums.Designation
import com.register.app.enums.EventType
import com.register.app.model.Event
import com.register.app.model.EventCommentDto
import com.register.app.model.EventReactionDto
import com.register.app.model.Group
import com.register.app.model.Member
import com.register.app.model.MembershipDto
import com.register.app.model.MembershipRequest
import com.register.app.repository.AuthRepository
import com.register.app.repository.GroupRepository
import com.register.app.util.DataStoreManager
import com.register.app.util.PAID
import com.register.app.util.UNPAID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val groupRepository: GroupRepository,
    private val authRepository: AuthRepository): ViewModel(){
        private val _groupLogoLivedata: MutableLiveData<String> = MutableLiveData()
        val groupLogoLivedata: LiveData<String> = _groupLogoLivedata
        private val _selectedMember: MutableLiveData<MembershipDto?> = MutableLiveData()
        val selectedMember: LiveData<MembershipDto?> = _selectedMember
        private val _activityRateLiveData: MutableLiveData<Float?> = MutableLiveData(100.0f)
        val activityRateLiveData: LiveData<Float?> = _activityRateLiveData
        private val _groupDetailLiveData: MutableLiveData<Group> = MutableLiveData()
        val groupDetailLiveData: LiveData<Group> = _groupDetailLiveData
        private val _bankDetails: MutableLiveData<BankDetail?> = MutableLiveData()
        val bankDetails: LiveData<BankDetail?> = _bankDetails
        private val _groupListLiveDate: MutableLiveData<List<Group>?> = MutableLiveData()
        val groupListLiveData: MutableLiveData<List<Group>?> = _groupListLiveDate
        val showCreateGroupSheet: MutableLiveData<Boolean> = MutableLiveData(false)
        private val _groupAdminList: MutableLiveData<List<Member>?> = MutableLiveData()
        val groupAdminList: LiveData<List<Member>?> = _groupAdminList
        private val _pendingMembershipDetail: MutableLiveData<Member> = MutableLiveData()
        val pendingMemberLiveData: LiveData<Member> = _pendingMembershipDetail
        private val _groupEvents: MutableLiveData<List<Event>?> = MutableLiveData()
        val groupEvents: LiveData<List<Event>?> = _groupEvents
        private val _paidActivities: MutableLiveData<List<Event>?> = MutableLiveData()
        val paidActivities: LiveData<List<Event>?> = _paidActivities
        private val _unpaidActivities: MutableLiveData<List<Event>?> = MutableLiveData()
        val unpaidActivities: LiveData<List<Event>?> = _unpaidActivities
        private val _eventCommentLideData: MutableLiveData<List<EventComment>> = MutableLiveData()
        val eventCommentLiveData: LiveData<List<EventComment>> = _eventCommentLideData
        private val _membershipId: MutableLiveData<String?> = MutableLiveData("")
        val membershipId: LiveData<String?> = _membershipId
        private val _memberDetails: MutableLiveData<List<Member>?> = MutableLiveData()
        val memberDetailsList: LiveData<List<Member>?> = _memberDetails
        private val _logoUrl: MutableLiveData<String?> = MutableLiveData()
        private val _groupMemberLiveData: MutableLiveData<Member> = MutableLiveData()
        val groupMemberLiveData: LiveData<Member> = _groupMemberLiveData
        val logoUrl: LiveData<String?> = _logoUrl
        private val _loadingState: MutableLiveData<Boolean?> = MutableLiveData(false)
        val loadingState: LiveData<Boolean?> = _loadingState

    init {
        viewModelScope.launch { getAllGroupsForUser() }
    }

    suspend fun getAllGroupsForUser() {
        //fetch groups from server
        _loadingState.value = true
        if (!dataStoreManager.readUserData()?.groupIds.isNullOrEmpty()) {
            val groups: List<Group>? = groupRepository.getAllGroupsForUser(dataStoreManager.readUserData()?.groupIds)
            _groupListLiveDate.value = groups
            _loadingState.value = false
        } else {
            _loadingState.value = false
        }
    }

    fun getBankDetails() {
        val group = groupDetailLiveData.value
        _bankDetails.value = group?.bankDetails
    }

    suspend fun setSelectedGroupDetail(group: Group) {
        _groupDetailLiveData.value = group
        //get membership id
        val member = getMember(group.memberList)
        _membershipId.value = member?.membershipId
        // Get all events for for group and filter into paid and unpaid for user
        val groupEvents = groupRepository.getAllActivitiesForGroup(group.groupId)
        val userEmail = dataStoreManager.readUserData()?.emailAddress

        //filter paid activities
        val paidActivities = groupEvents?.filter { event ->
            event.contributions?.any { it.memberEmail == userEmail } == true }
        _paidActivities.value = paidActivities
        //filter unpaid activities
        val unpaidActivities = groupEvents?.filter { event ->
            event.contributions?.none { it.memberEmail == userEmail } == true  && (
                    (event.eventType == EventType.FREE_WILL.name && event.eventStatus != "COMPLETED") ||
                    (event.eventType == EventType.FREE_WILL.name && event.eventStatus != "ARCHIVED")) }
        _unpaidActivities.value = unpaidActivities

        //get user activity rate
        val activityRate = groupRepository.getMemberActivityRate(
            membershipId.value, member?.joinedDateTime, group.groupId).data
        _activityRateLiveData.value = activityRate?.let { calculateActivityRate(it) }
        //getMembershipId(group)
    }

    private fun calculateActivityRate(activityRate: RateData): Float? {
        return if (activityRate.eventsDue > 0) {
            ((activityRate.eventsPaid / activityRate.eventsDue) * 100).toFloat()
        } else {
            0.0f
        }
    }

    private suspend fun getMember(memberList: List<MembershipDto>?): MembershipDto? {
        return memberList?.find { it.emailAddress == dataStoreManager.readUserData()?.emailAddress }
    }

    fun isUserAdmin(): Boolean {
        var isAdmin: Boolean? = null
        viewModelScope.launch {
            //isAdmin = dataStoreManager.readUserRoleData()!! == MemberOffice.PRESIDENT.name ||
                    //dataStoreManager.readUserRoleData() == MemberOffice.SECRETARY.name
        }
        return true //isAdmin?: false
    }

    fun getIndividualMembershipRequest(emailAddress: String) {
        _pendingMembershipDetail.value =
            Member(1,
                "Uche Egemba",
                "Urchman",
                "+2347037590923",
                "charlyco835@gmail.com",
                "", "",
                "ACTIVE",
                "President",
                "",
                "USER", listOf())
    }

    fun approveMembershipRequest(membershipRequest: MembershipRequest) {
        
    }

    fun getAllEventsForGroup(groupName: String?) {
    }



    private suspend fun getMembershipId(group: Group?): String?{
            val member = group?.memberList?.find { it.emailAddress == dataStoreManager.readUserData()?.emailAddress }
            return member?.membershipId
    }

    private suspend fun getMembershipIdByGroupId(groupId: Int){
            //val grou
            //val member = group?.memberList?.find { it.email == dataStoreManager.readUserEmailData() }
            //member?.membershipId ?: ""
            _membershipId.value = "2123"
    }

    fun getUserActivityRate(groupId: Int?) {
        viewModelScope.launch {
            val user = dataStoreManager.readUserData()
            // get date registered and membership Id

        }
    }

    suspend fun saveGroupUpdate(
        groupId: Int,
        groupName: String,
        description: String?,
        address: String?,
        phone: String?,
        email: String?,
        logoUrl: String?,
        bankDetail: BankDetail
    ): GenericResponse? {
        val group = GroupUpdateDto(
            groupName = groupName,
            groupDescription = description?: "",
            groupEmail = email?: "",
            phoneNumber = phone?: "",
            address = address?: "",
            logoUrl = logoUrl?: "",
            groupType = groupDetailLiveData.value?.groupType!!,
            bankDetails = bankDetail)
        _loadingState.value = true
        val response = groupRepository.updateGroup(groupId, group)
        _loadingState.value = false
        return response
    }

    fun getComplianceRate(contributionSize: Int?): ComplianceRate {
        val groupSize = groupDetailLiveData.value?.memberList?.size
        val percentage = (contributionSize?.div(groupSize!!))?.times(100)?.toDouble()!!
        return ComplianceRate(contributionSize?: 0, groupSize!!, percentage)
    }

    suspend fun uploadGroupLogo(
        inputStream: InputStream,
        mimeType: String?,
        fileNameFromUri: String?
    ): String {
        val requestBody = inputStream.readBytes().toRequestBody(mimeType?.toMediaTypeOrNull())
        val response = groupRepository.uploadImage(requestBody, fileNameFromUri!!)
        _groupLogoLivedata.value = response.data.secureUrl
        //Log.d("UPLOAD IMAGE", "uploadGroupLogo: $file")
        return response.data.secureUrl
    }

    fun populateActivities(type: String) {
        when(type) {
            PAID -> {
                _groupEvents.value = _paidActivities.value
            }
            UNPAID -> {
                _groupEvents.value = _unpaidActivities.value
            }
            else -> {}
        }
    }

    fun setSelectedMembership(member: MembershipDto) {
        _selectedMember.value = member
    }

    suspend fun createNewGroup(groupName: String, groupDescription: String, memberOffice: String, groupType: String): Group {
        _loadingState.value = true
        val groupModel = CreateGroupModel(
            groupName,
            groupDescription,
            dataStoreManager.readUserData()?.emailAddress!!,
            dataStoreManager.readUserData()?.fullName!!,
            memberOffice,
            groupType,
            groupLogoLivedata.value?: "")
        val response = groupRepository.createNewGroup(groupModel)
        _loadingState.value = false
        return response
    }

    suspend fun populateGroupMembers(group: Group?) {
        val membersEmailList = group?.memberList?.map { it.emailAddress }
        if (membersEmailList != null) {
            val members = authRepository.getAllMembersForGroup(membersEmailList)
            _memberDetails.value = members?.data
        }else{
            Log.d("MEMBER DETAILS", "getAllGroupMembers: null")
        }
    }

    suspend fun filterAdmins(memberList: List<MembershipDto>): List<Member>? {
        _loadingState.value = true
        val admins = memberList.filter { member -> member.designation == Designation.ADMIN.name }.map { it.emailAddress }
        val adminDetailList = authRepository.getAllMembersForGroup(admins)?.data
        _groupAdminList.value = adminDetailList
        _loadingState.value = false
        return adminDetailList
    }

    suspend fun addMemberToGroup(groupId: Int?, emailAddress: String): Boolean {
        _loadingState.value = true
        val response = groupRepository.addMemberToGroup(groupId, emailAddress)
        _loadingState.value = false
        return response.status
    }

    fun setSelectedMember(member: Member) {
        _groupMemberLiveData.value = member
    }

    suspend fun changeMemberStatus(
        membershipId: String,
        changeMemberStatusDto: ChangeMemberStatusDto
    ): MembershipDtoWrapper {
        _loadingState.value = true
        val response = groupRepository.changeMemberStatus(membershipId, changeMemberStatusDto)
        _loadingState.value = false
        if (response.status) {
            _selectedMember.value = response.data
        }
        return response
    }

    suspend fun expelMember(removeMemberModel: RemoveMemberModel): GenericResponse {
        _loadingState.value = true
        val response = groupRepository.expelMember(removeMemberModel)
        _loadingState.value = false
        return response
    }

    suspend fun reloadGroup(groupId: Int?) {
        _loadingState.value = true
        Log.d("PULLREFRESH", "Refreshing")
        val response = groupRepository?.getGroupDetails(groupId)
    }
}
