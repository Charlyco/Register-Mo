package com.register.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.register.app.dto.BankDetail
import com.register.app.dto.CommentReply
import com.register.app.dto.ComplianceRate
import com.register.app.dto.EventComment
import com.register.app.dto.PostCommentModel
import com.register.app.dto.ReactionType
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
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val groupRepository: GroupRepository): ViewModel(){
    private val _selectedMember: MutableLiveData<MembershipDto> = MutableLiveData()
    val selectedMember: LiveData<MembershipDto> = _selectedMember
    private val _activityImages: MutableLiveData<List<String>> = MutableLiveData()
    val activityImageList: LiveData<List<String>> = _activityImages
    private val _hasUserPaid: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasPaid: LiveData<Boolean> = _hasUserPaid
    private val _activityRAteLiveData: MutableLiveData<Float> = MutableLiveData()
    val activityRateLiveData: LiveData<Float> = _activityRAteLiveData
    private val _groupDetailLiveData: MutableLiveData<Group> = MutableLiveData()
    val groupDetailLiveData: LiveData<Group> = _groupDetailLiveData
    private val _bankDetails: MutableLiveData<BankDetail> = MutableLiveData()
    val bankDetails: LiveData<BankDetail> = _bankDetails
    private val _paymentEvidence: MutableLiveData<String?> = MutableLiveData()
    val paymentEvidence: LiveData<String?> = _paymentEvidence
    private val _groupListLiveDate: MutableLiveData<List<Group>> = MutableLiveData()
    val groupListLiveData: LiveData<List<Group>> = _groupListLiveDate
    val showCreateGroupSheet: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _groupAdminList: MutableLiveData<List<Member>> = MutableLiveData()
    val groupAdminList: LiveData<List<Member>> = _groupAdminList
    private val _pendingMemberList: MutableLiveData<List<Member>> = MutableLiveData()
    val pendingMemberList: LiveData<List<Member>> = _pendingMemberList
    private val _groupEvents: MutableLiveData<List<Event>?> = MutableLiveData()
    val groupEvents: LiveData<List<Event>?> = _groupEvents
    private val _paidActivities: MutableLiveData<List<Event>?> = MutableLiveData()
    val paidActivities: LiveData<List<Event>?> = _paidActivities
    private val _unpaidActivities: MutableLiveData<List<Event>?> = MutableLiveData()
    val unpaidActivities: LiveData<List<Event>?> = _unpaidActivities
    private val _selectedEvent: MutableLiveData<Event> = MutableLiveData()
    val selectedEvent: LiveData<Event> = _selectedEvent
    private val _eventDetailLiveData: MutableLiveData<Event> = MutableLiveData()
    val eventDetailLiveData: LiveData<Event> = _eventDetailLiveData
    private val _eventCommentLideData: MutableLiveData<List<EventComment>> = MutableLiveData()
    val eventCommentLiveData: LiveData<List<EventComment>> = _eventCommentLideData
    private val _membershipId: MutableLiveData<String> = MutableLiveData("")
    val membershipId: LiveData<String> = _membershipId
    private val _memberDetails: MutableLiveData<List<Member>> = MutableLiveData()
    val memberDetailsList: LiveData<List<Member>> = _memberDetails

    init {
        getAllGroupsForUser()
    }

    private fun getAllGroupsForUser() {
        //fetch groups from server
        val groups = listOf(
            Group(1, "IHS-2008", "2008 set of Isuikwuato High School",
                "charlyco@gmail.com", "+234-7037590923",
                "12 Achuzilam avenue Umuoma Nekede Owerri","Onuoha Charles",
                LocalDateTime.now().toString(),
                listOf(MembershipDto("", ""),
                    MembershipDto("charlyc835@gmaoil.com", "6673ge773ee"), MembershipDto("charlyco835@gmail.com", "eeerrd345fd")),
                listOf(MembershipRequest(1, "charlyco835@gmail.com", ""), MembershipRequest(2, "darlingtonnze@gmail.com", "")),
                listOf("charlyco835@gmail.com", "darlingtonnze@gmail.com"),
                "", "", "OPEN", "" ),
            Group(1, "CMO St Patrick's Parish",
                "The Catholic Men Organization of St. Patrick's Parish Nekede, Owerri",
                "charlyco@gmail.com", "+234-7037590923",
                "12 Achuzilam avenue Umuoma Nekede Owerri","Onuoha Charles",
                LocalDateTime.now().toString(),
                listOf(MembershipDto("charlyco835@gmail.com", "598709834"), MembershipDto("charlyco835@gmail.com", "674r8vd766r6ed"), MembershipDto("charlyco835@gmail.com", "87b8fyqib4gfiquf")),
                listOf(MembershipRequest(1, "charlyco835@gmail.com", ""), MembershipRequest(2, "darlingtonnze@gmail.com", "")),
                listOf("charlyco835@gmail.com", "darlingtonnze@gmail.com"),
                "", "", "CLOSED", "" ),
            Group(1, "CMO St Patrick's Parish",
                "The Catholic Men Organization of St. Patrick's Parish Nekede, Owerri",
                "charlyco@gmail.com", "+234-7037590923",
                "12 Achuzilam avenue Umuoma Nekede Owerri","Onuoha Charles",
                LocalDateTime.now().toString(),
                listOf(MembershipDto("charlyco835@gmail.com", "35qoungu394"), MembershipDto("charlyco835@gmail.com", "tiuugflko4kg34"), MembershipDto("charlyco835@gmail.com", "534q34gq34gq34")),
                listOf(MembershipRequest(1, "charlyco835@gmail.com", ""), MembershipRequest(2, "darlingtonnze@gmail.com", "")),
                listOf("charlyco835@gmail.com", "darlingtonnze@gmail.com"),
                "", "", "CLOSED", "" ),
            )
        _groupListLiveDate.value = groups
    }

    fun getBankDetails() {
        val detail = BankDetail("Onuoha Chukwuemeka",
            "000999888777",
            "First Bank")
        _bankDetails.value = detail
    }

    fun setSelectedGroupDetail(group: Group) {
        _groupDetailLiveData.value = group
        // Get all events for for group and filter into paid and unpaid for user
        _paidActivities.value =
            listOf(
                Event(12, "Birthday", "Isuikwuato High School 2008", LocalDateTime.now().toString(), "", "", mutableListOf("", "", ""), listOf(
                    EventCommentDto(1, "charlyco", LocalDateTime.now().toString(), " Nice one", listOf(), "Birthday")), listOf(EventReactionDto(0, "charlyco", "", ReactionType.LIKE.name, 1)),
                    "Charles", "IHS-2008", 0, 0.0, 0.0, 200.0, listOf(), "ACTIVE"),
                Event(13, "Convocation", "Isuikwuato High School 2008", LocalDateTime.now().toString(), "", "", mutableListOf("", "", ""), listOf(
                    EventCommentDto(1, "charlyco", LocalDateTime.now().toString(), " Nice one", listOf(), "Convocation")), listOf(EventReactionDto(0, "charlyco", "", ReactionType.LIKE.name, 1)),
                    "Charles", "IHS-2008", 0, 0.0, 0.0, 200.0, listOf(), "ACTIVE"),
                Event(14, "Wedding of Victor", "Isuikwuato High School 2008", LocalDateTime.now().toString(), "", "", mutableListOf("", "", ""), listOf(
                    EventCommentDto(1, "charlyco", LocalDateTime.now().toString(), " Nice one", listOf(), "Wedding of Victor")), listOf(EventReactionDto(0, "charlyco", "", ReactionType.LIKE.name, 1)),
                    "Charles", "IHS-2008", 0, 0.0, 0.0, 200.0, listOf(), "ACTIVE"),
            )
        _unpaidActivities.value = listOf(
            Event(13, "Wedding Anniversary", "Isuikwuato High School 2008", LocalDateTime.now().toString(), "", "", mutableListOf("", "", ""), listOf(
                EventCommentDto(1, "charlyco", LocalDateTime.now().toString(), " Nice one", listOf(), "Convocation")), listOf(EventReactionDto(0, "charlyco", "", ReactionType.LIKE.name, 1)),
                "Charles", "IHS-2008", 0, 0.0, 0.0, 200.0, listOf(), "ACTIVE"),
            Event(14, "Matriculation Ceremony", "Isuikwuato High School 2008", LocalDateTime.now().toString(), "", "", mutableListOf("", "", ""), listOf(
                EventCommentDto(1, "charlyco", LocalDateTime.now().toString(), " Nice one", listOf(), "Wedding of Victor")), listOf(EventReactionDto(0, "charlyco", "", ReactionType.LIKE.name, 1)),
                "Charles", "IHS-2008", 0, 0.0, 0.0, 200.0, listOf(), "ACTIVE"),
        )
        //get user activity rate
        _activityRAteLiveData.value = 65.0f
        //get member details
        _memberDetails.value = listOf( Member(1,
            "Nze Darlington",
            "NzeDal",
            "+2347037590923",
            "charlyc835@gmail.com",
            "", "",
            "ACTIVE",
            "President",
            "",
            "USER", listOf()),
            Member(2,
                "Onuoha Chukwuka",
                "Chukwii",
                "+2347037590923",
                "charlyco835@gmail.com",
                "", "",
                "ACTIVE",
                "Secretary",
                "",
                "USER", listOf()),
            Member(3,
                "Onuoha Chukwuemeka",
                "Chacrlyco",
                "+2347037590923",
                "charlyco835@gmail.com",
                "", "",
                "ACTIVE",
                " Financial Secretary",
                "",
                "USER", listOf())
        )
    }

    fun getIndividualAdminDetail() {
        _groupAdminList.value = listOf(
            Member(1,
                "Nze Darlington",
                "NzeDal",
                "+2347037590923",
                "charlyco835@gmail.com",
                "", "",
                "ACTIVE",
                "President",
                 "",
                "USER", listOf()),
                    Member(2,
            "Onuoha Chukwuka",
                        "Chukwii",
            "+2347037590923",
            "charlyco835@gmail.com",
            "", "",
            "ACTIVE",
            "Secretary",
             "",
            "USER", listOf()),
            Member(3,
                "Onuoha Chukwuemeka",
                "Chacrlyco",
                "+2347037590923",
                "charlyco835@gmail.com",
                "", "",
                "ACTIVE",
                " Financial Secretary",
                "",
                "USER", listOf())
        )
    }

    fun isUserAdmin(): Boolean {
        var isAdmin: Boolean? = null
        viewModelScope.launch {
            //isAdmin = dataStoreManager.readUserRoleData()!! == MemberOffice.PRESIDENT.name ||
                    //dataStoreManager.readUserRoleData() == MemberOffice.SECRETARY.name
        }
        return true //isAdmin?: false
    }

    fun getIndividualMembershipRequest(pendingMemberRequests: List<MembershipRequest>) {
        _pendingMemberList.value = listOf(
            Member(1,
                "Uche Egemba",
                "Urchman",
                "+2347037590923",
                "charlyco835@gmail.com",
                "", "",
                "ACTIVE",
                "President",
                "",
                "USER", listOf()),
                    Member(1,
            "Ogbonna Chekwube",
                        "CHekwube",
            "+2347037590923",
            "charlyco835@gmail.com",
            "", "",
            "ACTIVE",
            "Secretary", "",
            "USER", listOf())
        )
    }

    fun approveMembershipRequest(emailAddress: String) {
        
    }

    fun getAllEventsForGroup(groupName: String?) {
        _groupEvents.value = listOf(
            Event(12, "Birthday", "Isuikwuato High School 2008", LocalDateTime.now().toString(), "", "", mutableListOf("", "", ""), listOf(
                EventCommentDto(1, "charlyco", LocalDateTime.now().toString(), " Nice one", listOf(), "Birthday")), listOf(EventReactionDto(0, "charlyco", "", ReactionType.LIKE.name, 1)),
                "Charles", "IHS-2008", 0, 0.0, 0.0, 200.0, listOf(), "ACTIVE"),
            Event(13, "Convocation", "Isuikwuato High School 2008", LocalDateTime.now().toString(), "", "", mutableListOf("", "", ""), listOf(
                EventCommentDto(1, "charlyco", LocalDateTime.now().toString(), " Nice one", listOf(), "Convocation")), listOf(EventReactionDto(0, "charlyco", "", ReactionType.LIKE.name, 1)),
                "Charles", "IHS-2008", 0, 0.0, 0.0, 200.0, listOf(), "ACTIVE"),
            Event(14, "Wedding of Victor", "Isuikwuato High School 2008", LocalDateTime.now().toString(), "", "", mutableListOf("", "", ""), listOf(
                EventCommentDto(1, "charlyco", LocalDateTime.now().toString(), " Nice one", listOf(), "Wedding of Victor")), listOf(EventReactionDto(0, "charlyco", "", ReactionType.LIKE.name, 1)),
                "Charles", "IHS-2008", 0, 0.0, 0.0, 200.0, listOf(), "ACTIVE"),
        )
    }

    fun setSelectedEvent(eventFeed: Event) {
        _selectedEvent.value = eventFeed
        viewModelScope.launch { getMembershipIdByGroupId(eventFeed.groupId!!) }
    }

    fun postCommentReply(commentReply: String, eventCommentId: Int): CommentReply? {
        return null
    }

    fun postComment(postCommentModel: PostCommentModel, eventId: Int?): EventComment? {
        return null
    }

    suspend fun getMembershipId(group: Group?){
            val member = group?.memberList?.find { it.email == dataStoreManager.readUserEmailData() }
            member?.membershipId ?: "${group?.groupName}_1234"
            _membershipId.postValue(member?.membershipId ?: "${group?.groupName}_1234")
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

    fun saveGroupUpdate() {

    }

    fun getMemberDetails(email: String): Member {
       return Member(1,
            "Ogbonna Chekwube",
           "Chekwube",
            "+2347037590923",
            "charlyco835@gmail.com",
            "", "",
            "ACTIVE",
            "Secretary",
             "",
            "USER", listOf())
    }

    fun getComplianceRate(contributionSize: Int?, groupId: Int?): ComplianceRate {
        //Get the group detail and check membershipSize
        return ComplianceRate(23, 45, ((23/45)*100).toDouble())
    }

    fun uploadGroupLogo(file: File) {
        //Upload logo and return url
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

    fun uploadActivityImages(file: File) {
        //Upload the image ans add the url to imageList livedata
    }

    suspend fun createNewActivity(
        activityTitle: String,
        activityDescription: String,
        levyAmount: Double,
        eventDate: String
    ) {
        //val newActivity = data
//        newActivity.groupId = _groupDetailLiveData.value?.groupId
//        newActivity.groupName = _groupDetailLiveData.value?.groupName
//        newActivity.eventCreator = dataStoreManager.readAuthData()
        //Call repository method to create activity
    }

    fun setSelectedMember(member: MembershipDto) {
        _selectedMember.value = member
    }
}
