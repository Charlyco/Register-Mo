package com.register.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.register.app.dto.ChatMessage
import com.register.app.dto.ChatMessageResponse
import com.register.app.repository.ChatRepository
import com.register.app.util.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ForumViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val dataStoreManager: DataStoreManager
): ViewModel() {
    private val _chatMessages: MutableLiveData<List<ChatMessageResponse>> = MutableLiveData()
    val chatMessages: LiveData<List<ChatMessageResponse>> = _chatMessages
    private val _remoteChatMessages: MutableLiveData<ChatMessageResponse?> = MutableLiveData()
    val remoteChatMessages: MutableLiveData<ChatMessageResponse?> = _remoteChatMessages

    private val _currentRemoteUser: MutableLiveData<String?> = MutableLiveData()
    val currentRemoteUser: LiveData<String?> = _currentRemoteUser

    suspend fun fetUserChats() {
        val userChats = currentRemoteUser.value?.let { chatRepository.fetchUserChats(it) }
        if (userChats?.data?.isNotEmpty() == true) {
            val data = userChats.data
            val messageList = _chatMessages.value
            val newMessageList = mutableListOf<ChatMessageResponse>()
            newMessageList.addAll(messageList?.toMutableList() ?: mutableListOf())
            data.forEach {
                newMessageList.add(
                    ChatMessageResponse(
                        it.url,
                        it.time,
                        it.id,
                        it.message,
                        it.username,
                        it.mine
                    )
                )
            }
            _chatMessages.value = newMessageList
        }
    }

    suspend fun connectToChat(username: String?) {
        chatRepository.connectToChat()
        if (username != null) {
            chatRepository.subscribe("/topic/messages/", username) {
                val messageList = _chatMessages.value
                val newMessageList = mutableListOf<ChatMessageResponse>()
                newMessageList.addAll(messageList?.toMutableList() ?: mutableListOf())
                newMessageList.add(
                    ChatMessageResponse(
                        it.url,
                        LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")).toString(),
                        it.id,
                        it.message,
                        it.username,
                        it.isMine
                    )
                )
                _chatMessages.postValue(newMessageList)
            }
        }
    }

    private fun transformToChatMessage(it: ChatMessageResponse) {
        val messageList = _chatMessages.value
        val newMessageList = mutableListOf<ChatMessageResponse>()
        newMessageList.addAll(messageList?.toMutableList() ?: mutableListOf())
        newMessageList.add(
            ChatMessageResponse(
                it.url,
                it.time,
                it.id,
                it.message,
                it.username,
                it.isMine
            )
        )
        _chatMessages.postValue(newMessageList)
    }

    fun disconnectChat() {
        viewModelScope.launch {
            chatRepository.disconnectChat()
        }
    }

    suspend fun sendMessage(toUsername: String, message: String) {
        val chatMessage = ChatMessage(message, dataStoreManager.readUserData()?.userName!!, "", toUsername)
        chatRepository.sendMessage(toUsername, chatMessage) {
            transformToChatMessage(it)
        }
    }

}