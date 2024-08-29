package com.register.app

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.register.app.dto.ChatMessage
import com.register.app.dto.ChatMessageResponse
import com.register.app.dto.DirectChatMessageData
import com.register.app.dto.DirectChatMessages
import com.register.app.dto.MessageData
import com.register.app.dto.notifications.DirectNotification
import com.register.app.dto.notifications.ElectionNotification
import com.register.app.enums.NotificationType
import com.register.app.model.NotificationModel
import com.register.app.repository.ChatRepository
import com.register.app.util.CHANNEL_ID
import com.register.app.util.DataStoreManager
import com.register.app.util.ELECTION_NOTIFICATION_REQUEST_CODE
import com.register.app.util.FORUM
import com.register.app.util.FORUM_NOTIFICATION_REQUEST_CODE
import com.register.app.util.GENERAL_NOTIFICATION_REQUEST_CODE
import com.register.app.util.NOTIFICATION
import com.register.app.util.NOTIFICATION_CONTENT
import com.register.app.util.NOTIFICATION_TITLE
import com.register.app.util.NOTIFICATION_TYPE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : FirebaseMessagingService() {
    private val atomicInt = AtomicInteger(0)

    @Inject
    lateinit var chatRepository: ChatRepository
    @Inject
    lateinit var dataStoreManager: DataStoreManager

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                dataStoreManager.writeFirebaseToken(token)
                Log.d("FCM", "Token: $token")
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCM", "Message: ${message.data}")
        val data = Gson().fromJson(Gson().toJsonTree(message.data), NotificationModel::class.java)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        displayNotification(data, notificationManager)
    }

    private fun displayNotification(
        data: NotificationModel?,
        notificationManager: NotificationManager
    ) {
        var chatMessage: MessageData? = null
        var directChat: DirectChatMessageData? = null
        var election: ElectionNotification? = null
        if (data?.type == NotificationType.CHAT.name) {
            chatMessage = Gson().fromJson(data.content, MessageData::class.java)
        }
        if (data?.type == NotificationType.DIRECT_CHAT.name) {
            directChat = Gson().fromJson(data.content, DirectChatMessageData::class.java)
        }

        if (data?.type == NotificationType.ELECTION.name) {
            election = Gson().fromJson(data.content, ElectionNotification::class.java)
        }

        val chatIntent = Intent(this, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(FORUM, "forum")
            putExtra(NOTIFICATION_TYPE, data?.type)
            putExtra(NOTIFICATION_TITLE, data?.title)
            putExtra(NOTIFICATION_CONTENT, data?.content)
        }

        val generalIntent = Intent(this, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(NOTIFICATION, "notification")
            putExtra(NOTIFICATION_TYPE, data?.type)
            putExtra(NOTIFICATION_TITLE, data?.title)
            putExtra(NOTIFICATION_CONTENT, data?.content)
        }

        val electionIntent = Intent(this, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(NOTIFICATION, "elections")
            putExtra(NOTIFICATION_TYPE, data?.type)
            putExtra(NOTIFICATION_TITLE, data?.title)
            putExtra(NOTIFICATION_CONTENT, data?.content)
        }

        val adminIntent = Intent(this, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(NOTIFICATION, "admin")
            putExtra(NOTIFICATION_TYPE, data?.type)
            putExtra(NOTIFICATION_TITLE, data?.title)
            putExtra(NOTIFICATION_CONTENT, data?.content)
        }

        val directChatIntent = Intent(this, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(NOTIFICATION, "admin")
            putExtra(NOTIFICATION_TYPE, data?.type)
            putExtra(NOTIFICATION_TITLE, data?.title)
            putExtra(NOTIFICATION_CONTENT, data?.content)
        }

        val directChatPendingIntent = PendingIntent.getActivity(this, GENERAL_NOTIFICATION_REQUEST_CODE, directChatIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val adminPendingIntent = PendingIntent.getActivity(this, GENERAL_NOTIFICATION_REQUEST_CODE, adminIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val chatPendingIntent = PendingIntent.getActivity(this, FORUM_NOTIFICATION_REQUEST_CODE, chatIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val generalPendingIntent = PendingIntent.getActivity(this, GENERAL_NOTIFICATION_REQUEST_CODE, generalIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val electionPendingIntent = PendingIntent.getActivity(this, ELECTION_NOTIFICATION_REQUEST_CODE, electionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        when(data?.type) {
            NotificationType.CHAT.name -> {
                val channelId = getString(R.string.register_notification_channel_id)
                val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                val forumNotification = NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.register_logo)
                    .setContentTitle(data.title)
                    .setContentText(chatMessage?.message?: data.content)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(chatPendingIntent)
                    .setSound(defaultSoundUri)
                    .setAutoCancel(true)

                coroutineScope.launch {
                    if(chatMessage?.senderName != dataStoreManager.readUserData()?.fullName) {
                        notificationManager.notify(getUniqueNotificationId(), forumNotification.build())
                    }
                }
            }

            NotificationType.DIRECT_CHAT.name -> {
                val channelId = getString(R.string.register_notification_channel_id)
                val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                val directChatNotification = NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.register_logo)
                    .setContentTitle(data.title)
                    .setContentText(directChat?.message?: data.content)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(directChatPendingIntent)
                    .setSound(defaultSoundUri)
                    .setAutoCancel(true)

                notificationManager.notify(getUniqueNotificationId(), directChatNotification.build())
            }

            NotificationType.GENERAL.name -> {
                val channelId = getString(R.string.register_notification_channel_id)
                val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val generalNotification = NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.register_logo)
                    .setContentTitle(data.title)
                    .setContentText(data.content)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(generalPendingIntent)
                    .setSound(defaultSoundUri)
                    .setAutoCancel(true)

                notificationManager.notify(getUniqueNotificationId(), generalNotification.build())
            }

            NotificationType.ELECTION.name -> {
                val channelId = getString(R.string.register_notification_channel_id)
                val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val generalNotification = NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.register_logo)
                    .setContentTitle(data.title)
                    .setContentText("${election?.electionTitle} is now ${election?.electionEvent}")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(electionPendingIntent)
                    .setSound(defaultSoundUri)
                    .setAutoCancel(true)

                notificationManager.notify(getUniqueNotificationId(), generalNotification.build())
            }

            NotificationType.ADMIN.name -> {
                val channelId = getString(R.string.register_notification_channel_id)
                val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val adminNotification = NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.register_logo)
                    .setContentTitle(data.title)
                    .setContentText(data.content)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(adminPendingIntent)
                    .setSound(defaultSoundUri)
                    .setAutoCancel(true)

                notificationManager.notify(getUniqueNotificationId(), adminNotification.build())
            }

            else -> {}
        }
    }

    private fun getUniqueNotificationId(): Int {
        return atomicInt.getAndIncrement()
    }
}