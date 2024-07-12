package com.register.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.dto.ChatMessageResponse
import com.register.app.util.BottomNavBar
import com.register.app.util.GenericTopBar
import com.register.app.viewmodel.ForumViewModel
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch

@Composable
fun Forum(forumViewModel: ForumViewModel?, groupViewModel: GroupViewModel, navController: NavController){
    val remoteUser = forumViewModel?.currentRemoteUser?.observeAsState()?.value
    val context = LocalContext.current

    Scaffold(
        topBar = { GenericTopBar(title = "", navController = navController, navRoute = "home") },
        bottomBar = { BottomNavBar(navController = navController) }
    ) {
        ForumScreen(Modifier.padding(it),forumViewModel, groupViewModel, remoteUser, navController)
    }
}

@Composable
fun ForumScreen(
    modifier: Modifier,
    forumViewModel: ForumViewModel?,
    groupViewModel: GroupViewModel,
    remoteUser: String?,
    navController: NavController
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val conversations = forumViewModel?.chatMessages?.observeAsState()?.value

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
                    if (item.isMine == true) {
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
                    bottom.linkTo(parent.bottom, margin = 2.dp)
                    top.linkTo(parent.bottom)
                }
        ) {
            MessageBox(forumViewModel, navController, remoteUser!!)
        }
    }
}

@Composable
fun RemoteMessageItem(item: ChatMessageResponse) {
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
            val (message, time) = createRefs()

            Text(
                text = item.message!!,
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.constrainAs(message) {
                    top.linkTo(parent.top, margin = 2.dp)
                    start.linkTo(parent.start, margin = 2.dp)
                }
            )

            Text(
                text = item.time!!,
                fontSize = TextUnit(10.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.constrainAs(time) {
                    top.linkTo(message.bottom, margin = 1.dp)
                    start.linkTo(parent.start, margin = 2.dp)
                }
            )
        }
    }
}

@Composable
fun MyMessageItem(item: ChatMessageResponse) {
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
                    end.linkTo(parent.end, margin = 2.dp)
                }
            )

            Text(
                text = item.time!!,
                fontSize = TextUnit(10.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onSecondary,
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
    navController: NavController,
    toUsername: String,
) {
    var message by rememberSaveable { mutableStateOf("") }
    val screenWidth = LocalConfiguration.current.screenWidthDp - 64
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    //val focusRequester = remember { FocusRequester() }
    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (box, btn) = createRefs()
        Surface(
            modifier = Modifier
                .width(screenWidth.dp)
                .constrainAs(box) {
                    start.linkTo(parent.start, margin = 2.dp)
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
                        forumViewModel?.sendMessage(toUsername, message)
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