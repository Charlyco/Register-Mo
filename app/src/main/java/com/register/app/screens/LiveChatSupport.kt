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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.register.app.dto.SupportMessageDto
import com.register.app.model.Member
import com.register.app.util.GenericTopBar
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.ForumViewModel
import com.register.app.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun LiveChatSupport(
    homeViewModel: HomeViewModel,
    forumViewModel: ForumViewModel,
    navController: NavController,
    authViewModel: AuthViewModel) {

    Scaffold(
        topBar = { GenericTopBar(
            title = "Live chat support",
            navController = navController
        ) }
    ) {
       LiveChatSupportScreen(Modifier.padding(it), homeViewModel, forumViewModel, navController, authViewModel)
    }
}

@Composable
fun LiveChatSupportScreen(
    modifier: Modifier,
    homeViewModel: HomeViewModel,
    forumViewModel: ForumViewModel,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val conversations = forumViewModel.supportMessages.observeAsState().value
    val userData = authViewModel.userLideData.observeAsState().value
    val listState = rememberLazyListState()

    LaunchedEffect(conversations) {
        if (!conversations.isNullOrEmpty()) {
            listState.scrollToItem(conversations.size - 1)
        }
    }
    ConstraintLayout(
        modifier = Modifier
            .padding(top = 64.dp)
            .fillMaxWidth()
            .height((screenHeight - 8).dp)
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
                state = listState
            ) {
                items(conversations) { item ->
                    if (item.email == userData?.emailAddress) {
                        UserMessageItem(item)
                    }else {
                        SupportMessageItem(item)
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
            SupportMessageBox(forumViewModel, navController, userData)
        }
    }
}

@Composable
fun SupportMessageItem(item: SupportMessageDto) {
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
            val (name, message, time) = createRefs()

            Text(
                text = item.fullName!!,
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.constrainAs(name) {
                    top.linkTo(parent.top, margin = 2.dp)
                    start.linkTo(parent.start, margin = 4.dp)
                }
            )

            Text(
                text = item.message!!,
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.constrainAs(message) {
                    top.linkTo(name.bottom, margin = 2.dp)
                    start.linkTo(parent.start, margin = 4.dp)
                }
            )

            Text(
                text = LocalDateTime.parse(item.dateTime).format(DateTimeFormatter.ofPattern("HH:mm")),
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
fun UserMessageItem(item: SupportMessageDto) {
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
                text = LocalDateTime.parse(item.dateTime).format(DateTimeFormatter.ofPattern("HH:mm")),
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
fun SupportMessageBox(
    forumViewModel: ForumViewModel,
    navController: NavController,
    userData: Member?

) {
    var message by rememberSaveable { mutableStateOf("") }
    val screenWidth = LocalConfiguration.current.screenWidthDp - 64
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

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
        IconButton(
            onClick = {
                keyboardController?.hide()
                coroutineScope.launch {
                    forumViewModel.sendSupportMessage(userData, message)
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
