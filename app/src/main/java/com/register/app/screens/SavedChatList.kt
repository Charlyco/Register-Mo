package com.register.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.model.DirectChatContact
import com.register.app.util.BottomNavBar
import com.register.app.util.GenericTopBar
import com.register.app.util.ImageLoader
import com.register.app.viewmodel.ForumViewModel
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch

@Composable
fun SavedChatList(
    forumViewModel: ForumViewModel,
    navController: NavController,
    groupViewModel: GroupViewModel,
){
    LaunchedEffect(key1 = Unit) {
        forumViewModel.fetchDirectChatList()
    }
    Scaffold(
        topBar = { GenericTopBar(title = "Chats", navController = navController) },
        bottomBar = { BottomNavBar(navController) }
    ) {
        ChatContactList(Modifier.padding(it), forumViewModel, groupViewModel, navController)
    }
}

@Composable
fun ChatContactList(
    modifier: Modifier,
    forumViewModel: ForumViewModel,
    groupViewModel: GroupViewModel,
    navController: NavController
) {
    val chatList = forumViewModel.directChatList.observeAsState().value
    if (chatList != null) {
        LazyColumn(
            modifier = Modifier
                .padding(top = 64.dp, bottom = 72.dp)
                .fillMaxSize(),
            state = rememberLazyListState()
        ) {
            items(chatList) { chatContact ->
                ChatItem(chatContact, navController, forumViewModel, groupViewModel)
            }
        }
    }
}

@Composable
fun ChatItem(chatContact: DirectChatContact,
             navController: NavController,
             forumViewModel: ForumViewModel,
             groupViewModel: GroupViewModel
             ) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clickable {
                coroutineScope.launch {
                    forumViewModel.fetUserChats(
                        chatContact.contactMembershipId!!,
                        chatContact.myMembershipId!!
                    )
                    val group = groupViewModel.reloadGroup(chatContact.groupId)
                    if (group != null) {
                        val userEmail =
                            group.memberList?.find { it.membershipId == chatContact.contactMembershipId }?.emailAddress
                        navController.navigate("admin_chat/${userEmail}")
                        forumViewModel.subScribeToDirectChat(userEmail!!, group)
                    }
                }
            },
        color = MaterialTheme.colorScheme.background,
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            Modifier.fillMaxWidth()
        ) {
            ConstraintLayout(
                Modifier.fillMaxWidth(),
            ) {
                val (profile, name) = createRefs()

                Surface(
                    Modifier
                        .constrainAs(profile) {
                            start.linkTo(parent.start)
                            centerVerticallyTo(parent)
                        },
                    color = MaterialTheme.colorScheme.background,
                    border = BorderStroke(1.dp, Color.Gray),
                    shape = MaterialTheme.shapes.large
                ) {
                    ImageLoader(
                        imageUrl = chatContact.imageUrl ?: "",
                        context = context,
                        height = 42,
                        width = 42,
                        placeHolder = R.drawable.placeholder
                    )
                }

                Text(
                    text = chatContact.contactName!!,
                    fontSize = TextUnit(16.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(name) {
                            start.linkTo(profile.end, margin = 8.dp)
                            centerVerticallyTo(parent)
                        }
                )
            }
            HorizontalDivider(Modifier.padding(vertical = 4.dp))
        }
    }
}
