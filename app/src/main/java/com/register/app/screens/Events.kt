package com.register.app.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.dto.ReactionType
import com.register.app.model.Event
import com.register.app.util.GenericTopBar
import com.register.app.util.ImageLoader
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch

@Composable
fun Events(
    navController: NavController,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    title: String?,
    activityViewModel: ActivityViewModel
) {
    Scaffold(
        topBar = { GenericTopBar(
            title = title!!,
            navController = navController,
            navRoute = "group_detail"
        )},
        floatingActionButton = { if(authViewModel.isUserAdmin()) {
            NewEventActionButton(groupViewModel, navController) }},
        floatingActionButtonPosition = FabPosition.End
    ) {
        EventScreenDetail(Modifier.padding(it),navController, groupViewModel, activityViewModel)
    }
}

@Composable
fun EventScreenDetail(
    modifier: Modifier,
    navController: NavController,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel
) {
    Surface(
        Modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        EventList(navController, groupViewModel, activityViewModel)
    }
}

@Composable
fun EventList(navController: NavController, groupViewModel: GroupViewModel, activityViewModel: ActivityViewModel) {
    val feedList = groupViewModel.groupEvents.observeAsState().value
    if (feedList?.isNotEmpty() == true) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            rememberLazyListState()
        ) {
            items(feedList) {eventFeed ->
                EventFeedItem(navController, groupViewModel, activityViewModel, eventFeed)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventFeedItem(
    navController: NavController,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    eventFeed: Event
) {
    val pageState = rememberPagerState(pageCount = { eventFeed.imageUrlList?.size!!} )
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp - 32
    val coroutineScope = rememberCoroutineScope()
    var likeList = 0
    var unlikeList = 0
    var loveList = 0

    eventFeed.eventReactionsList?.forEach { eventReaction ->
        when (eventReaction.reactionType) {
            ReactionType.LIKE.name -> {likeList++}
            ReactionType.UNLIKE.name -> {unlikeList++}
            ReactionType.LOVE.name -> {loveList++}
        }
    }
    ConstraintLayout(
        modifier = Modifier
            .clickable {
                navController.navigate("event_detail") {
                    launchSingleTop = true
                    coroutineScope.launch { activityViewModel.setSelectedEvent(eventFeed) }
                }
            }
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        val (eventImages, dotIndicator, eventTitle, groupName, levyAmount, createdBy, date, comment, react) = createRefs()
        Surface(
            modifier = Modifier
                .width(screenWidth.dp)
                .constrainAs(eventImages) {
                    top.linkTo(parent.top, margin = 8.dp)
                    centerHorizontallyTo(parent)
                },
            shadowElevation = dimensionResource(id = R.dimen.default_elevation),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            HorizontalPager(
                state = pageState,
                modifier = Modifier
                    .fillMaxWidth()


            ) {
                eventFeed.imageUrlList?.get(pageState.currentPage)?.let {
                    imageUrl  -> ImageLoader(imageUrl, context, 256, screenWidth, R.drawable.event) }
            }
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
        Text(
            text = eventFeed.eventTitle!!,
            fontSize = TextUnit(20.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.constrainAs(eventTitle) {
                top.linkTo(eventImages.bottom, margin = 8.dp)
                start.linkTo(eventImages.start, margin = 16.dp)
            }
        )

        Text(
            text = eventFeed.groupName!!,
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            modifier = Modifier.constrainAs(groupName) {
                top.linkTo(eventTitle.bottom, margin = 4.dp)
                start.linkTo(eventImages.start, margin = 16.dp)
            },
            color = Color.DarkGray
        )

        Text(
            text = "Levy: ${eventFeed.levyAmount.toString()}",
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            modifier = Modifier.constrainAs(levyAmount) {
                top.linkTo(eventImages.bottom, margin = 8.dp)
                end.linkTo(parent.end, margin = 16.dp)
            }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.constrainAs(react) {
                top.linkTo(eventImages.top, margin = 16.dp)
                end.linkTo(eventImages.end, margin = 16.dp)
            }
        ) {
            Row(
                modifier = Modifier.clickable {  }
            ) {
                Icon(painter = painterResource(id = R.drawable.unlike),
                    contentDescription = "React",
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(text = unlikeList.toString())
            }
            Row(
                modifier = Modifier.clickable {  }
            ) {
                Icon(painter = painterResource(id = R.drawable.like),
                    contentDescription = "React",
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(text = likeList.toString())
            }
            Row(
                modifier = Modifier.clickable {  }
            ) {
                Icon(painter = painterResource(id = R.drawable.heart),
                    contentDescription = "React",
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(text = loveList.toString())
            }
        }

        Row(
            modifier = Modifier
                .clickable { }
                .constrainAs(comment) {
                    end.linkTo(eventImages.end, margin = 16.dp)
                    bottom.linkTo(eventImages.bottom, margin = 16.dp)
                },
        ) {
            Icon(imageVector = Icons.Default.AddComment,
                contentDescription = "React",
                modifier = Modifier
                    .padding(vertical = 2.dp)
                    .size(24.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Text(text = eventFeed.eventComments?.size.toString())
        }
    }
}

@Composable
fun NewEventActionButton(
    groupViewModel: GroupViewModel,
    navController: NavController
) {
    Surface(
        Modifier
            .size(56.dp)
            .clip(CircleShape)
            .clickable { },
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = dimensionResource(id = R.dimen.default_elevation)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "",
            Modifier.size(24.dp)
        )
    }
}