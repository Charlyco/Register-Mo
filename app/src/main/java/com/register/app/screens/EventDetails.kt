package com.register.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.dto.CommentReply
import com.register.app.dto.EventComment
import com.register.app.dto.NewEventDto
import com.register.app.dto.ReactionType
import com.register.app.model.Event
import com.register.app.util.DataStoreManager
import com.register.app.util.ImageLoader
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.register.app.model.Member
import com.register.app.util.DateFormatter
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.GroupViewModel

@Composable
fun EventDetails(
    dataStoreManager: DataStoreManager,
    navController: NavController,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel) {
    val selectedEvent = groupViewModel.selectedEvent.observeAsState().value
    Scaffold(
        topBar = { EventDetailTopBar(navController, groupViewModel, selectedEvent) }
    ) {
        EventDetailContent(Modifier.padding(it), dataStoreManager, navController, groupViewModel, authViewModel)
    }
}

@Composable
fun EventDetailTopBar(
    navController: NavController,
    groupViewModel: GroupViewModel,
    selectedEvent: Event?
) {
    //val topBarWidth = LocalConfiguration.current.screenWidthDp - 32
    Surface(
        Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.background,
        shadowElevation = dimensionResource(id = R.dimen.default_elevation)
    ) {
        ConstraintLayout(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            val (navBtn, eventTitle) = createRefs()
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "",
                modifier = Modifier
                    .clickable {
                        navController.navigateUp()
                    }
                    .constrainAs(navBtn) {
                        start.linkTo(parent.start, margin = 8.dp)
                        centerVerticallyTo(parent)
                    }
            )

            Text(
                text = selectedEvent?.eventTitle!!,
                Modifier.constrainAs(eventTitle) {
                    centerHorizontallyTo(parent)
                    centerVerticallyTo(parent)
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventDetailContent(
    modifier: Modifier,
    dataStoreManager: DataStoreManager,
    navController: NavController,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel
) {
    val event = groupViewModel.selectedEvent.observeAsState().value
    val commentList = groupViewModel.eventCommentLiveData.observeAsState().value
    val pageState = rememberPagerState(pageCount = { event?.imageUrlList?.size?: 0} )
    var showDetails by rememberSaveable { mutableStateOf(true)}
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp - 64
    val scrollState = rememberScrollState(initial = 0)
    var likeList = 0
    var unlikeList = 0
    var loveList = 0

    event?.eventReactions?.forEach { eventReaction ->
        when (eventReaction.reactionType) {
            ReactionType.LIKE.name -> {likeList++}
            ReactionType.UNLIKE.name -> {unlikeList++}
            ReactionType.LOVE.name -> {loveList++}
        }
    }

    Surface(
        Modifier
            .padding(top = 72.dp)
            .fillMaxSize()
            .verticalScroll(state = scrollState, enabled = true, reverseScrolling = false),
        color = MaterialTheme.colorScheme.background
    ) {
        ConstraintLayout(
            Modifier
                .fillMaxSize()) {
            val (eventImages, dotIndicator, evenDetails, tab,
                commentTab, react) = createRefs()

            HorizontalPager(
                state = pageState,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(eventImages) {
                        top.linkTo(parent.top)
                        centerHorizontallyTo(parent)
                    }
            ) {
                event?.imageUrlList?.get(pageState.currentPage)
                    ?.let { imageUrl  -> ImageLoader(
                        imageUrl,
                        context,
                        280,
                        screenWidth,
                        R.drawable.event
                    ) }
            }

            Row(
                Modifier.constrainAs(dotIndicator) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(eventImages.bottom, margin = 16.dp)
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pageState.pageCount) { iterations ->
                    val color = if (pageState.currentPage == iterations) {
                        MaterialTheme.colorScheme.primary
                    }else MaterialTheme.colorScheme.surface

                    Box(modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp))
                }
            }

            Surface(
                Modifier.constrainAs(react) {
                    start.linkTo(parent.start, margin = 16.dp)
                    top.linkTo(eventImages.bottom, margin = 4.dp)
                },
                color = Color.Transparent
            ) {
                ReactToEvent(likeList, unlikeList, loveList)
            }

            Surface(
                Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .padding(horizontal = 16.dp)
                    .constrainAs(tab) {
                        top.linkTo(react.bottom, margin = 8.dp)
                        centerHorizontallyTo(parent)
                    },
                shape = MaterialTheme.shapes.small
            ) {
                TabSwitch() {
                    showDetails = it
                }
            }

            if(showDetails) {
                Surface(
                    Modifier
                        .fillMaxWidth()
                        .constrainAs(evenDetails) {
                            top.linkTo(tab.bottom, margin = 4.dp)
                            centerHorizontallyTo(parent)
                        },
                    color = MaterialTheme.colorScheme.background,
                ) {
                    ViewEventDetails(event, dataStoreManager, authViewModel, groupViewModel, navController)
                }
            }
            if (!showDetails) {
                Surface(
                    Modifier
                        .fillMaxWidth()
                        .constrainAs(commentTab) {
                            top.linkTo(tab.bottom, margin = 4.dp)
                            centerHorizontallyTo(parent)
                        },
                    color = MaterialTheme.colorScheme.background,
                ) {
                  Column(
                      Modifier.fillMaxWidth()
                  ) {
                      Surface(
                          Modifier
                              .padding(horizontal = 16.dp, vertical = 8.dp)
                              .fillMaxWidth(),
                          color = Color.Transparent,
                          border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                          shape = MaterialTheme.shapes.medium
                      ) {
                          CommentBox(groupViewModel, event, dataStoreManager)
                      }

                      if (commentList?.isNotEmpty() == true) {
                          LazyColumn(
                              modifier = Modifier
                                  .padding(vertical = 1.dp)
                                  .height(screenHeight.dp),
                              verticalArrangement = Arrangement.Top
                          ) {
                              items(commentList) { comment ->
                                  CommentItem(comment, groupViewModel, dataStoreManager)
                              }
                          }
                      }
                  }
                }
            }
        }
    }
}

@Composable
fun TabSwitch(switchView: (showDetails: Boolean) -> Unit) {
    val screenWidth = LocalConfiguration.current.screenWidthDp / 2
    var showDetails by rememberSaveable { mutableStateOf(true)}
    ConstraintLayout(
        Modifier.fillMaxSize()
    ) {
        val (detailBg, commentBg, detailsTop, commentTop) = createRefs()
        Text(
            text = stringResource(id = R.string.details),
            Modifier
                .width(screenWidth.dp)
                .fillMaxHeight()
                .padding(top = 4.dp)
                .clickable {
                    showDetails = true
                    switchView(showDetails)
                }
                .constrainAs(detailBg) {
                    start.linkTo(parent.start)
                },
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(id = R.string.view_comments),
            Modifier
                .width(screenWidth.dp)
                .fillMaxHeight()
                .padding(top = 4.dp)
                .clickable {
                    showDetails = false
                    switchView(showDetails)
                }
                .constrainAs(commentBg) {
                    end.linkTo(parent.end)
                },
            textAlign = TextAlign.Center
        )
        if (showDetails) {
            Surface(
                Modifier
                    .width((screenWidth - 24).dp)
                    .height(40.dp)
                    .padding(vertical = 1.dp)
                    .constrainAs(detailsTop) {
                        start.linkTo(parent.start, margin = 1.dp)
                        centerVerticallyTo(parent)
                    },
                shadowElevation = dimensionResource(id = R.dimen.low_elevation),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.background
            ) {
                Text(
                    text = stringResource(id = R.string.details),
                    Modifier
                        .width(screenWidth.dp)
                        .fillMaxHeight()
                        .padding(top = 4.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        if (!showDetails) {
            Surface(
                Modifier
                    .width((screenWidth - 24).dp)
                    .height(40.dp)
                    .padding(vertical = 1.dp)
                    .constrainAs(commentTop) {
                        end.linkTo(parent.end, margin = 1.dp)
                        centerVerticallyTo(parent)
                    },
                shadowElevation = dimensionResource(id = R.dimen.low_elevation),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.background
            ) {
                Text(
                    text = stringResource(id = R.string.view_comments),
                    Modifier
                        .width(screenWidth.dp)
                        .fillMaxHeight()
                        .padding(top = 4.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ViewEventDetails(
    event: Event?,
    dataStoreManager: DataStoreManager,
    authViewModel: AuthViewModel,
    groupViewModel: GroupViewModel,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    var userId: Int? = null
    var showPaidList by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(key1 = 321) { userId = dataStoreManager.readAuthData()?.toInt() }
    Column(
        Modifier.fillMaxSize()
    ) {
        Column(
            Modifier
                .padding(horizontal = 16.dp)
        ) {
            HorizontalDivider(
                Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = stringResource(id = R.string.description),
                Modifier.paddingFromBaseline(bottom = 16.dp),
                fontSize = TextUnit(18.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                text = event?.eventDescription!!,
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground
            )

            HorizontalDivider(
                Modifier.padding(vertical = 8.dp)
            )
        }

        Text(
            text = stringResource(id = R.string.other_details),
            Modifier
                .padding(start = 16.dp)
                .paddingFromBaseline(bottom = 16.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = TextUnit(18.0f, TextUnitType.Sp))

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Group: ",
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
            Text(text = event?.groupName!!,
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Activity created by:",
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
            Text(event?.eventCreator!!,
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Created at:",
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
            Text(text = DateFormatter.formatDateTime(event?.dateCreated!!),
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Levy amount:",
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
            Text(text = event?.levyAmount.toString(),
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "No. that have paid:",
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
            Text(text = event?.paidMembersList?.size.toString(),
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Amount realized so far:",
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
            Text(text = event?.amountRealized.toString(),
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = if (event?.paidMembersList?.contains(userId) == true) {
                    "You have paid for this activity" } else "You are yet to pay for this activity",
                color = if (event?.paidMembersList?.contains(userId) == true) {
                    Color.Green
                } else {
                    Color.Red
                },
                fontSize = TextUnit(14.0f, TextUnitType.Sp)
            )
        }

        Button(
            onClick = {
                navController.navigate("payment") {
                    launchSingleTop = true
                }
            },
            Modifier
                .fillMaxWidth()
                .padding(start = 64.dp, end = 64.dp, top = 32.dp)
            ) {
            Text(text = stringResource(id = R.string.pay_now))
        }
        HorizontalDivider(
            Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
        )

        PaidListHeader(groupViewModel, event, showPaidList) {
            showPaidList = it
        }
        if (showPaidList) {
            if (!event?.paidMembersList.isNullOrEmpty()) {
                event?.paidMembersList?.forEach {
                    val member: Member? = authViewModel.fetchMemberDetailsById(it)
                    if (member != null) {
                        Row(
                            Modifier
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = member.fullName,
                                fontSize = TextUnit(14.0f, TextUnitType.Sp)
                            )
                            Text(text = member.memberPost,
                                fontSize = TextUnit(14.0f, TextUnitType.Sp))
                        }
                    }
                }
            }else {
                Text(
                    text = stringResource(id = R.string.no_payment),
                    Modifier.padding(start = 16.dp),
                    color = Color.DarkGray,
                    fontSize = TextUnit(14.0f, TextUnitType.Sp))
            }
        }
        HorizontalDivider(Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
        if (groupViewModel.isUserAdmin()) {
            AdminActions(event, groupViewModel, navController)
        }
    }
}

@Composable
fun AdminActions(event: Event?, groupViewModel: GroupViewModel, navController: NavController) {
    Surface(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(
                RoundedCornerShape(
                    bottomEnd = 32.dp,
                    bottomStart = 32.dp
                )
            ),
        color = MaterialTheme.colorScheme.primary
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.admin_actions),
                Modifier.padding(vertical = 18.dp, horizontal = 16.dp),
                fontSize = TextUnit(18.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    Modifier.clickable {  },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.completed_activity),
                        contentDescription = "",
                        Modifier.size(32.dp),
                        tint = Color.Green)
                    Text(text = stringResource(id = R.string.mark_completed),
                        fontSize = TextUnit(14.0f, TextUnitType.Sp),
                        color = MaterialTheme.colorScheme.onPrimary)
                }
                Column(
                    Modifier.clickable {  },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.archive_down_minimlistic),
                        contentDescription = "",
                        Modifier.size(32.dp),
                        tint = Color.Yellow)
                    Text(text = stringResource(id = R.string.archive),
                        fontSize = TextUnit(14.0f, TextUnitType.Sp),
                        color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    Modifier.clickable {  },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete_activity),
                        contentDescription = "",
                        Modifier.size(32.dp),
                        tint = Color.Red)
                    Text(text = stringResource(id = R.string.delete_activity),
                        fontSize = TextUnit(14.0f, TextUnitType.Sp),
                        color = MaterialTheme.colorScheme.onPrimary)
                }
                Column(
                    Modifier.clickable {  },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.analytics),
                        contentDescription = "",
                        Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.surface)
                    Text(text = stringResource(id = R.string.generate_report),
                        fontSize = TextUnit(14.0f, TextUnitType.Sp),
                        color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@Composable
fun PaidListHeader(
    groupViewModel: GroupViewModel,
    event: Event?,
    showPaidList: Boolean,
    callback: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clickable { callback(!showPaidList) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.paid_list),
            fontSize = TextUnit(18.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold
        )
        if (showPaidList) {
            Icon(
                painter = painterResource(id = R.drawable.up_arrow_solid),
                contentDescription = "",
                Modifier
                    .size(16.dp)
                    .clickable { callback(false) }
            )
        }else {
            Icon(
                painter = painterResource(id = R.drawable.forward_arrow_solid),
                contentDescription = "",
                Modifier
                    .size(16.dp)
                    .clickable { callback(true) }
            )
        }
    }
}

@Composable
fun CommentBox(groupViewModel: GroupViewModel, event: Event?, dataStoreManager: DataStoreManager) {
    var commentText by rememberSaveable {  mutableStateOf("")}
    val screenWidth = LocalConfiguration.current.screenWidthDp - 84
    val coroutineScope = rememberCoroutineScope()
    Row(
        Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
        ) {
        TextField(
            value = commentText,
            onValueChange ={ commentText = it },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            placeholder = { Text(text = stringResource(id = R.string.comment)) },
            modifier = Modifier.width(screenWidth.dp)

        )
        IconButton(onClick = { coroutineScope.launch {
            val newComment = groupViewModel.postComment(
               NewEventDto(dataStoreManager.readAuthData(), commentText), event?.eventId) }
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send reply",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ReactToEvent(likeList: Int, unlikeList: Int, loveList: Int) {
    Row(
        modifier =Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            Modifier
                .height(28.dp)
                .clickable { }
                .padding(horizontal = 2.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = painterResource(id = R.drawable.unlike),
                    contentDescription = "React",
                    modifier = Modifier
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(text = unlikeList.toString())
            }
        }

        Surface(
            Modifier
                .height(28.dp)
                .clickable { }
                .padding(horizontal = 2.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = painterResource(id = R.drawable.like),
                    contentDescription = "React",
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(text = likeList.toString())
            }
        }

        Surface(
            Modifier
                .height(28.dp)
                .clickable { }
                .padding(horizontal = 2.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = painterResource(id = R.drawable.heart),
                    contentDescription = "React",
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .size(24.dp),
                    tint = Color.Red
                )
                Text(text = loveList.toString())
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: EventComment,
    groupViewModel: GroupViewModel,
    dataStoreManager: DataStoreManager
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp - 8
    var showCommentReplyBox by rememberSaveable { mutableStateOf(false) }
    var commentReply by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var commentReplyList by rememberSaveable { mutableStateOf(listOf<CommentReply>()) }
    Surface(
        modifier = Modifier
            .padding(vertical = 1.dp)
            .width(screenWidth.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.extraSmall
    ) {
        ConstraintLayout {
            val (commentText, replyText, replyBox, replyList, userName, time ) = createRefs()
            Text(
                text = "${comment.username}: ",
                fontSize = TextUnit(10.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.constrainAs(userName) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start, margin = 4.dp)
                }
            )
            Text(
                text = comment.comment,
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                modifier = Modifier
                    .constrainAs(commentText) {
                        start.linkTo(parent.start, margin = 8.dp)
                        top.linkTo(userName.bottom, margin = 2.dp)
                    }
            )
            Text(
                text = comment.dateOfComment,
                fontSize = TextUnit(10.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.constrainAs(time) {
                    top.linkTo(parent.top, margin = 4.dp)
                    end.linkTo(parent.end, margin = 4.dp)
                }
            )
            if (commentReplyList.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .constrainAs(replyList) {
                            top.linkTo(userName.bottom)
                            end.linkTo(parent.end, margin = 4.dp)
                        }
                ) {
                    CommentReplyList(commentReplyList)
                }
            }

            Text(
                text = stringResource(id = R.string.reply),
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .clickable { showCommentReplyBox = true }
                    .constrainAs(replyText) {
                        end.linkTo(parent.end, margin = 4.dp)
                        bottom.linkTo(parent.bottom, margin = 2.dp)
                    }
            )
            if (showCommentReplyBox) {
                Row(
                    modifier = Modifier.constrainAs(replyBox) {
                        top.linkTo(commentText.bottom, margin = 2.dp)
                        start.linkTo(parent.start, margin = 4.dp)
                    }
                ) {
                    TextField(
                        value = commentReply,
                        onValueChange ={ commentReply = it },
                        modifier = Modifier.width((screenWidth - 84).dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedContainerColor = MaterialTheme.colorScheme.background
                        ),
                        placeholder = { Text(text = stringResource(id = R.string.reply)) }
                    )
                    IconButton(onClick = { coroutineScope.launch {
                        val newReplyItem = groupViewModel.postCommentReply(
                            commentReply,
                            comment.commentId
                        )
                        if (newReplyItem != null) {
                            val newCommentReplyList = commentReplyList.toMutableList() // creates a mutable list of the reply list defined above
                            newCommentReplyList.add(newReplyItem) //adds the new item to the list
                            commentReplyList = newCommentReplyList //reassign the reply list with the updated list
                        }
                    } }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send reply",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CommentReplyList(commentReplyList: List<CommentReply>) {
    val screenHeight = LocalConfiguration.current.screenHeightDp - 64
    LazyColumn(
        modifier = Modifier.height(240.dp)
    ) {
        items(commentReplyList) { reply ->
            ReplyItem(reply)
        }
    }
}

@Composable
fun ReplyItem(reply: CommentReply) {
    ConstraintLayout {
        val (userName, comment, time) = createRefs()

        Text(
            text = "${reply.username}: ",
            fontSize = TextUnit(10.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.constrainAs(userName) {
                top.linkTo(parent.top)
                start.linkTo(parent.start, margin = 4.dp)
            }
        )
        Text(
            text = reply.reply,
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            modifier = Modifier
                .constrainAs(comment) {
                    start.linkTo(parent.start, margin = 8.dp)
                    top.linkTo(userName.bottom, margin = 2.dp)
                }
        )
        Text(
            text = reply.dateOfComment,
            fontSize = TextUnit(10.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.constrainAs(time) {
                top.linkTo(comment.bottom)
                end.linkTo(parent.end, margin = 4.dp)
            }
        )
    }
}
