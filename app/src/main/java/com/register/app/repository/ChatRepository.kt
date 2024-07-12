package com.register.app.repository

import com.register.app.dto.ChatMessage
import com.register.app.dto.ChatMessageResponse
import com.register.app.dto.FirebaseTokenModel
import com.register.app.dto.GenericResponse
import com.register.app.dto.UserChatMessages

interface ChatRepository {
    suspend fun checkTokenWithDeviceId(deviceId: String, token: String): GenericResponse
    suspend fun updateFcmToken(firebaseTokenModel: FirebaseTokenModel): GenericResponse
    suspend fun connectToChat()
    suspend fun subscribe(path: String, toUsername: String, callback: (ChatMessageResponse) -> Unit)
    suspend fun sendMessage(username: String, message: ChatMessage, callback: (ChatMessageResponse) -> Unit?)
    suspend fun disconnectChat()
    suspend fun fetchUserChats(username: String): UserChatMessages

}
