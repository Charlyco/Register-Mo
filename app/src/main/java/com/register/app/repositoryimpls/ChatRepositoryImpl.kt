package com.register.app.repositoryimpls

import android.util.Log
import com.google.gson.Gson
import com.register.app.api.ChatService
import com.register.app.dto.FirebaseTokenModel
import com.register.app.dto.GenericResponse
import com.register.app.dto.JoinChatPayload
import com.register.app.dto.MessageData
import com.register.app.dto.MessagePayload
import com.register.app.dto.SupportMessageDto
import com.register.app.dto.UserChatMessages
import com.register.app.repository.ChatRepository
import com.register.app.websocket.StompWebSocketClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ChatRepositoryImpl @Inject constructor(
    private val chatService: ChatService,
    private val stompWebSocketClient: StompWebSocketClient
): ChatRepository {

    override suspend fun checkTokenWithDeviceId(deviceId: String, token: String): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = chatService.verifyFirebaseToken(FirebaseTokenModel(deviceId, token))
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun updateFcmToken(firebaseTokenModel: FirebaseTokenModel): GenericResponse {
        return suspendCoroutine { continuation ->
            val call = chatService.registerFirebaseToken(firebaseTokenModel)
            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun connectToChat(jwtToken: String) {
        stompWebSocketClient.connect(jwtToken)
    }

    override suspend fun subscribe(
        path: String,
        payload: JoinChatPayload,
        callback: (MessageData) -> Unit) {
        stompWebSocketClient.subscribe(path, payload) { chatMessage ->
            //val newMessage = Gson().fromJson(stompMessage, ChatMessageModel::class.java)
            callback(chatMessage)
        }
    }

    override suspend fun subscribeToSupport(
        payload: SupportMessageDto,
        callback: (SupportMessageDto) -> Unit
    ) {
        stompWebSocketClient.subscribeToSupport(payload) { supportMessage ->
            callback(supportMessage)
        }
    }

    override suspend fun sendMessage(
        groupId: Int,
        message: MessagePayload,
        callback: (MessageData) -> Unit?) {
        val jsonString = Gson().toJson(message)
        stompWebSocketClient.sendMessage("/app/chat/newMessage", jsonString) { topic1, message1 ->
            val response = Gson().fromJson(message1, MessageData::class.java)
            Log.d("MESSAGE", response.toString())
            callback(
                MessageData(
                    response.message,
                    response.membershipId,
                    response.senderName,
                    response.imageUrl,
                    response.sendTime,
                )
            )
        }
    }

    override suspend fun sendSupportMessage(
        message: SupportMessageDto,
        callback: (SupportMessageDto) -> Unit?
    ) {
        val jsonString = Gson().toJson(message)
        stompWebSocketClient.sendMessage("/app/customer/sendMessage", jsonString) { topic1, message1 ->
            val response = Gson().fromJson(message1, SupportMessageDto::class.java)
            Log.d("MESSAGE", response.toString())
            callback(
                SupportMessageDto(
                    response.email,
                    response.fullName,
                    response.message,
                    response.messageType,
                    response.dateTime
                )
            )
        }
    }

    override suspend fun disconnectChat() {
        stompWebSocketClient.close()
    }

    override suspend fun fetchUserChats(groupId: Int): UserChatMessages {
        return suspendCoroutine { continuation ->
            val call = chatService.getUserChatMessages(groupId)
            call.enqueue(object : Callback<UserChatMessages> {
                override fun onResponse(
                    call: Call<UserChatMessages>,
                    response: Response<UserChatMessages>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<UserChatMessages>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}
