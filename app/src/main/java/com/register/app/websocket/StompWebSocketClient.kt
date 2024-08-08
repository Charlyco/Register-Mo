package com.register.app.websocket

import com.register.app.dto.JoinChatPayload
import com.register.app.dto.MessageData
import com.register.app.dto.SupportMessageDto

interface StompWebSocketClient {
    suspend fun connect(jwtToken: String)
    suspend fun subscribe(
        path: String,
        payload: JoinChatPayload,
        callback: (topicMessage: MessageData) -> Unit
    )
    suspend fun sendMessage(path: String, message: String, onSend: (path: String, message: String) -> Unit)
    suspend fun close()

    suspend fun subscribeToSupport(
        payload: SupportMessageDto,
        callback: (supportMessage: SupportMessageDto) -> Unit
    )

    suspend fun sendSupportMessage(message: String, onSend: (path: String, message: String) -> Unit)
}