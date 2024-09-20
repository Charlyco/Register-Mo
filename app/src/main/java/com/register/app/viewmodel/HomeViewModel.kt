package com.register.app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.register.app.enums.EventStatus
import com.register.app.model.Event
import com.register.app.model.Faq
import com.register.app.model.Group
import com.register.app.model.NotificationModel
import com.register.app.model.PrivacyPolicy
import com.register.app.repository.AuthRepository
import com.register.app.repository.GroupRepository
import com.register.app.repository.NotificationRepository
import com.register.app.util.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val groupRepository: GroupRepository,
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository
): ViewModel() {
    private val _privacyStatement: MutableLiveData<PrivacyPolicy?> = MutableLiveData()
    val privacyStatement: LiveData<PrivacyPolicy?> = _privacyStatement
    private val _authModeLiveData: MutableLiveData<Boolean?> = MutableLiveData()
    val authModeLiveData: LiveData<Boolean?> = _authModeLiveData
    private val _notificationList: MutableLiveData<MutableList<NotificationModel>?> = MutableLiveData()
    val notificationList: LiveData<MutableList<NotificationModel>?> = _notificationList
    private val _faqListLiveData: MutableLiveData<List<Faq>?> = MutableLiveData()
    val faqListLiveData: LiveData<List<Faq>?> = _faqListLiveData
    private val _suggestedGroupLiveData: MutableLiveData<List<Group>> = MutableLiveData()
    val suggestedGroupListLiveData: LiveData<List<Group>> = _suggestedGroupLiveData
    private val _loadingState: MutableLiveData<Boolean> = MutableLiveData()
    val loadingState: LiveData<Boolean> = _loadingState
    private val _homeDestination: MutableLiveData<String> = MutableLiveData("splash")
    val homeDestination: LiveData<String> = _homeDestination

    init {
        viewModelScope.launch {
           _authModeLiveData.value = dataStoreManager.readLoginType()
        }
    }

    private fun getSuggestedGroups() {
        val groups = listOf<Group>()
        _suggestedGroupLiveData.value = groups
    }

    fun refreshHomeContents() {
        viewModelScope.launch {
            _loadingState.value = true
            getSuggestedGroups()
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

    fun setHomeDestination(route: String) {
        _homeDestination.value = route
    }

    fun addNotification(notification: NotificationModel) {
        viewModelScope.launch {
            val response = notificationRepository.saveNotification(notification)
            _notificationList.value = response
        }
    }

    suspend fun populateNotifications() {
            Log.d("notifications", "populating notifications")
            val notifications = notificationRepository.getAllNotifications()
            Log.d("notifications", notifications.toString())
            _notificationList.postValue(notifications)
    }

    suspend fun setAuthMode(mode: Boolean) {
        dataStoreManager.writeLoginType(mode)
        _authModeLiveData.value = mode
    }

    suspend fun getPrivacyStatement() {
        _privacyStatement.value = authRepository.getPrivacyStatement()?.data
    }
}

