package com.register.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.dto.GroupStateItem
import com.register.app.dto.JoinChatPayload
import com.register.app.dto.MessageData
import com.register.app.util.BottomNavBar
import com.register.app.util.GroupStateSaver
import com.register.app.util.ImageLoader
import com.register.app.viewmodel.ForumViewModel
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun Forum(forumViewModel: ForumViewModel?, groupViewModel: GroupViewModel, navController: NavController){
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    if (group == null) {
        NullGroupScreen()
    }else{
        Scaffold(
            topBar = { ChatTopBar(groupViewModel, forumViewModel, navController) },
            bottomBar = { BottomNavBar(navController = navController) },
            containerColor = MaterialTheme.colorScheme.background
        ) {
            ForumScreen(Modifier.padding(it),forumViewModel, groupViewModel, navController)
        }
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
    val selectedGroup = forumViewModel?.selectedGroup?.observeAsState()?.value?: groupViewModel.groupDetailLiveData.observeAsState().value

    //var selectedGroup by rememberSaveable { mutableStateOf(groupList?.get(0)) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val coroutineScope = rememberCoroutineScope()

    if (selectedGroup != null) {
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
                        text = selectedGroup.groupName,
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
                                        forumViewModel?.setSelectedGroup(groupDetail)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }else {
        ChatErrorScreen()
    }

}

@Composable
fun ChatErrorScreen() {
    Surface(
        Modifier.fillMaxSize()
    ) {
       ConstraintLayout(
           Modifier.fillMaxSize()
       ) {
           val (errorText, errorImage) = createRefs()

           Text(
               text = stringResource(id = R.string.error_text),
               fontSize = TextUnit(14.0f, TextUnitType.Sp),
               color = MaterialTheme.colorScheme.onBackground,
               modifier = Modifier.constrainAs(errorText) {
                   centerHorizontallyTo(parent)
                   centerVerticallyTo(parent)
               }
               )
       }
    }
}

@Composable
fun ForumScreen(
    modifier: Modifier,
    forumViewModel: ForumViewModel?,
    groupViewModel: GroupViewModel,
    navController: NavController
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val conversations = forumViewModel?.chatMessages?.observeAsState()?.value
    val membershipId = groupViewModel.membershipId.value

    ConstraintLayout(
        modifier = Modifier
            .padding(top = 64.dp)
            .fillMaxWidth()
            .height((screenHeight - 96).dp)
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
        ) { if (conversations != null) {
            LazyColumn(
                state = rememberLazyListState()
            ) {
                items(conversations) { item ->
                    if (item.membershipId == membershipId) {
                        MyMessageItem(item)
                    }else {
                        RemoteMessageItem(item)
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
                    bottom.linkTo(parent.bottom, margin = 42.dp)
                }
        ) {
            MessageBox(forumViewModel, groupViewModel, navController, membershipId!!)
        }
    }
}

@Composable
fun RemoteMessageItem(item: MessageData) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .padding(vertical = 2.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.small
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (image, name, message, time) = createRefs()

            Surface(
                Modifier
                    .constrainAs(image) {
                        centerVerticallyTo(parent)
                        start.linkTo(parent.start, margin = 4.dp)
                    },
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

            Text(
                text = item.senderName!!,
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.constrainAs(name) {
                    top.linkTo(parent.top, margin = 2.dp)
                    start.linkTo(image.end, margin = 4.dp)
                }
            )

            Text(
                text = item.message!!,
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.constrainAs(message) {
                    top.linkTo(name.bottom, margin = 2.dp)
                    start.linkTo(image.end, margin = 4.dp)
                }
            )

            Text(
                text = LocalDateTime.parse(item.sendTime).format(DateTimeFormatter.ofPattern("HH:mm")),
                fontSize = TextUnit(10.0f, TextUnitType.Sp),
                color = Color.Gray,
                modifier = Modifier.constrainAs(time) {
                    top.linkTo(message.bottom, margin = 1.dp)
                    end.linkTo(parent.end, margin = 4.dp)
                }
            )
        }
    }
}

@Composable
fun MyMessageItem(item: MessageData) {
    Surface(
        modifier = Modifier
            .padding(vertical = 2.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.small
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (message, time) = createRefs()

            Text(
                text = item.message!!,
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.constrainAs(message) {
                    top.linkTo(parent.top, margin = 2.dp)
                    end.linkTo(parent.end, margin = 4.dp)
                }
            )

            Text(
                text = LocalDateTime.parse(item.sendTime).format(DateTimeFormatter.ofPattern("HH:mm")),
                fontSize = TextUnit(10.0f, TextUnitType.Sp),
                color = Color.Gray,
                modifier = Modifier.constrainAs(time) {
                    top.linkTo(message.bottom, margin = 1.dp)
                    end.linkTo(parent.end, margin = 2.dp)
                }
            )
        }
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
                placeholder = { Text(text = stringResource(id = R.string.message)) })
        }
        if (message.isBlank()) {
            IconButton(onClick = { },
                modifier = Modifier.constrainAs(btn) { end.linkTo(parent.end, margin = 4.dp)
                }
            ) { Icon(imageVector = Icons.Default.AttachFile, contentDescription = "Attach file") }
        }else{
            IconButton(
                onClick = {
                    keyboardController?.hide()
                    coroutineScope.launch {
                        forumViewModel?.sendMessage(membershipId, message, group)
                        message = ""
                    }
                },
                modifier = Modifier.constrainAs(btn) {
                    end.linkTo(parent.end, margin = 4.dp)
                }
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send message")
            }
        }
    }
}