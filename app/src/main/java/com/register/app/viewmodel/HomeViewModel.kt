package com.register.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.register.app.dto.ReactionType
import com.register.app.dto.ScreenLoadState
import com.register.app.enums.EventStatus
import com.register.app.enums.EventType
import com.register.app.model.Event
import com.register.app.model.EventCommentDto
import com.register.app.model.EventReactionDto
import com.register.app.model.Faq
import com.register.app.model.Group
import com.register.app.model.MembershipDto
import com.register.app.model.MembershipRequest
import com.register.app.repository.AuthRepository
import com.register.app.repository.GroupRepository
import com.register.app.util.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val groupRepository: GroupRepository,
    private val authRepository: AuthRepository
): ViewModel() {
    private val _faqListLiveData: MutableLiveData<List<Faq>?> = MutableLiveData()
    val faqListLiveData: LiveData<List<Faq>?> = _faqListLiveData
    private val _suggestedGroupLiveData: MutableLiveData<List<Group>> = MutableLiveData()
    val suggestedGroupListLiveData: LiveData<List<Group>> = _suggestedGroupLiveData
    private val _loadingState: MutableLiveData<Boolean> = MutableLiveData()
    val loadingState: LiveData<Boolean> = _loadingState
    private val _eventFeeds: MutableLiveData<List<Event>?> = MutableLiveData()
    val eventFeeds: LiveData<List<Event>?> = _eventFeeds

init {
    viewModelScope.launch {
        _loadingState.value = true
        getEventFeeds()
        getSuggestedGroups()
        getFaqList()
        _loadingState.value = false
    }
}

    private fun getSuggestedGroups() {
        val groups = listOf<Group>()
        _suggestedGroupLiveData.value = groups
    }

    private suspend fun getEventFeeds() {
        val userGroups = dataStoreManager.readUserData()?.groupIds
        val events = mutableListOf<Event>()
        userGroups?.forEach { groupId ->
            groupRepository.getAllActivitiesForGroup(groupId)?.forEach { event ->
                if (event.eventStatus == EventStatus.CURRENT.name) {
                    events.add(event)
                }
            }
        }
        if (events.isNotEmpty()) {
            _eventFeeds.value = events
        }
    }

    fun refreshHomeContents() {
        viewModelScope.launch {
            _loadingState.value = true
            getSuggestedGroups()
            getEventFeeds()
            _loadingState.value = false
        }
    }

//    fun setSelectedEvent(eventFeed: Event) {
//        _selectedEvent.value = eventFeed
//        //Fetch event comment  and set the response data to _eventCommentLivedate
//    }

    suspend fun getFaqList() {
        _faqListLiveData.value = authRepository.getFaqList().data
    }

}

