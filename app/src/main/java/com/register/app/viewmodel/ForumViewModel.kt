package com.register.app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.register.app.dto.ChatMessageResponse
import com.register.app.dto.GroupStateItem
import com.register.app.dto.JoinChatPayload
import com.register.app.dto.MessageData
import com.register.app.dto.MessagePayload
import com.register.app.dto.SupportMessageDto
import com.register.app.enums.MessageType
import com.register.app.model.Group
import com.register.app.model.Member
import com.register.app.repository.ChatRepository
import com.register.app.util.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ForumViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val dataStoreManager: DataStoreManager
): ViewModel() {
    private val _supportMessages: MutableLiveData<List<SupportMessageDto>?> = MutableLiveData()
    val supportMessages: LiveData<List<SupportMessageDto>?> = _supportMessages
    private val _chatMessages: MutableLiveData<List<MessageData>?> = MutableLiveData()
    val chatMessages: LiveData<List<MessageData>?> = _chatMessages
    private val _remoteChatMessages: MutableLiveData<ChatMessageResponse?> = MutableLiveData()
    val remoteChatMessages: MutableLiveData<ChatMessageResponse?> = _remoteChatMessages
    private val _selectedGroup: MutableLiveData<Group?> = MutableLiveData()
    val selectedGroup: LiveData<Group?> = _selectedGroup
    private val _errorLiveData: MutableLiveData<String?> = MutableLiveData()
    val errorLiveData: LiveData<String?> = _errorLiveData
    private val _isLoadingLiveData: MutableLiveData<Boolean?> = MutableLiveData()
    val isLoadingLiveData: LiveData<Boolean?> = _isLoadingLiveData
    private val _currentUser: MutableLiveData<String?> = MutableLiveData()
    val currentUser: LiveData<String?> = _currentUser

    init {
        viewModelScope.launch {
            _currentUser.value = dataStoreManager.readUserData()?.fullName
            var initialGroupId: Int? = null
            val groups = dataStoreManager.readUserData()?.groupIds
            if (groups?.isNotEmpty() == true) {
                initialGroupId = groups[0]
            }
            if (initialGroupId != null) {
                val payload = JoinChatPayload(dataStoreManager.readUserData()?.fullName!!, initialGroupId!!)
                loadGroupChats(initialGroupId)
                connectToChat(payload)
            }
        }
    }


    suspend fun fetUserChats() {
        _isLoadingLiveData.value = true
        val userChats = chatRepository.fetchUserChats(selectedGroup.value?.groupId!!)
        _isLoadingLiveData.value = false
        if (userChats?.data?.isNotEmpty() == true) {
            val data = userChats.data
            val messageList = _chatMessages.value
            val newMessageList = mutableListOf<MessageData>()
            newMessageList.addAll(messageList?.toMutableList() ?: mutableListOf())
            data.forEach {
                newMessageList.add(
                    MessageData(
                        it.groupId,
                        it.groupName,
                        it.message,
                        it.membershipId,
                        it.senderName,
                        it.imageUrl,
                        it.sendTime
                    )
                )
            }
            _chatMessages.value = newMessageList
        }
    }

    suspend fun connectToChat(payload: JoinChatPayload) {
        dataStoreManager.readTokenData()?.let { chatRepository.connectToChat(it) }
        chatRepository.subscribe("/app/chat/joinChat", payload) {
            val messageList = _chatMessages.value
            val newMessageList = mutableListOf<MessageData>()
            newMessageList.addAll(messageList?.toMutableList() ?: mutableListOf())
            newMessageList.add(
                MessageData(
                    it.groupId,
                    it.groupName,
                    it.message,
                    it.membershipId,
                    it.senderName,
                    it.imageUrl,
                    it.sendTime
                )
            )
            _chatMessages.postValue(newMessageList)
        }
    }

    private fun transformToChatMessage(it: MessageData) {
        val messageList = _chatMessages.value
        val newMessageList = mutableListOf<MessageData>()
        newMessageList.addAll(messageList?.toMutableList() ?: mutableListOf())
        newMessageList.add(
            MessageData(
                it.groupId,
                it.groupName,
                it.message,
                it.membershipId,
                it.senderName,
                it.imageUrl,
                it.sendTime
            )
        )
        _chatMessages.postValue(newMessageList)
    }

    fun disconnectChat() {
        viewModelScope.launch {
            chatRepository.disconnectChat()
        }
    }

    suspend fun sendMessage(membershipId: String, message: String, group: Group?) {
        val groupId = selectedGroup.value?.groupId?: group?.groupId  //if the user has not selected a group, use the default group
        val groupName = selectedGroup.value?.groupName?: group?.groupName
        val chatMessage = MessagePayload(
            message,
            membershipId,
            dataStoreManager.readUserData()?.fullName!!,
            dataStoreManager.readUserData()?.imageUrl?: "",
            groupName!!,
            groupId!!,
            LocalDateTime.now().toString())
        chatRepository.sendMessage(groupId, chatMessage) {
            //Log.d("SEND_MESSAGE", it.toString())
            //transformToChatMessage(it)
        }
    }

    suspend fun loadGroupChats(groupId: Int?) {
        _isLoadingLiveData.value = true
        val groupChats = groupId.let { chatRepository.fetchUserChats(it!!) }
        _isLoadingLiveData.value = false
        if (groupChats?.data?.isNotEmpty() == true) {
            val data = groupChats.data
            //val messageList = _chatMessages.value
//            val newMessageList = mutableListOf<MessageData>()
//            newMessageList.addAll(messageList?.toMutableList() ?: mutableListOf())
//            newMessageList.addAll(data)
            _chatMessages.value = data
        }
    }

    suspend fun setSelectedGroup(selectedGroup: Group?) {
        _selectedGroup.value = selectedGroup
        loadGroupChats(selectedGroup?.groupId)
    }

    suspend fun sendSupportMessage(userData: Member?, message: String) {
        val messagePayload = SupportMessageDto(
            userData?.emailAddress!!,
            userData.fullName,
            message,
            MessageType.MESSAGE.name, LocalDateTime.now().toString())
        chatRepository.sendSupportMessage(messagePayload){
            val messageList = _supportMessages.value
            val newMessageList = mutableListOf<SupportMessageDto>()
            newMessageList.addAll(messageList?.toMutableList() ?: mutableListOf())
            newMessageList.add(it)
            _supportMessages.postValue(newMessageList)
        }
    }

    suspend fun subscribeToSupport(supportMessageDto: SupportMessageDto) {
        chatRepository.subscribeToSupport(supportMessageDto) {
            val messageList = _supportMessages.value
            val newMessageList = mutableListOf<SupportMessageDto>()
            newMessageList.addAll(messageList?.toMutableList() ?: mutableListOf())
            newMessageList.add(it)
            _supportMessages.value = newMessageList
        }
    }
}