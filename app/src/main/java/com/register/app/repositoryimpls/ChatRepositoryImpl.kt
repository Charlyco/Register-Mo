package com.register.app.repositoryimpls

import android.util.Log
import com.google.gson.Gson
import com.register.app.api.ChatService
import com.register.app.dto.ChatMessage
import com.register.app.dto.ChatMessageResponse
import com.register.app.dto.FirebaseTokenModel
import com.register.app.dto.GenericResponse
import com.register.app.dto.UserChatMessages
import com.register.app.repository.ChatRepository
import com.register.app.websocket.StompWebSocketClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalTime
import java.time.format.DateTimeFormatter
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

    override suspend fun connectToChat() {
        stompWebSocketClient.connect()
    }

    override suspend fun subscribe(path: String,
                                   toUsername: String,
                                   callback: (ChatMessageResponse) -> Unit) {
        stompWebSocketClient.subscribe("$path$toUsername") { chatMessage ->
            //val newMessage = Gson().fromJson(stompMessage, ChatMessageModel::class.java)
            callback(chatMessage)
        }
    }
    override suspend fun sendMessage(username: String,
                                     message: ChatMessage,
                                     callback: (ChatMessageResponse) -> Unit?) {
        val jsonString = Gson().toJson(message)
        stompWebSocketClient.sendMessage("/app/chat/$username", jsonString) { topic1, message1 ->
            val response = Gson().fromJson(message1, ChatMessageResponse::class.java)
            Log.d("MESSAGE", response.toString())
            callback(
                ChatMessageResponse(
                    response.url,
                    LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")).toString(),
                    response.id,
                    response.message,
                    response.username,
                    true,
                )
            )
        }
    }

    override suspend fun disconnectChat() {
        stompWebSocketClient.close()
    }

    override suspend fun fetchUserChats(username: String): UserChatMessages {
        return suspendCoroutine { continuation ->
            val call = chatService.getUserChatMessages(username)
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
