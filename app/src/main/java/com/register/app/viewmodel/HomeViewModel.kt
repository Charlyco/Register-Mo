package com.register.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.register.app.dto.CommentReply
import com.register.app.dto.EventComment
import com.register.app.dto.NewEventDto
import com.register.app.dto.ReactionType
import com.register.app.dto.ScreenLoadState
import com.register.app.model.Event
import com.register.app.model.EventReaction
import com.register.app.model.Member
import com.register.app.util.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val dataStoreManager: DataStoreManager): ViewModel() {
    private val _eventDetailLiveData: MutableLiveData<Event> = MutableLiveData()
    val eventDetailLiveData: LiveData<Event> = _eventDetailLiveData
    private val _loadingState: MutableLiveData<ScreenLoadState>? = MutableLiveData()
    val loadingState: LiveData<ScreenLoadState>? = _loadingState
    private val _eventFeeds: MutableLiveData<List<Event>?> = MutableLiveData(
        listOf(
            Event(12, "Birthday", "Isuikwuato High School 2008", LocalDateTime.now().toString(), "", "", mutableListOf("", "", ""), 3,
                listOf(EventReaction(0, 2, "", ReactionType.LIKE.name)), "Charles", "IHS-2008", 0.0, 0.0, 0.0, listOf(1,2,3,4), ""),
            Event(12, "Child Dedication", "Isuikwuato High School 2008", LocalDateTime.now().toString(), "", "", mutableListOf("", "", "", ""), 3,
                listOf(EventReaction(0, 2, "", ReactionType.LIKE.name)), "Charles", "IHS-2008", 0.0, 0.0, 0.0, listOf(1,2,3,4), "")
        )
    )
    val eventFeeds: LiveData<List<Event>?> = _eventFeeds
    private val _selectedEvent: MutableLiveData<Event> = MutableLiveData()
    val selectedEvent: LiveData<Event> = _selectedEvent
    private val _eventCommentLideData: MutableLiveData<List<EventComment>> = MutableLiveData()
    val eventCommentLiveData: LiveData<List<EventComment>> = _eventCommentLideData

    fun refreshHomeContents() {
        //TODO("Not yet implemented")
    }

    fun setSelectedEvent(eventFeed: Event) {
        _selectedEvent.value = eventFeed
        //Fetch event comment  and set the response data to _eventCommentLivedate
    }

    fun postCommentReply(commentReply: String, eventCommentId: Int): CommentReply? {
        TODO("Not yet implemented")
    }

    fun postComment(newEventDto: NewEventDto, eventId: Int?): EventComment? {
        return null
    }

    fun fetchMemberDetailsById(memberId: Int): Member? {
        return null
    }

    fun checkUserRole(): String? {
        var role: String? = ""
        viewModelScope.launch {
            role = dataStoreManager.readUserRoleData()
        }
        return "ADMIN"
        //return role
    }
}

