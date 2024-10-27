package com.register.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.register.app.dto.DirectChatMessageData
import com.register.app.dto.MessageData
import com.register.app.dto.notifications.ElectionNotification
import com.register.app.enums.NotificationType
import com.register.app.model.NotificationModel
import com.register.app.ui.theme.RegisterTheme
import com.register.app.util.DataStoreManager
import com.register.app.util.NOTIFICATIONS
import com.register.app.util.NOTIFICATION_CONTENT
import com.register.app.util.NOTIFICATION_REQUEST_CODE
import com.register.app.util.NOTIFICATION_TITLE
import com.register.app.util.NOTIFICATION_TYPE
import com.register.app.util.Utils
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.ForumViewModel
import com.register.app.viewmodel.GroupViewModel
import com.register.app.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var dataStoreManager: DataStoreManager
    val homeViewModel: HomeViewModel by viewModels()
    val groupViewModel: GroupViewModel by viewModels()
    val forumViewModel: ForumViewModel by viewModels()
    val authViewModel: AuthViewModel by viewModels()
    val activityViewModel: ActivityViewModel by viewModels()
    val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { result ->
            val jpegImage = result?.let { Utils.convertBitmapToJPEG(applicationContext, it) }
            lifecycleScope.launch { activityViewModel.uploadEvidenceOfPaymentFromCamera(jpegImage) }

        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch { Utils.createDeviceId(dataStoreManager) }
        startMyFirebaseMessagingService()
        askForPermissions()
        handleDeepLink(intent)
        setContent {
            RegisterTheme {
                RegisterAppNavHost(this, dataStoreManager, takePicture)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
        if (authViewModel.userLideData.value != null) {
                authViewModel.getUserDetails()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        lifecycleScope.launch {
            if (authViewModel.userLideData.value != null) {
                authViewModel.getUserDetails()
            }
        }
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        if (intent != null && intent.action == Intent.ACTION_VIEW) {
            val notificationType = intent.getStringExtra(NOTIFICATION_TYPE)

            when (notificationType) {
                NotificationType.CHAT.name -> {
                    val content = intent.getStringExtra(NOTIFICATION_CONTENT)
                    if (content != null) {
                        val data = Gson().fromJson(content, MessageData::class.java)
                        lifecycleScope.launch {
                            homeViewModel.setHomeDestination("forum")
                            val group = groupViewModel.reloadGroup(data.groupId)
                            forumViewModel.setSelectedGroup(group)
                        }
                    }
                }

                NotificationType.DIRECT_CHAT.name -> {
                    val content = intent.getStringExtra(NOTIFICATION_CONTENT)
                    if (content != null) {
                        val data = Gson().fromJson(content, DirectChatMessageData::class.java)
                        lifecycleScope.launch {
                            val group = groupViewModel.reloadGroup(data.groupId)
                            val remoteUser =
                                group?.memberList?.find { it.membershipId == data.senderId }
                            if (remoteUser != null) {
                                homeViewModel.setHomeDestination("admin_chat/${remoteUser.emailAddress}")
                                forumViewModel.setRemoteUser(remoteUser)
                                forumViewModel.fetUserChats(data.recipientId!!, data.senderId!!)
                                forumViewModel.subScribeToDirectChat(remoteUser.emailAddress, group)
                                forumViewModel.saveDirectChatContactFromNotification(data)
                            }
                        }
                    }
                }

                NotificationType.GENERAL.name -> {
                    val type = intent.getStringExtra(NOTIFICATION_TYPE)
                    val title = intent.getStringExtra(NOTIFICATION_TITLE)
                    val content = intent.getStringExtra(NOTIFICATION_CONTENT)
                    val notification = NotificationModel(null, title, content, type, LocalDateTime.now().toString())
                    lifecycleScope.launch {
                        homeViewModel.setHomeDestination(NOTIFICATIONS)
                        homeViewModel.addNotification(notification)
                    }
                }

                NotificationType.ELECTION.name -> {
                    val content = intent.getStringExtra(NOTIFICATION_CONTENT)
                    val type = intent.getStringExtra(NOTIFICATION_TYPE)
                    if (content != null) {
                        val election = Gson().fromJson(content, ElectionNotification::class.java)
                        val notification = NotificationModel(
                            null,
                            election.electionEvent,
                            "${election.electionTitle} is now ${election.electionEvent}",
                            type, LocalDateTime.now().toString()
                        )
                        lifecycleScope.launch {
                            homeViewModel.setHomeDestination("notifications")
                            groupViewModel.reloadGroup(election.groupId)
                            homeViewModel.addNotification(notification)
                        }
                    }
                }

                NotificationType.ADMIN.name -> {
                    val type = intent.getStringExtra(NOTIFICATION_TYPE)
                    val title = intent.getStringExtra(NOTIFICATION_TITLE)
                    val content = intent.getStringExtra(NOTIFICATION_CONTENT)
                    val notification = NotificationModel(null, title, content, type, LocalDateTime.now().toString())
                    lifecycleScope.launch {
                        homeViewModel.setHomeDestination("notifications")
                        homeViewModel.addNotification(notification)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun askForPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_REQUEST_CODE
            )
        }
    }

    private fun startMyFirebaseMessagingService() {
        val intent = Intent(this@MainActivity, NotificationService::class.java)
        startService(intent)
    }

}

