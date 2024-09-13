package com.register.app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.register.app.dto.DirectChatMessageData
import com.register.app.dto.JoinChatPayload
import com.register.app.dto.MessageData
import com.register.app.dto.MessagePayload
import com.register.app.dto.SupportMessageDto
import com.register.app.enums.MessageType
import com.register.app.model.DirectChatContact
import com.register.app.model.Group
import com.register.app.model.Member
import com.register.app.model.MembershipDto
import com.register.app.repository.AuthRepository
import com.register.app.repository.ChatRepository
import com.register.app.util.DataStoreManager
import com.register.app.util.SUPPORT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ForumViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val dataStoreManager: DataStoreManager
): ViewModel() {
    private val _directChatList: MutableLiveData<List<DirectChatContact>?> = MutableLiveData()
    val directChatList: LiveData<List<DirectChatContact>?> = _directChatList
    private val _remoteUser: MutableLiveData<MembershipDto> = MutableLiveData()
    val remoteUser: LiveData<MembershipDto> = _remoteUser
    private val _supportMessages: MutableLiveData<List<SupportMessageDto>?> = MutableLiveData()
    val supportMessages: LiveData<List<SupportMessageDto>?> = _supportMessages
    private val _chatMessages: MutableLiveData<List<MessageData>?> = MutableLiveData()
    val chatMessages: LiveData<List<MessageData>?> = _chatMessages
    private val _directChatMessages: MutableLiveData<List<DirectChatMessageData>?> = MutableLiveData()
    val directChatMessages: MutableLiveData<List<DirectChatMessageData>?> = _directChatMessages
    private val _selectedGroup: MutableLiveData<Group?> = MutableLiveData()
    val selectedGroup: LiveData<Group?> = _selectedGroup
    private val _errorLiveData: MutableLiveData<String?> = MutableLiveData()
    val errorLiveData: LiveData<String?> = _errorLiveData
    private val _isLoadingLiveData: MutableLiveData<Boolean?> = MutableLiveData()
    val isLoadingLiveData: LiveData<Boolean?> = _isLoadingLiveData
    private val _currentUser: MutableLiveData<String?> = MutableLiveData()
    val currentUser: LiveData<String?> = _currentUser
    private val _messageToReply: MutableLiveData<MessageData?> = MutableLiveData()
    val messageToReply: LiveData<MessageData?> = _messageToReply

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
            _directChatList.value = chatRepository.fetchChatContactList()
        }
    }

