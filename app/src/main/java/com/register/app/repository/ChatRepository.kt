package com.register.app.repository

import com.register.app.dto.FirebaseTokenModel
import com.register.app.dto.GenericResponse
import com.register.app.dto.JoinChatPayload
import com.register.app.dto.MessageData
import com.register.app.dto.MessagePayload
import com.register.app.dto.UserChatMessages

interface ChatRepository {
    suspend fun checkTokenWithDeviceId(deviceId: String, token: String): GenericResponse
    suspend fun updateFcmToken(firebaseTokenModel: FirebaseTokenModel): GenericResponse
    suspend fun connectToChat(jwtToken: String)
    suspend fun subscribe(path: String, payload: JoinChatPayload, callback: (MessageData) -> Unit)
    suspend fun sendMessage(groupId: Int, message: MessagePayload, callback: (MessageData) -> Unit?)
    suspend fun disconnectChat()
    suspend fun fetchUserChats(groupId: Int): UserChatMessages

}
