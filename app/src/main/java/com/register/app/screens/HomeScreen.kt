package com.register.app.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.register.app.R
import com.register.app.dto.ReactionType
import com.register.app.dto.ScreenLoadState
import com.register.app.model.Event
import com.register.app.util.ADMIN
import com.register.app.util.BottomNavBar
import com.register.app.util.CircularIndicator
import com.register.app.util.DataStoreManager
import com.register.app.util.ImageLoader
import com.register.app.viewmodel.HomeViewModel

@Composable
fun HomeScreen(homeViewModel : HomeViewModel, navController: NavController, dataStoreManager: DataStoreManager) {
    val userRole: String? = homeViewModel.checkUserRole()
    Scaffold(
        topBar = { HomeTopBar(navController, homeViewModel, dataStoreManager) },
        bottomBar = { BottomNavBar(navController) },
        floatingActionButton = { if (userRole == ADMIN) {
            NewEventActionButton(dataStoreManager, homeViewModel, navController) }},
        floatingActionButtonPosition = FabPosition.End
    ) {
        EventFeeds(modifier = Modifier.padding(it), homeViewModel, dataStoreManager, navController)
    }
}

@Composable
fun NewEventActionButton(
    dataStoreManager: DataStoreManager,
    homeViewModel: HomeViewModel,
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EventFeeds(
    modifier: Modifier,
    homeViewModel: HomeViewModel,
    dataStoreManager: DataStoreManager,
    navController: NavController
) {
    val loadingState = homeViewModel.loadingState?.observeAsState()?.value
    val isRefreshing by rememberSaveable { mutableStateOf(false)}
    val refreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { homeViewModel.refreshHomeContents() },
        refreshThreshold = 84.dp,
        refreshingOffset = 64.dp)

    when (loadingState) {
        ScreenLoadState.LOADING -> { CircularIndicator() }
        ScreenLoadState.ERROR -> { ErrorState(homeViewModel) }
        else -> {
            Surface(
                modifier = Modifier
                    .padding(top = 64.dp)
                    .fillMaxSize()
                    .pullRefresh(refreshState, true),
                color = MaterialTheme.colorScheme.background
            ) {
                ConstraintLayout(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val (fab, feedList, groupSuggestions) = createRefs()

//                    Text(
//                        text = stringResource(id = R.string.feeds),
//                        fontSize = TextUnit(20.0f, TextUnitType.Sp),
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier.constrainAs(feedHeader) {
//                            start.linkTo(parent.start, margin = 8.dp)
//                            top.linkTo(parent.top)
//                        }
//                    )

                    Surface(
                        modifier = Modifier.constrainAs(feedList) {
                            top.linkTo(parent.top, margin = 8.dp)
                        },
                        color = MaterialTheme.colorScheme.background
                    ) {
                        FeedList(homeViewModel, navController, dataStoreManager)
                    }
                }
            }
        }
    }
}

@Composable
fun FeedList(
    homeViewModel: HomeViewModel,
    navController: NavController,
    dataStoreManager: DataStoreManager
) {
    val feedList = homeViewModel.eventFeeds.observeAsState().value
    if (feedList?.isNotEmpty() == true) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            rememberLazyListState()
        ) {
            items(feedList) {eventFeed ->
                EventFeedItem(dataStoreManager, navController, homeViewModel, eventFeed)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventFeedItem(
    dataStoreManager: DataStoreManager,
    navController: NavController,
    homeViewModel: HomeViewModel,
    eventFeed: Event
) {
    val pageState = rememberPagerState(pageCount = { eventFeed.imageUrlList.size} )
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp - 32
    var likeList = 0
    var unlikeList = 0
    var loveList = 0

    eventFeed.eventReactions?.forEach { eventReaction ->
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
                    homeViewModel.setSelectedEvent(eventFeed)
                }
            }
            .fillMaxWidth()
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
                eventFeed.imageUrlList[pageState.currentPage]?.let { imageUrl  -> ImageLoader(imageUrl, context, 256, screenWidth) }
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
            text = eventFeed.eventTitle,
            fontSize = TextUnit(20.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.constrainAs(eventTitle) {
                top.linkTo(eventImages.top, margin = 10.dp)
                start.linkTo(eventImages.start, margin = 16.dp)
            }
        )

        Text(
            text = eventFeed.groupName,
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            modifier = Modifier.constrainAs(groupName) {
                top.linkTo(eventImages.bottom, margin = 8.dp)
                start.linkTo(parent.start, margin = 16.dp)
            }
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
            Text(text = eventFeed.eventCommentsCount.toString())
        }
    }
}

@Composable
fun ErrorState(homeViewModel: HomeViewModel) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(navController: NavController,
               homeViewModel: HomeViewModel,
               dataStoreManager: DataStoreManager) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
    ) {
        ProfilePictureLoader(dataStoreManager)
        TopAppBar(
            title = {  },
            modifier = Modifier.width((screenWidth - 40).dp),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background),
            actions = {
                IconButton(
                    onClick = { isExpanded = !isExpanded }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = stringResource(id = R.string.menu))
                }
                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false },
                    modifier = Modifier.width(160.dp)
                    ) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.settings)) },
                        onClick = {
                            isExpanded = false
                        },
                        colors = MenuDefaults.itemColors(

                        ),
                        leadingIcon = { Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(id = R.string.settings)
                        ) }
                    )

                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.privacy)) },
                        onClick = {
                            isExpanded = false
                        },
                        leadingIcon = { Icon(
                            imageVector = Icons.Default.PrivacyTip,
                            contentDescription = stringResource(id = R.string.privacy)
                        ) }
                    )

                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.about)) },
                        onClick = {
                            isExpanded = false
                        },
                        leadingIcon = { Icon(
                            imageVector = Icons.Default.Details,
                            contentDescription = stringResource(id = R.string.about)
                        ) }
                    )

                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.sign_out)) },
                        onClick = {
                            isExpanded = false
                        },
                        leadingIcon = { Icon(
                            imageVector = Icons.Default.ArrowOutward,
                            contentDescription = stringResource(id = R.string.sign_out)
                        ) }
                    )
                }
            }
        )
    }
}

@Composable
fun ProfilePictureLoader(dataStoreManager: DataStoreManager) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current).data(data = "imageUrl").apply(block = fun ImageRequest.Builder.() {
            transformations(CircleCropTransformation())
            crossfade(true)
            placeholder(R.drawable.user)
            error(R.drawable.user)
        }).build(),
        contentScale = ContentScale.Fit
    )
    Surface(
        modifier = Modifier.size(40.dp),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Image(
            painter = painter,
            contentDescription = ""
        )
    }
}

@Preview
@Composable
fun PreviewHome() {
    val context = LocalContext.current
    HomeScreen(
        homeViewModel = HomeViewModel(DataStoreManager(context)),
        navController = rememberNavController(),
        dataStoreManager = DataStoreManager(context))
}