suspend fun subScribeToDirectChat(recipientEmail: String, group: Group) {
        val currentUser = dataStoreManager.readUserData()
        val recipientId = group.memberList?.find { it.emailAddress == recipientEmail }?.membershipId
        val senderId = group.memberList?.find { it.emailAddress == currentUser?.emailAddress }?.membershipId
        val chatMessage = DirectChatMessageData(
            null,
            null,
            currentUser?.fullName!!,
            senderId!!,
            recipientId!!,
            recipientEmail,
            currentUser.imageUrl,
            LocalDateTime.now().toString(),
            group.groupName,
            group.groupId,
            dataStoreManager.readTokenData()
        )
        chatRepository.subScribeToDirectChat(chatMessage) {
            val messageList = _directChatMessages.value
            val newMessageList = mutableListOf<DirectChatMessageData>()
            newMessageList.addAll(messageList?.toMutableList() ?: mutableListOf())
            newMessageList.add(it)
            _directChatMessages.postValue(newMessageList)
        }
    }

    suspend fun sendDirectMessage(recipientEmail: String, group: Group, message: String) {
        val currentUser = dataStoreManager.readUserData()
        val recipientId = group.memberList?.find { it.emailAddress == recipientEmail }?.membershipId
        val senderId = group.memberList?.find { it.emailAddress == currentUser?.emailAddress }?.membershipId
        val chatMessage = DirectChatMessageData(
            null,
            message,
            currentUser?.fullName!!,
            senderId!!,
            recipientId!!,
            recipientEmail,
            currentUser.imageUrl,
            LocalDateTime.now().toString(),
            group.groupName,
            group.groupId,
            dataStoreManager.readTokenData()
        )
        chatRepository.sendDirectMessage(chatMessage) {
            val messageList = directChatMessages.value
            val newMessageList = mutableListOf<DirectChatMessageData>()
            newMessageList.addAll(messageList?.toMutableList() ?: mutableListOf())
            newMessageList.add(it)
            _directChatMessages.postValue(newMessageList)
        }
        saveChatContactFromLocalMessage(chatMessage, recipientEmail)
    }

    suspend fun fetUserChats(recipientId: String, senderId: String) {
        _isLoadingLiveData.value = true
        _directChatMessages.postValue(mutableListOf())
        val userChats = chatRepository.fetchUserChats(recipientId, senderId)
        _isLoadingLiveData.value = false
        if (userChats?.data?.isNotEmpty() == true) {
            val data = userChats.data
            val newMessageList = mutableListOf<DirectChatMessageData>()
           // newMessageList.addAll(messageList?.toMutableList() ?: mutableListOf())
            data.forEach {
                newMessageList.add(
                    DirectChatMessageData(
                        it.id,
                        it.message,
                        it.senderName,
                        it.senderId,
                        it.recipientId,
                        it.recipientEmail,
                        it.imageUrl,
                        it.sendTime,
                        it.groupName,
                        it.groupId,
                        null
                    )
                )
            }
            _directChatMessages.postValue(newMessageList)
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
                    it.id,
                    it.groupId,
                    it.groupName,
                    it.message,
                    it.membershipId,
                    it.senderName,
                    it.imageUrl,
                    it.sendTime,
                    it.originalMessageId
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
                it.id,
                it.groupId,
                it.groupName,
                it.message,
                it.membershipId,
                it.senderName,
                it.imageUrl,
                it.sendTime,
                it.originalMessageId
            )
        )
        _chatMessages.postValue(newMessageList)
    }

    fun disconnectChat() {
        viewModelScope.launch {
            chatRepository.disconnectChat()
        }
    }

    suspend fun sendMessageToForum(
        membershipId: String,
        message: String,
        group: Group?,
        messageToReply: MessageData?
    ) {
        val groupId = selectedGroup.value?.groupId?: group?.groupId  //if the user has not selected a group, use the default group
        val groupName = selectedGroup.value?.groupName?: group?.groupName
        val chatMessage = MessagePayload(
            message,
            membershipId,
            dataStoreManager.readUserData()?.fullName!!,
            dataStoreManager.readUserData()?.imageUrl?: "",
            groupName!!,
            groupId!!,
            LocalDateTime.now().toString(),
            messageToReply?.id!!)
        chatRepository.sendMessage(groupId, chatMessage) {
        }
    }

    suspend fun loadGroupChats(groupId: Int?) {
        _isLoadingLiveData.value = true
        _chatMessages.value = mutableListOf()
        val groupChats = groupId.let { chatRepository.getForumMessages(it!!) }
        _isLoadingLiveData.value = false
        if (groupChats?.data?.isNotEmpty() == true) {
            val data = groupChats.data
            _chatMessages.value = data
        }
    }

    suspend fun setSelectedGroup(selectedGroup: Group?) {
        _selectedGroup.value = selectedGroup
        loadGroupChats(selectedGroup?.groupId)
    }

    suspend fun sendSupportMessage(userData: Member?, message: String) {
        val user = dataStoreManager.readUserData()
        val messagePayload = SupportMessageDto(
            null,
            userData?.emailAddress!!,
            userData.fullName,
            message,
            MessageType.MESSAGE.name,
            LocalDateTime.now().toString(),
            user?.emailAddress!!,
            SUPPORT)
        chatRepository.sendSupportMessage(messagePayload){
            val messageList = _supportMessages.value
            val newMessageList = mutableListOf<SupportMessageDto>()
            newMessageList.addAll(messageList?.toMutableList() ?: mutableListOf())
            newMessageList.add(it)
            _supportMessages.postValue(newMessageList)
        }
    }

    suspend fun subscribeToSupport() {
        val user = dataStoreManager.readUserData();
        val message = SupportMessageDto(
            null,
            user?.emailAddress!!,
            user.fullName,
            "",
            MessageType.JOIN.name,
            LocalDateTime.now().toString(),
            user.emailAddress,
            SUPPORT
        )
        chatRepository.subscribeToSupport(message) {
            val messageList = _supportMessages.value
            val newMessageList = mutableListOf<SupportMessageDto>()
            newMessageList.addAll(messageList?.toMutableList() ?: mutableListOf())
            newMessageList.add(it)
            _supportMessages.value = newMessageList
        }
    }

    fun setRemoteUser(remoteUser: MembershipDto) {
        _remoteUser.value = remoteUser
    }

    fun captureMessageToReply(messageData: MessageData?) {
        _messageToReply.value = messageData
        Log.d("REPLY: ", messageToReply.value.toString())
    }

    suspend fun saveDirectChatContactFromNotification(data: DirectChatMessageData?) {
        val chatContact = DirectChatContact(
            null,
            data?.groupId,
            data?.groupName,
            data?.senderId,
            data?.recipientId,
            data?.senderName,
            data?.imageUrl,
            data?.sendTime
        )
        chatRepository.saveChatContact(chatContact)
    }

    private suspend fun saveChatContactFromLocalMessage(
        chatMessage: DirectChatMessageData,
        recipientEmail: String
    ) {
        val user = authRepository.reloadUserData(recipientEmail)
        val chatContact = DirectChatContact(
            null,
            chatMessage.groupId,
            chatMessage.groupName,
            chatMessage.recipientId,
            chatMessage.senderId,
            user?.fullName,
            user?.imageUrl,
            chatMessage.sendTime
        )
        Log.d("SAVING IN VIEWMODEL", chatContact.contactName?: "No User")
        chatRepository.saveChatContact(chatContact)
    }

    suspend fun fetchDirectChatList() {
        val chats = chatRepository.fetchChatContactList()
        Log.d("CHAT_LIST", chats.toString())
        _directChatList.value = chats
    }
}