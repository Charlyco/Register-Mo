package com.register.app.repositoryimpls

import android.util.Log
import com.google.gson.Gson
import com.register.app.api.ChatService
import com.register.app.dto.DirectChatMessageData
import com.register.app.dto.DirectChatMessages
import com.register.app.dto.FirebaseTokenModel
import com.register.app.dto.GenericResponse
import com.register.app.dto.JoinChatPayload
import com.register.app.dto.MessageData
import com.register.app.dto.MessagePayload
import com.register.app.dto.SupportMessageDto
import com.register.app.dto.ForumMessages
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
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(GenericResponse("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(GenericResponse("Please check Internet connection and try again", false, null))
                        }
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
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume(GenericResponse("Invalid Credentials", false, null))
                            }
                            500 -> continuation.resume(GenericResponse("Please check Internet connection and try again", false, null))
                        }
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
                    response.groupId,
                    response.groupName,
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
        stompWebSocketClient.sendSupportMessage(jsonString) { topic1, message1 ->
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

    override suspend fun fetchUserChats(recipientId: String, senderId: String): DirectChatMessages? {
        return suspendCoroutine { continuation ->
            val call = chatService.getUserChatMessages(recipientId, senderId)
            call.enqueue(object : Callback<DirectChatMessages?> {
                override fun onResponse(
                    call: Call<DirectChatMessages?>,
                    response: Response<DirectChatMessages?>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume( null)
                            }
                            500 -> continuation.resume( null)
                        }
                    }
                }

                override fun onFailure(call: Call<DirectChatMessages?>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun getForumMessages(groupId: Int): ForumMessages? {
        return suspendCoroutine { continuation ->
            val call = chatService.getForumMessages(groupId)
            call.enqueue(object : Callback<ForumMessages?> {
                override fun onResponse(
                    call: Call<ForumMessages?>,
                    response: Response<ForumMessages?>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()!!)
                    }else{
                        val responseCode = response.code()
                        when (responseCode) {
                            401 -> {
                                continuation.resume( null)
                            }
                            500 -> continuation.resume( null)
                        }
                    }
                }

                override fun onFailure(call: Call<ForumMessages?>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun subScribeToDirectChat(
        directChatMessageData: DirectChatMessageData,
        callback: (DirectChatMessageData) -> Unit
    ) {
        stompWebSocketClient.subscribeToDirectChat(directChatMessageData) { chatMessage ->
            callback(chatMessage)
        }
    }

    override suspend fun sendDirectMessage(
        chatMessageData: DirectChatMessageData,
        callback: (DirectChatMessageData) -> Unit
    ) {
        val jsonString = Gson().toJson(chatMessageData)
        stompWebSocketClient.sendDirectChat(chatMessageData) { topic1, message1 ->
            val response = Gson().fromJson(message1, DirectChatMessageData::class.java)
            Log.d("MESSAGE", response.toString())
            callback(
                DirectChatMessageData(
                    response.id,
                    response.message,
                    response.senderName,
                    response.senderId,
                    response.recipientId,
                    response.recipientEmail,
                    response.imageUrl,
                    response.sendTime,
                    response.groupName,
                    response.groupId,
                    null
                )
            )
        }
    }


}
