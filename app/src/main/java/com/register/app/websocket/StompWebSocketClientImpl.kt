package com.register.app.websocket

import android.util.Log
import com.google.gson.Gson
import com.register.app.dto.DirectChatMessageData
import com.register.app.dto.JoinChatPayload
import com.register.app.dto.MessageData
import com.register.app.dto.SupportMessageDto
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.StompHeader


class StompWebSocketClientImpl(url: String) : StompWebSocketClient {
    private var client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)

    private val compositeDisposable = CompositeDisposable()

    private var subscription: Disposable? = null
    private var supportSubscription: Disposable? = null
    private var directChatSubscription: Disposable? = null

    override suspend fun connect(jwtToken: String) {
        val headers: MutableList<StompHeader> = ArrayList()
        headers.add(StompHeader("Authorization", "Bearer $jwtToken"))
        client.connect(headers)
        client.withClientHeartbeat(1000 * 30)
        val disposable = client.lifecycle().subscribe {
            Log.d("STOMP", "LIFECYCLE --- ${it.type}: ${it?.exception?.message}")
        }
        compositeDisposable.add(disposable)
    }

    override suspend fun subscribe(
        path: String,
        payload: JoinChatPayload,
        callback: (topicMessage: MessageData) -> Unit
    ) {
        if (subscription != null) {
            if (!subscription?.isDisposed!!) {
                subscription?.dispose()
            }
        }
        subscription = client.topic("/forum/${payload.groupId}").subscribe({ message ->
            val chatMessage = Gson().fromJson(message.payload, MessageData::class.java)
            callback(chatMessage)
        }, { throwable ->
            Log.d("STOMPERROR", throwable.message ?: "Unknown error")
            client.send(path, Gson().toJson(payload))
        })

    }

    override suspend fun subscribeToSupport(
        payload: SupportMessageDto,
        callback: (supportMessageDto: SupportMessageDto) -> Unit
    ) {
        if (supportSubscription != null) {
            if (!supportSubscription?.isDisposed!!) {
                supportSubscription?.dispose()
            }
        }
        supportSubscription = client.topic("/support/${payload.email}").subscribe({ message ->
            val chatMessage = Gson().fromJson(message.payload, SupportMessageDto::class.java)
            callback(chatMessage)
        }, { throwable ->
            Log.d("STOMPERROR", throwable.message ?: "Unknown error")
            client.send("/app/customer/newTicket", Gson().toJson(payload))
        })

    }

    override suspend fun sendMessage(
        path: String,
        message: String,
        onSend: (path: String, message: String) -> Unit
    ) {
        val disposable = client.send(path, message).subscribe({
            Log.d("REGISTER_STOMP", "SENT")
            onSend(path, message)
        }, { throwable ->
            Log.d("STOMPERROR", throwable.message ?: "Unknown error")
        })
        compositeDisposable.add(disposable)
    }

    override suspend fun sendSupportMessage(
        message: String,
        onSend: (path: String, message: String) -> Unit
    ) {
        val path = "/app/customer/sendMessage"
        val disposable = client.send(path, message).subscribe({
            Log.d("REGISTER_STOMP", "SENT")
            onSend(path, message)
        }, { throwable ->
            Log.d("STOMPERROR", throwable.message ?: "Unknown error")
        })
        compositeDisposable.add(disposable)
    }

    override suspend fun subscribeToDirectChat(
        directChatMessageData: DirectChatMessageData,
        callback: (message: DirectChatMessageData) -> Unit
    ) {
        if (directChatSubscription != null) {
            if (!directChatSubscription?.isDisposed!!) {
                directChatSubscription?.dispose()
            }
        }
        directChatSubscription = client.topic("/direct/${directChatMessageData.senderId}").subscribe({ message ->
            val chatMessage = Gson().fromJson(message.payload, DirectChatMessageData::class.java)
            callback(chatMessage)
        }, { throwable ->
            Log.d("STOMPERROR", throwable.message ?: "Unknown error")
            client.send("/app/direct/initiate", Gson().toJson(directChatMessageData))
        })
    }

    override suspend fun sendDirectChat(
        directChatMessage: DirectChatMessageData,
        onSend: (path: String, message: String) -> Unit
    ) {
        val path = "/app/direct/send"
        val disposable = client.send(path, Gson().toJson(directChatMessage)).subscribe({
            Log.d("REGISTER_STOMP", "SENT")
            onSend(path, Gson().toJson(directChatMessage))
        }, { throwable ->
            Log.d("STOMPERROR", throwable.message ?: "Unknown error")
        })
        compositeDisposable.add(disposable)
    }

    override suspend fun close() {
        client.disconnect()
        compositeDisposable.dispose()
    }



    companion object {
        private var INSTANCE: StompWebSocketClientImpl? = null

        fun getInstance(socketUrl: String): StompWebSocketClientImpl {
            if (INSTANCE == null) {
                INSTANCE = StompWebSocketClientImpl(socketUrl)
            }

            return INSTANCE!!
        }
    }
}