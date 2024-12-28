package com.register.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.dto.GroupStateItem
import com.register.app.dto.JoinChatPayload
import com.register.app.dto.MessageData
import com.register.app.enums.MemberStatus
import com.register.app.util.BottomNavBar
import com.register.app.util.ImageLoader
import com.register.app.viewmodel.ForumViewModel
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun Forum(forumViewModel: ForumViewModel?, groupViewModel: GroupViewModel, navController: NavController){
    val groupList = groupViewModel.groupListLiveData.observeAsState().value
    if (groupList.isNullOrEmpty()) {
        NullGroupScreen()
    }else {
        ForumScreen(forumViewModel, groupViewModel, navController)
    }
}

@Composable
fun NullGroupScreen() {
    Surface(
        Modifier.fillMaxSize()
    ) {
        ConstraintLayout(
            Modifier.fillMaxSize()
        ) {
            val (image, text) = createRefs()

            Image(
                painter = painterResource(id = R.drawable.forum),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(120.dp)
                    .constrainAs(image) {
                        centerHorizontallyTo(parent)
                        centerVerticallyTo(parent)
                    }
            )
            Text(
                text = stringResource(id = R.string.no_groups),
                fontSize = TextUnit(20.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.constrainAs(text) {
                    top.linkTo(image.bottom, margin = 16.dp)
                    centerHorizontallyTo(parent)
                }
            )
        }
    }
}

@Composable
fun ForumScreen(
    forumViewModel: ForumViewModel?,
    groupViewModel: GroupViewModel,
    navController: NavController
) {

    Scaffold(
        topBar = { ChatTopBar(groupViewModel, forumViewModel, navController) },
        bottomBar = { BottomNavBar(navController = navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) {
        ForumScreenContent(Modifier.padding(it), forumViewModel, groupViewModel, navController)
    }
}

@Composable
fun ForumScreenContent(
    padding: Modifier,
    forumViewModel: ForumViewModel?,
    groupViewModel: GroupViewModel,
    navController: NavController
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val conversations = forumViewModel?.chatMessages?.observeAsState()?.value
    val membershipId = groupViewModel.membershipId.value
    val userFullName = forumViewModel?.currentUser?.observeAsState()?.value
    val coroutineScope = rememberCoroutineScope()
    val group = groupViewModel.groupDetailLiveData.observeAsState().value

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
            .padding(top = 196.dp)
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
                            MyMessageItem(forumViewModel, item, conversations){
                                coroutineScope.launch { listState.scrollToItem(it) }
                            }
                        } else {
                            RemoteMessageItem(forumViewModel, groupViewModel, item, conversations){
                                coroutineScope.launch { listState.scrollToItem(it) }
                            }
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
                    bottom.linkTo(parent.bottom, margin = 72.dp)
                }
        ) {
            MessageBox(forumViewModel, groupViewModel, navController, membershipId!!)
        }
    }
}

@Composable
fun ChatTopBar(
    groupViewModel: GroupViewModel,
    forumViewModel: ForumViewModel?,
    navController: NavController
) {
    val groupList = groupViewModel.groupListLiveData.observeAsState().value
    val groupSaverList = mutableListOf<GroupStateItem>()
    groupList?.forEach {
        groupSaverList.add(GroupStateItem(it.groupId, it.groupName))
    }
    val selectedGroup = forumViewModel?.selectedGroup?.observeAsState()?.value?: groupList?.get(0)
    var expanded by rememberSaveable { mutableStateOf(false) }
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val coroutineScope = rememberCoroutineScope()

    Surface(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .height(56.dp)
            .clickable {
                expanded = !expanded
            },
        color = MaterialTheme.colorScheme.background,
        shadowElevation = dimensionResource(id = R.dimen.default_elevation),
        shape = MaterialTheme.shapes.small
    ) {
        Box(
            Modifier.fillMaxWidth()
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val (selectionBox, list, icon) = createRefs()
                Text(
                    text = selectedGroup?.groupName!!,
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(8.dp)
                        .constrainAs(selectionBox) {
                            start.linkTo(parent.start, margin = 4.dp)
                            centerVerticallyTo(parent)
                        }
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "",
                    modifier = Modifier.constrainAs(icon) {
                        end.linkTo(parent.end, margin = 2.dp)
                        centerVerticallyTo(parent)
                    }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .constrainAs(list) {
                            centerHorizontallyTo(parent)
                        }
                        .width((screenWidth - 8).dp)
                ) {
                    groupList?.forEach { group ->
                        DropdownMenuItem(
                            text = { Text(text = group.groupName)},
                            onClick = {
                                coroutineScope.launch {
                                    val groupDetail = groupList.find { it.groupId == group.groupId } // Find the group that matches the selected item
                                    expanded = false
                                    forumViewModel?.connectToChat(JoinChatPayload(groupDetail?.groupName!!, groupDetail.groupId))
                                    groupViewModel.setSelectedGroupDetail(groupDetail!!)
                                    forumViewModel?.setSelectedGroup(groupDetail)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RemoteMessageItem(
    forumViewModel: ForumViewModel?,
    groupViewModel: GroupViewModel,
    item: MessageData,
    conversations: List<MessageData>,
    scrollToItem: (Int) -> Unit
    ) {
    var originalMessage by remember {  mutableStateOf<MessageData?>(null)}
    if (item.originalMessageId != null) {
        originalMessage = conversations.find { it.id == item.originalMessageId }
    }
    val context = LocalContext.current
    var showContextMenu by rememberSaveable { mutableStateOf(false) }
    val isUserAdmin = groupViewModel.isUserAdminLiveData.observeAsState().value

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
                imageUrl = item.imageUrl ?: "",
                context = context,
                height = 42,
                width = 42,
                placeHolder = R.drawable.placeholder
            )
        }
        Surface(
            modifier = Modifier
                .padding()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            if (originalMessage != null) {
                                scrollToItem(conversations.indexOf(originalMessage!!))
                            }
                        },
                        onLongPress = {
                            if (isUserAdmin == true) {
                                showContextMenu = true
                            }
                        },
                    )
                },
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 2.dp,
            shape = MaterialTheme.shapes.small
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                if (item.originalMessageId != null) {
                    Text(
                        text = originalMessage?.message?:"",
                        color = Color.Gray,
                        fontSize = TextUnit(12.0f, TextUnitType.Sp),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onTertiary)
                }
                Text(
                    text = item.senderName!!,
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(end = 4.dp, start = 4.dp)
                )
                Text(
                    text = item.message!!,
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = if (LocalDateTime.parse(item.sendTime).isBefore(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)))) {
                        LocalDateTime.parse(item.sendTime).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    } else {
                        LocalDateTime.parse(item.sendTime).format(DateTimeFormatter.ofPattern("HH:mm"))
                    },
                    fontSize = TextUnit(10.0f, TextUnitType.Sp),
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Text(
                    text = stringResource(id = R.string.reply),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            forumViewModel?.captureMessageToReply(item)
                        }
                        .padding(end = 4.dp),
                    fontSize = TextUnit(10.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.End
                )
            }
        }
    }
    if (showContextMenu) {
        MessageItemContextMenu(item, forumViewModel) {showContextMenu = it}
    }
}

