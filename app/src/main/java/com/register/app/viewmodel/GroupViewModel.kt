package com.register.app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.register.app.dto.AdminUpdateResponse
import com.register.app.dto.BankDetail
import com.register.app.dto.ChangeMemberStatusDto
import com.register.app.dto.ComplianceRate
import com.register.app.dto.Contestant
import com.register.app.dto.CreateGroupModel
import com.register.app.dto.Election
import com.register.app.dto.EventComment
import com.register.app.dto.GenericResponse
import com.register.app.dto.GroupDetailWrapper
import com.register.app.dto.GroupNotification
import com.register.app.dto.GroupUpdateDto
import com.register.app.dto.GroupsWrapper
import com.register.app.dto.JoinGroupDto
import com.register.app.dto.MembershipDtoWrapper
import com.register.app.dto.RateData
import com.register.app.dto.RemoveMemberModel
import com.register.app.dto.UpdateAdminDto
import com.register.app.dto.VoteDto
import com.register.app.enums.ContestantStatus
import com.register.app.enums.Designation
import com.register.app.enums.ElectionStatus
import com.register.app.enums.MemberStatus
import com.register.app.enums.VoteStatus
import com.register.app.model.Event
import com.register.app.model.Group
import com.register.app.model.Member
import com.register.app.model.MembershipDto
import com.register.app.model.MembershipRequest
import com.register.app.repository.ActivityRepository
import com.register.app.repository.AuthRepository
import com.register.app.repository.GroupRepository
import com.register.app.util.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val groupRepository: GroupRepository,
    private val authRepository: AuthRepository,
    private val activityRepository: ActivityRepository): ViewModel() {
        private val _groupNotificationList: MutableLiveData<List<GroupNotification>?> = MutableLiveData()
    val groupNotificationList: LiveData<List<GroupNotification>?> = _groupNotificationList
    private val _electionsLideData: MutableLiveData<List<Election>?> = MutableLiveData()
    val electionsLiveData: LiveData<List<Election>?> = _electionsLideData
    private val _electionDetail: MutableLiveData<Election?> = MutableLiveData()
    val electionDetail: LiveData<Election?> = _electionDetail
    private val _contestantList: MutableLiveData<List<Member>?> = MutableLiveData(mutableListOf())
    val contestantList: LiveData<List<Member>?> = _contestantList
    private val _complianceRate: MutableLiveData<ComplianceRate> = MutableLiveData()
    val complianceRate: LiveData<ComplianceRate> = _complianceRate
    private val _isAdminLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    val isUserAdminLiveData: LiveData<Boolean> = _isAdminLiveData
    private val _groupLogoLivedata: MutableLiveData<String> = MutableLiveData()
    val groupLogoLivedata: LiveData<String> = _groupLogoLivedata
    private val _selectedMember: MutableLiveData<MembershipDto?> = MutableLiveData()
    val selectedMember: LiveData<MembershipDto?> = _selectedMember
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
    private val _isSuspended: MutableLiveData<Boolean> = MutableLiveData(false)
    val isUserSuspended: LiveData<Boolean> = _isSuspended
    private val _memberUnpaidActivities: MutableLiveData<List<Event>?> = MutableLiveData()
    val memberUnpaidActivities: LiveData<List<Event>?> = _memberUnpaidActivities
    private val _memberPaidActivities: MutableLiveData<List<Event>?> = MutableLiveData()
    val memberPaidActivities: LiveData<List<Event>?> = _memberPaidActivities
    private val _eventCommentLideData: MutableLiveData<List<EventComment>> = MutableLiveData()
    val eventCommentLiveData: LiveData<List<EventComment>> = _eventCommentLideData
    private val _membershipId: MutableLiveData<String?> = MutableLiveData()
    val membershipId: LiveData<String?> = _membershipId
    private val _memberDetails: MutableLiveData<List<Member>?> = MutableLiveData()
    val memberDetailsList: LiveData<List<Member>?> = _memberDetails
    private val _logoUrl: MutableLiveData<String?> = MutableLiveData()
    private val _groupMemberLiveData: MutableLiveData<Member> = MutableLiveData()
    val groupMemberLiveData: LiveData<Member> = _groupMemberLiveData
    val logoUrl: LiveData<String?> = _logoUrl
    private val _loadingState: MutableLiveData<Boolean?> = MutableLiveData(false)
    val loadingState: LiveData<Boolean?> = _loadingState
    private val _memberPaymentRateLiveData: MutableLiveData<RateData?> = MutableLiveData()
    val memberPaymentRateLiveData: LiveData<RateData?> = _memberPaymentRateLiveData
    private val _suggestedGroupList: MutableLiveData<List<Group>?> = MutableLiveData()
    val suggestedGroupList: LiveData<List<Group>?> = _suggestedGroupList

    init {
        viewModelScope.launch { getAllGroupsForUser() }
    }

    suspend fun getAllGroupsForUser() {
        if (!dataStoreManager.readUserData()?.groupIds.isNullOrEmpty()) {
            _loadingState.value = true
            val groups: List<Group>? =
                groupRepository.getAllGroupsForUser(dataStoreManager.readUserData()?.groupIds)
            _loadingState.value = false
            _groupListLiveDate.value = groups
            _groupDetailLiveData.value =
                groups?.get(0) // temporarily set a default group till  the user selects a group
            getMembershipId(groups?.get(0))
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
        isUserAdmin(group)
        isUserSuspended(group)
    }

    private fun isUserSuspended(group: Group) {
        val membership = group.memberList?.find { it.membershipId == membershipId.value }
        if (membership?.memberStatus == MemberStatus.SUSPENDED.name) {
            _isSuspended.value = true
        } else _isSuspended.value = false
    }

    private suspend fun getMember(memberList: List<MembershipDto>?): MembershipDto? {
        return memberList?.find { it.emailAddress == dataStoreManager.readUserData()?.emailAddress }
    }

    suspend fun isUserAdmin(group: Group) {
        val member = group.memberList?.find { it.emailAddress ==  dataStoreManager.readUserData()?.emailAddress}

        _isAdminLiveData.value = (member?.designation == Designation.ADMIN.name && (member.memberStatus == MemberStatus.ACTIVE.name))
    }

    suspend fun getIndividualMembershipRequest(emailAddress: String) {
        _pendingMembershipDetail.value = authRepository.getMemberDetailsByEmail(emailAddress)
    }

    suspend fun approveMembershipRequest(membershipRequest: MembershipRequest): GenericResponse {
        _loadingState.value = true
        val response = groupRepository.approveMembershipRequest(membershipRequest)
        _loadingState.value = false
        return response
    }

    private suspend fun getMembershipId(group: Group?) {
        val member =
            group?.memberList?.find { it.emailAddress == dataStoreManager.readUserData()?.emailAddress }
        _membershipId.value = member?.membershipId
    }

    suspend fun saveGroupUpdate(
        groupId: Int,
        groupName: String,
        description: String?,
        address: String?,
        phone: String?,
        email: String?,
        logoUrl: String?,
        groupType: String?
    ): GenericResponse? {
        val group = GroupUpdateDto(
            groupName = groupName,
            groupDescription = description ?: "",
            groupEmail = email ?: "",
            phoneNumber = phone ?: "",
            address = address ?: "",
            logoUrl = logoUrl ?: "",
            groupType = groupType?: groupDetailLiveData.value?.groupType!!
        )
        _loadingState.value = true
        val response = groupRepository.updateGroup(groupId, group)
        _loadingState.value = false
        return response
    }

    fun getComplianceRate(event: Event) {
        val groupSize =
            groupListLiveData.value?.find { it.groupId == event.groupId }?.memberList?.size
        val percentage = ((event.contributions?.size ?: 0.times(100)).div(groupSize!!)).toDouble()
        _complianceRate.value =
            ComplianceRate(event.contributions?.size ?: 0, groupSize, percentage)
    }

    suspend fun uploadGroupLogo(
        inputStream: InputStream,
        mimeType: String?,
        fileNameFromUri: String?
    ): String {
        _loadingState.value = true
        val requestBody = inputStream.readBytes().toRequestBody(mimeType?.toMediaTypeOrNull())
        val response = groupRepository.uploadImage(requestBody, fileNameFromUri!!)
        _loadingState.value = false
        if (response.status) {
            _groupLogoLivedata.value = response.data?.secureUrl
            return response.data?.secureUrl!!
        }
        return ""
    }

    suspend fun setSelectedMember(member: Member) {
        _groupMemberLiveData.value = member
        val group = groupDetailLiveData.value
        val membershipDto =
            group?.memberList?.find { membershipDto -> membershipDto.emailAddress == member.emailAddress }
        getMemberActivityRate(membershipDto?.membershipId, membershipDto?.joinedDateTime, groupDetailLiveData.value?.groupId!!
        )
        val groupEvents = activityRepository.getAllActivitiesForGroup(
            group?.groupId!!, membershipDto?.membershipId!!, membershipDto.joinedDateTime)
        _memberPaidActivities.value = groupEvents?.paidEvents
        _memberUnpaidActivities.value = groupEvents?.unpaidEvents
        _selectedMember.value = membershipDto
    }

    suspend fun createNewGroup(
        groupName: String,
        groupDescription: String,
        memberOffice: String,
        groupType: String
    ): GroupDetailWrapper? {
        val groupModel = CreateGroupModel(
            groupName,
            groupDescription,
            dataStoreManager.readUserData()?.emailAddress!!,
            dataStoreManager.readUserData()?.fullName!!,
            memberOffice,
            groupType,
            groupLogoLivedata.value ?: ""
        )
        _loadingState.value = true
        val response = groupRepository.createNewGroup(groupModel)
        _loadingState.value = false
        if (response?.status == true) {
            showCreateGroupSheet.postValue(false)
            setSelectedGroupDetail(response.data!!)
            isUserAdmin(response.data) //grants the user admin rights
            return response
        } else {
            return response
        }
    }

    suspend fun populateGroupMembers(group: Group?) {
        val membersEmailList = group?.memberList?.map { it.emailAddress }
        if (membersEmailList != null) {
            _loadingState.value = true
            val members = authRepository.getAllMembersForGroup(membersEmailList)
            _loadingState.value = false
            _memberDetails.value = members?.data
        } else {
            Log.d("MEMBER DETAILS", "getAllGroupMembers: null")
        }
    }

    suspend fun filterAdmins(memberList: List<MembershipDto>): List<Member>? {
        _loadingState.value = true
        val admins = memberList.filter { member -> member.designation == Designation.ADMIN.name }
            .map { it.emailAddress }
        val adminDetailList = authRepository.getAllMembersForGroup(admins)?.data
        _groupAdminList.value = adminDetailList
        _loadingState.value = false
        return adminDetailList
    }

    suspend fun addMemberToGroup(groupId: Int?, emailAddress: String): Boolean {
        _loadingState.value = true
        val response = groupRepository.addMemberToGroup(groupId, emailAddress)
        _loadingState.value = false
        if (response.status) {
            reloadGroup(groupId) // refresh group details
        }
        return response.status
    }

    private suspend fun getMemberActivityRate(
        membershipId: String?,
        joinedDateTime: String?,
        groupId: Int
    ) {
        val activityRate = activityRepository.getMemberActivityRate(
            membershipId, joinedDateTime, groupId
        ).data
        _memberPaymentRateLiveData.value = activityRate
        //_activityRateLiveData.value = activityRate?.let { calculateActivityRate(it) }
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
        if (response.status) {
            reloadGroup(removeMemberModel.groupId) // refresh group details
        }
        return response
    }

    suspend fun reloadGroup(groupId: Int?): Group? {
        _loadingState.value = true
        val response = groupRepository.getGroupDetails(groupId)?.data
        _loadingState.value = false
        if (response != null) {
            setSelectedGroupDetail(response)
        }
        return response
    }

    suspend fun searchGroupByName(searchTag: String): GroupsWrapper? {
        _loadingState.value = true
        val response = groupRepository.searchGroupByName(searchTag)
        _loadingState.value = false
        if (response?.status == true) {
            _suggestedGroupList.value = response.data
        }
        return response
    }

    suspend fun requestToJoinGroup(selectedGroup: Group): GenericResponse {
        _loadingState.value = true
        val userInfo = JoinGroupDto(
            dataStoreManager.readUserData()?.emailAddress!!,
            dataStoreManager.readUserData()?.fullName!!
        )
        val response = groupRepository.requestToJoinGroup(selectedGroup.groupId, userInfo)
        _loadingState.value = false
        return response
    }

    fun getMembershipIdForMember(emailAddress: String): String? {
        val group = groupDetailLiveData.value
        val member = group?.memberList?.find { it.emailAddress == emailAddress }
        return member?.membershipId
    }

    suspend fun createElection(
        electionTitle: String,
        electionDescription: String,
        electionDate: String,
        office: String
    ): GenericResponse {
        val contestants = mutableListOf<Contestant>()
        contestantList.value?.forEach { contestant ->
            contestants.add(
                Contestant(
                    null,
                    contestant.fullName,
                    contestant.imageUrl,
                    getMembershipIdForMember(contestant.emailAddress),
                    contestant.emailAddress,
                    electionTitle,
                    null,
                    office,
                    ContestantStatus.UNDECIDED.name,
                    ""
                )
            )
        }
        val group = groupDetailLiveData.value
        val admin = dataStoreManager.readUserData()?.fullName
        val election = Election(
            null, electionTitle,
            electionDate, electionDescription,
            office, contestants,
            group?.groupId, group?.groupName,
            admin, null, ElectionStatus.FUTURISTIC.name
        )

        _loadingState.value = true
        val response = groupRepository.createElection(election)
        _loadingState.value = false
        return response
    }

    fun addToContestants(member: Member) {
        val members = contestantList.value?.toMutableList()
        members?.add(member)
        _contestantList.value = members
    }

    fun removeFromContestants(contestant: Member) {
        val members = contestantList.value?.toMutableList()
        members?.remove(contestant)
        _contestantList.value = members
    }

    fun clearContestants() {
        val members = contestantList.value?.toMutableList()
        members?.clear()
        _contestantList.value = members
    }

    fun getGroupElections(groupId: Int?) {
        _loadingState.value = true
        viewModelScope.launch {
            val response = groupRepository.getGroupElections(groupId)
            _electionsLideData.value = response
            _loadingState.value = false
        }
    }

    fun setSelectedElection(election: Election) {
        _electionDetail.value = election
    }

    suspend fun castVote(election: Election, contestant: Contestant?): GenericResponse {
        _loadingState.value = true
        val user = dataStoreManager.readUserData()?.fullName
        val voteModel = VoteDto(
            null,
            user,
            election.groupId,
            contestant?.contestantName,
            contestant?.id,
            election.electionId,
            contestant?.office,
            LocalDateTime.now().toString(),
            VoteStatus.VALID.name
        )
        val response = groupRepository.castVote(voteModel)
        if (response.status) {
            getElectionDetails(election.electionId!!)
        }
        _loadingState.value = false
        return response
    }

    suspend fun checkIfUserHasVoted(election: Election): Boolean {
        val user = dataStoreManager.readUserData()?.fullName
        val response = groupRepository.checkIfUserHasVoted(user, election.electionId)
        return response.status
    }

    suspend fun removeContestant(contestantId: Long?, electionId: Int?): GenericResponse {
        _loadingState.value = true
        val response = groupRepository.removeContestant(contestantId, electionId)
        _loadingState.value = false
        return response
    }

    fun getElectionDetails(electionId: Int) {
        _loadingState.value = true
        viewModelScope.launch {
            val response = groupRepository.getElectionDetails(electionId)
            _electionDetail.value = response
            _loadingState.value = false
        }
    }

    suspend fun addContestant(contestant: Member, election: Election) {
        val newContestant = Contestant(
            null,
            contestant.fullName,
            contestant.imageUrl,
            getMembershipIdForMember(contestant.emailAddress),
            contestant.emailAddress,
            election.electionTitle,
            null,
            election.office,
            ContestantStatus.UNDECIDED.name,
            ""
        )
        _loadingState.value = true
        val response = groupRepository.addContestant(newContestant, election.electionId!!)
        if (response.status) {
            getElectionDetails(election.electionId)
        }
        _loadingState.value = false
        //return response
    }

    suspend fun startElection(election: Election) {
        if (election.electionStatus != ElectionStatus.COMPLETED.name) {
            _loadingState.value = true
            val response = groupRepository.startElection(election.electionId)
            if (response.status) {
                getElectionDetails(election.electionId!!)
            }
            _loadingState.value = false
        }
    }

    suspend fun endElection(election: Election) {
        if (election.electionStatus == ElectionStatus.ACTIVE.name) {
            _loadingState.value = true
            val response = groupRepository.endElection(election.electionId)
            if (response.status) {
                getElectionDetails(election.electionId!!)
            }
            _loadingState.value = false
        }
    }

    suspend fun leaveGroup(group: Group?): GenericResponse {
        val removeMemberModel = RemoveMemberModel(
            membershipId.value!!,
            dataStoreManager.readUserData()?.emailAddress!!,
            group?.groupId!!
        )
        _loadingState.value = true
        val response = groupRepository.expelMember(removeMemberModel)
        _loadingState.value = false
        return response
    }

    suspend fun removeAdmin(membership: MembershipDto?, office: String): AdminUpdateResponse {
        val group = groupDetailLiveData.value
        val updateAdminDto = UpdateAdminDto(group?.groupId, membership?.membershipId, office)
        _loadingState.value = true
        val response = groupRepository.removeAdmin(updateAdminDto)
        _loadingState.value = false
        if (response.status) {
            _groupDetailLiveData.value = response.data!!
        }
        return response
    }

    suspend fun makeAdmin(membership: MembershipDto?, newOffice: String): AdminUpdateResponse {
        val group = groupDetailLiveData.value
        val updateAdminDto = UpdateAdminDto(group?.groupId, membership?.membershipId, newOffice)
        _loadingState.value = true
        val response = groupRepository.makeAdmin(updateAdminDto)
        _loadingState.value = false
        if (response.status) {
            _groupDetailLiveData.value = response.data!!
        }
        return response
    }

    suspend fun getGroupNotifications(groupId: Int?) {
        _loadingState.value = true
        val response = groupRepository.getGroupNotifications(groupId)
        _groupNotificationList.value = response.data
        _loadingState.value = false
    }

    suspend fun fetchBankDetails(groupId: Int) {
        _loadingState.value = true
        val response = groupRepository.getBankDetails(groupId)
        _bankDetails.value = response.data
        _loadingState.value = false
    }

    suspend fun updateBankDetail(bankDetail: BankDetail, groupId: Int): GenericResponse {
        _loadingState.value = true
        val response = groupRepository.updateBankDetails(bankDetail, groupId)
        _loadingState.value = false
        return response
    }

    suspend fun rejectMembershipRequest(selectedRequest: MembershipRequest): GenericResponse {
        _loadingState.value = true
        val response = groupRepository.rejectMembershipRequest(selectedRequest)
        _loadingState.value = false
        return response
    }

    suspend fun deleteGroup(groupId: Int): GenericResponse {
        _loadingState.value = true
        val response  = groupRepository.deleteGroup(groupId)
        _loadingState.value = false
        return response
    }

}