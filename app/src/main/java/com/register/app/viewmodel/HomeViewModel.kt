package com.register.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.register.app.dto.ReactionType
import com.register.app.dto.ScreenLoadState
import com.register.app.model.Event
import com.register.app.model.EventCommentDto
import com.register.app.model.EventReactionDto
import com.register.app.model.Group
import com.register.app.model.MembershipDto
import com.register.app.model.MembershipRequest
import com.register.app.util.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val dataStoreManager: DataStoreManager): ViewModel() {
    private val _suggestedGroupLiveData: MutableLiveData<List<Group>> = MutableLiveData()
    val suggestedGroupListLiveData: LiveData<List<Group>> = _suggestedGroupLiveData
    private val _loadingState: MutableLiveData<ScreenLoadState>? = MutableLiveData()
    val loadingState: LiveData<ScreenLoadState>? = _loadingState
    private val _eventFeeds: MutableLiveData<List<Event>?> = MutableLiveData()
    val eventFeeds: LiveData<List<Event>?> = _eventFeeds

init {
    getEventFeeds()
    getSuggestedGroups()
}

    private fun getSuggestedGroups() {
        val groups = listOf(
            Group(1, "IHS-2008", "2008 set of Isuikwuato High School",
                "charlyco@gmail.com", "+234-7037590923",
                "12 Achuzilam avenue Umuoma Nekede Owerri","Onuoha Charles",
                LocalDateTime.now().toString(),
                listOf(
                    MembershipDto("", ""),
                    MembershipDto("", ""), MembershipDto("", "")
                ),
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
        _suggestedGroupLiveData.value = groups
    }

    private fun getEventFeeds() {
        val events = listOf(
            Event(12, "Birthday", "Isuikwuato High School 2008", LocalDateTime.now().toString(), "", "", mutableListOf("", "", ""), listOf(
                EventCommentDto(1, "charlyco", LocalDateTime.now().toString(), " Nice one", listOf(), "Birthday")
            ), listOf(EventReactionDto(0, "charlyco", "", ReactionType.LIKE.name, 1)),
                "Charles", "IHS-2008", 0, 0.0, 0.0, 200.0, listOf(), "ACTIVE"),
            Event(13, "Convocation", "Isuikwuato High School 2008", LocalDateTime.now().toString(), "", "", mutableListOf("", "", ""), listOf(
                EventCommentDto(1, "charlyco", LocalDateTime.now().toString(), " Nice one", listOf(), "Convocation")
            ), listOf(EventReactionDto(0, "charlyco", "", ReactionType.LIKE.name, 1)),
                "Charles", "IHS-2008", 0, 0.0, 0.0, 200.0, listOf(), "ACTIVE"),
            Event(14, "Wedding of Victor", "Isuikwuato High School 2008", LocalDateTime.now().toString(), "", "", mutableListOf("", "", ""), listOf(
                EventCommentDto(1, "charlyco", LocalDateTime.now().toString(), " Nice one", listOf(), "Wedding of Victor")
            ), listOf(EventReactionDto(0, "charlyco", "", ReactionType.LIKE.name, 1)),
                "Charles", "IHS-2008", 0, 0.0, 0.0, 200.0, listOf(), "ACTIVE"),
        )
        _eventFeeds.value = events
    }

    fun refreshHomeContents() {
        //TODO("Not yet implemented")
    }

//    fun setSelectedEvent(eventFeed: Event) {
//        _selectedEvent.value = eventFeed
//        //Fetch event comment  and set the response data to _eventCommentLivedate
//    }


}

