package com.register.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.dto.DirectChatMessageData
import com.register.app.model.Member
import com.register.app.util.GenericTopBar
import com.register.app.util.ImageLoader
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.ForumViewModel
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun AdminChat(
    forumViewModel: ForumViewModel,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    navController: NavController,
    remoteUserEmail: String?) {

    var remoteUserDetail by remember { mutableStateOf<Member?>(null)}
    val remoteUser = forumViewModel.remoteUser.observeAsState().value //get remote user set in #handleNewIntent in mainActivity
    LaunchedEffect(remoteUserEmail) {
        remoteUserDetail = authViewModel.getMemberDetails(remoteUserEmail?: remoteUser?.emailAddress!!)
    }
    Scaffold(
        topBar = { GenericTopBar(
            title = remoteUserDetail?.fullName?: "",
            navController = navController
        ) }
    ) {
        ChatList(Modifier.padding(it), remoteUserDetail, forumViewModel, groupViewModel, navController)
    }
}

@Composable
fun ChatList(
    modifier: Modifier,
    remoteUser: Member?,
    forumViewModel: ForumViewModel,
    groupViewModel: GroupViewModel,
    navController: NavController
) {
    val conversations = forumViewModel.directChatMessages.observeAsState().value?.sortedByDescending { it.sendTime }
    val userFullName = forumViewModel.currentUser.observeAsState().value

    // Create and remember the LazyListState
    val listState = rememberLazyListState()

    // Scroll to the last item when conversations change
    LaunchedEffect(conversations) {
        if (!conversations.isNullOrEmpty()) {
            listState.scrollToItem(conversations.size - 1)
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .padding(top = 124.dp)
            .fillMaxSize()
    ) {
        val (conversation, messageBox) = createRefs()

        Surface(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .constrainAs(conversation) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(messageBox.top, margin = 4.dp)
                },
            color = MaterialTheme.colorScheme.background
        ) {
            if (conversations != null) {
                LazyColumn(
                    state = listState,  // Set the state to LazyColumn
                ) {
                    items(conversations) { item ->
                        if (item.senderName == userFullName) {
                            LocalDirectMessageItem(item)
                        } else {
                            RemoteDirectMessageItem(item)
                        }
                    }
                }
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(messageBox) {
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                }
        ) {
            DirectChatMessageBox(forumViewModel, groupViewModel, remoteUser)
        }
    }
}

@Composable
fun DirectChatMessageBox(
    forumViewModel: ForumViewModel,
    groupViewModel: GroupViewModel,
    remoteUser: Member?
) {
    var message by rememberSaveable { mutableStateOf("") }
    val screenWidth = LocalConfiguration.current.screenWidthDp - 64
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val group = groupViewModel.groupDetailLiveData.value

    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (box, btn) = createRefs()
        Surface(
            modifier = Modifier
                .width(screenWidth.dp)
                .constrainAs(box) {
                    start.linkTo(parent.start, margin = 4.dp)
                },
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.background,
            border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.primary)
        ) {
            TextField(
                value = message,
                onValueChange = { message =it},
                placeholder = { Text(text = stringResource(id = R.string.message)) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                )
            )
        }
        IconButton(
            onClick = {
                keyboardController?.hide()
                coroutineScope.launch {
                    forumViewModel.sendDirectMessage(remoteUser?.emailAddress!!, group!!, message)
                    message = ""
                } },
            modifier = Modifier.constrainAs(btn) {
                end.linkTo(parent.end, margin = 4.dp)
            }
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send message")
        }
    }
}

@Composable
fun RemoteDirectMessageItem(messageData: DirectChatMessageData) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .padding(top = 2.dp, bottom = 2.dp, end = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            Modifier.padding(horizontal = 8.dp),
            color = MaterialTheme.colorScheme.background,
            border = BorderStroke(1.dp, Color.Gray),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            ImageLoader(
                imageUrl = messageData.imageUrl ?: "",
                context = context,
                height = 42,
                width = 42,
                placeHolder = R.drawable.placeholder
            )
        }
        Surface(
            modifier = Modifier
                .padding(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 2.dp,
            shape = MaterialTheme.shapes.small
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = messageData.senderName!!,
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(end = 4.dp, start = 4.dp)
                )
                Text(
                    text = messageData.message!!,
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = if (LocalDateTime.parse(messageData.sendTime).isBefore(LocalDateTime.of(
                            LocalDate.now(), LocalTime.of(0, 0)))) {
                        LocalDateTime.parse(messageData.sendTime).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    } else {
                        LocalDateTime.parse(messageData.sendTime).format(DateTimeFormatter.ofPattern("HH:mm"))
                    },
                    fontSize = TextUnit(10.0f, TextUnitType.Sp),
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun LocalDirectMessageItem(messageData: DirectChatMessageData) {
    Row(
        modifier = Modifier
            .padding(top = 2.dp, bottom = 2.dp, start = 42.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, bottom = 2.dp, start = 16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shape = MaterialTheme.shapes.small
        ) {
            Column {
                Text(
                    text = messageData.message!!,
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Text(
                    text = if (LocalDateTime.parse(messageData.sendTime).isBefore(LocalDateTime.of(
                            LocalDate.now(), LocalTime.of(0, 0)))) {
                        LocalDateTime.parse(messageData.sendTime).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    } else {
                        LocalDateTime.parse(messageData.sendTime).format(DateTimeFormatter.ofPattern("HH:mm"))
                    },
                    fontSize = TextUnit(10.0f, TextUnitType.Sp),
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}
