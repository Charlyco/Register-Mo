package com.register.app.viewmodel

import androidx.datastore.preferences.protobuf.ListValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.register.app.dto.ActivityRate
import com.register.app.dto.BankDetail
import com.register.app.dto.CommentReply
import com.register.app.dto.EventComment
import com.register.app.dto.NewEventDto
import com.register.app.dto.ReactionType
import com.register.app.enums.MemberOffice
import com.register.app.model.Event
import com.register.app.model.EventReaction
import com.register.app.model.Group
import com.register.app.model.Member
import com.register.app.model.MembershipDto
import com.register.app.model.MembershipRequest
import com.register.app.util.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(private val dataStoreManager: DataStoreManager): ViewModel(){
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
    private val _activeGroupEvents: MutableLiveData<List<Event>?> = MutableLiveData()
    val activeGroupEvents: LiveData<List<Event>?> = _activeGroupEvents
    private val _selectedEvent: MutableLiveData<Event> = MutableLiveData()
    val selectedEvent: LiveData<Event> = _selectedEvent
    private val _eventDetailLiveData: MutableLiveData<Event> = MutableLiveData()
    val eventDetailLiveData: LiveData<Event> = _eventDetailLiveData
    private val _eventCommentLideData: MutableLiveData<List<EventComment>> = MutableLiveData()
    val eventCommentLiveData: LiveData<List<EventComment>> = _eventCommentLideData
    private val _membershipId: MutableLiveData<String> = MutableLiveData("")
    val membershipId: LiveData<String> = _membershipId

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
                    MembershipDto("", ""), MembershipDto("", "")),
                listOf(MembershipRequest(1, "charlyco835@gmail.com", ""), MembershipRequest(2, "darlingtonnze@gmail.com", "")),
                listOf("charlyco835@gmail.com", "darlingtonnze@gmail.com"),
                "", "", "OPEN", "" ),
            Group(1, "CMO St Patrick's Parish",
                "The Catholic Men Organization of St. Patrick's Parish Nekede, Owerri",
                "charlyco@gmail.com", "+234-7037590923",
                "12 Achuzilam avenue Umuoma Nekede Owerri","Onuoha Charles",
                LocalDateTime.now().toString(),
                listOf(MembershipDto("", ""), MembershipDto("", ""), MembershipDto("", "")),
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
        // Get all active events for user
        _activeGroupEvents.value =
            listOf(
                Event(12, "Birthday", "Isuikwuato High School 2008", LocalDateTime.now().toString(), "", "", mutableListOf("", "", ""), 3,
                    listOf(EventReaction(0, 2, "", ReactionType.LIKE.name)), "Charles", "IHS-2008", 0.0, 0.0, 0.0, listOf(1,2,3,4), ""),
                Event(12, "Child Dedication", "Isuikwuato High School 2008", LocalDateTime.now().toString(), "", "", mutableListOf("", "", "", ""), 3,
                    listOf(EventReaction(0, 2, "", ReactionType.LIKE.name)), "Charles", "IHS-2008", 0.0, 0.0, 0.0, listOf(1,2,3,4), "")
            )
        //get user activity rate
        _activityRAteLiveData.value = 65.0f
    }

    fun getIndividualAdminDetail() {
        _groupAdminList.value = listOf(
            Member(1,
                "Nze Darlington",
                "+2347037590923",
                "charlyco835@gmail.com",
                "", "",
                "ACTIVE",
                "President",
                0.0, "",
                "USER", listOf()),
                    Member(2,
            "Onuoha Chukwuka",
            "+2347037590923",
            "charlyco835@gmail.com",
            "", "",
            "ACTIVE",
            "Secretary",
            0.0, "",
            "USER", listOf()),
            Member(3,
                "Onuoha Chukwuemeka",
                "+2347037590923",
                "charlyco835@gmail.com",
                "", "",
                "ACTIVE",
                " Financial Secretary",
                0.0, "",
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
                "+2347037590923",
                "charlyco835@gmail.com",
                "", "",
                "ACTIVE",
                "President",
                0.0, "",
                "USER", listOf()),
                    Member(1,
            "Ogbonna Chekwube",
            "+2347037590923",
            "charlyco835@gmail.com",
            "", "",
            "ACTIVE",
            "Secretary",
            0.0, "",
            "USER", listOf())
        )
    }

    fun approveMembershipRequest(emailAddress: String) {
        
    }

    fun getAllEventsForGroup(groupName: String?) {
        _groupEvents.value = listOf(
            Event(12, "Birthday", "Isuikwuato High School 2008", LocalDateTime.now().toString(), "", "", mutableListOf("","",""), 3,
                listOf(EventReaction(0, 2, "", ReactionType.LIKE.name)), "Charles", "IHS-2008", 0.0, 0.0, 0.0, listOf(1,2,3), ""),
            Event(12, "Child Dedication", "Isuikwuato High School 2008", LocalDateTime.now().toString(), "", "", mutableListOf("-","","",""), 3,
                listOf(EventReaction(0, 2, "", ReactionType.LIKE.name)), "Charles", "IHS-2008", 0.0, 0.0, 0.0, listOf(1,2,3), ""),
            Event(12, "Convocation", "SEES 2019, FPNO", LocalDateTime.now().toString(), "", "", mutableListOf("",""), 3,
                listOf(EventReaction(0, 2, "", ReactionType.LIKE.name)), "Charles", "IHS-2008", 0.0, 0.0, 0.0, listOf(1,2,3), ""),
            Event(12, "Child Dedication", "Isuikwuato High School 2008", LocalDateTime.now().toString(), "", "", mutableListOf("","",""), 3,
                listOf(EventReaction(0, 2, "", ReactionType.LIKE.name)), "Charles", "IHS-2008", 0.0, 0.0, 0.0, listOf(1,2,3), "")
        )
    }

    fun setSelectedEvent(eventFeed: Event) {
        _selectedEvent.value = eventFeed
    }

    fun postCommentReply(commentReply: String, eventCommentId: Int): CommentReply? {
        return null
    }

    fun postComment(newEventDto: NewEventDto, eventId: Int?): EventComment? {
        return null
    }

    suspend fun getMembershipId(group: Group?): String {
        return withContext(Dispatchers.IO) {
            val member = group?.memberList?.find { it.email == dataStoreManager.readUserEmailData() }
            member?.membershipId ?: ""
        }
    }

    fun getUserActivityRate(groupId: Int?) {
        viewModelScope.launch {
            val user = dataStoreManager.readAuthData()
            // get date registered and membership Id

        }
    }

    fun saveGroupUpdate() {

    }

    fun getMemberDetails(email: String): Member {
       return Member(1,
            "Ogbonna Chekwube",
            "+2347037590923",
            "charlyco835@gmail.com",
            "", "",
            "ACTIVE",
            "Secretary",
            0.0, "",
            "USER", listOf())
    }
}