@Composable
fun MessageItemContextMenu(
    item: MessageData,
    forumViewModel: ForumViewModel?,
    onDismiss: (Boolean) -> Unit) {

    val coroutineScope = rememberCoroutineScope()
    Dialog(onDismissRequest = { onDismiss(false) }) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.small
        ) {
            Column(
                Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.delete_message),
                    modifier = Modifier.clickable {
                        coroutineScope.launch{
                            forumViewModel?.deleteMessage(item.groupId, item.id)
                        }
                        onDismiss(false)
                    }
                )
            }
        }
    }
}

@Composable
fun MyMessageItem(
    forumViewModel: ForumViewModel?,
    item: MessageData,
    conversations: List<MessageData>,
    scrollToItem: (Int) -> Unit
    ) {
    var originalMessage by remember {  mutableStateOf<MessageData?>(null)}
    if (item.originalMessageId != null) {
        originalMessage = conversations.find { it.id == item.originalMessageId }
    }
    var showContextMenu by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(top = 2.dp, bottom = 2.dp, start = 42.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, bottom = 2.dp, start = 16.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            if (originalMessage != null) {
                                scrollToItem(conversations.indexOf(originalMessage!!))
                            }
                        },
                        onLongPress = { showContextMenu = true },
                    )
                },
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shape = MaterialTheme.shapes.small
        ) {
            Column(
                horizontalAlignment = Alignment.End,
            ) {
                if (originalMessage != null) {
                    Text(
                        text = originalMessage?.message?: "",
                        color = Color.Gray,
                        fontSize = TextUnit(12.0f, TextUnitType.Sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onTertiary)
                }
                Text(
                    text = item.message!!,
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Text(
                    text = if (LocalDateTime.parse(item.sendTime).isBefore(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)))) {
                        LocalDateTime.parse(item.sendTime).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    } else {
                        LocalDateTime.parse(item.sendTime).format(DateTimeFormatter.ofPattern("HH:mm"))
                    },
                    fontSize = TextUnit(10.0f, TextUnitType.Sp),
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Text(
                    text = stringResource(id = R.string.reply),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .fillMaxWidth()
                        .clickable {
                            forumViewModel?.captureMessageToReply(item)
                        },
                    fontSize = TextUnit(10.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
    if (showContextMenu) {
        MessageItemContextMenu(item, forumViewModel) {showContextMenu = it}
    }
}


@Composable
fun MessageBox(
    forumViewModel: ForumViewModel?,
    groupViewModel: GroupViewModel,
    navController: NavController,
    membershipId: String,
) {
    var message by rememberSaveable { mutableStateOf("") }
    val screenWidth = LocalConfiguration.current.screenWidthDp - 64
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val group = groupViewModel.groupDetailLiveData.value
    val messageToReply = forumViewModel?.messageToReply?.observeAsState()?.value
    val isSuspended = groupViewModel.isUserSuspended.observeAsState().value

    Column(modifier = Modifier.fillMaxWidth()) {
        if (messageToReply != null) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onTertiary)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.TopEnd
            ) {

                Text(
                    text = messageToReply.message!!,
                    color = Color.Gray,
                    fontSize = TextUnit(10.0f, TextUnitType.Sp),
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "",
                    modifier = Modifier.clickable {
                        forumViewModel.captureMessageToReply(null)
                    }
                )
            }
        }
        if (isSuspended == true) {
            Text(
                text = stringResource(id = R.string.suspended),
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = Color.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                textAlign = TextAlign.Center)
        }else {
            Row {
                Surface(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .width(screenWidth.dp),
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
                            forumViewModel?.sendMessageToForum(membershipId, message, group, messageToReply)
                            message = ""
                            forumViewModel?.captureMessageToReply(null)
                        } },
                    modifier = Modifier
                        .padding(end = 4.dp),
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send message")
                }
            }
        }
    }
}