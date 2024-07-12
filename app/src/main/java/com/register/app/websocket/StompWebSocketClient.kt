package com.register.app.websocket

import com.register.app.dto.ChatMessageResponse

interface StompWebSocketClient {
    suspend fun connect()
    suspend fun subscribe(path: String, callback: (topicMessage: ChatMessageResponse) -> Unit)
    suspend fun sendMessage(path: String, message: String, onSend: (path: String, message: String) -> Unit)
    suspend fun close()
}