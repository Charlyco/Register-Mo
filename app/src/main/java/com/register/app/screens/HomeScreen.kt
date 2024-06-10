package com.register.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.register.app.dto.ScreenLoadState
import com.register.app.model.Event
import com.register.app.model.Group
import com.register.app.util.BottomNavBar
import com.register.app.util.CircularIndicator
import com.register.app.util.DataStoreManager
import com.register.app.util.GroupItem
import com.register.app.util.ImageLoader
import com.register.app.viewmodel.GroupViewModel
import com.register.app.viewmodel.HomeViewModel

@Composable
fun HomeScreen(homeViewModel : HomeViewModel, navController: NavController, groupViewModel: GroupViewModel, dataStoreManager: DataStoreManager) {
    Scaffold(
        topBar = { HomeTopBar(navController, homeViewModel, dataStoreManager) },
        bottomBar = { BottomNavBar(navController) }
    ) {
        HomeScreenContent(modifier = Modifier.padding(it), homeViewModel, groupViewModel, navController)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier,
    homeViewModel: HomeViewModel,
    groupViewModel: GroupViewModel,
    navController: NavController
) {
    val scrollState = rememberScrollState(initial = 0)
    val height = LocalConfiguration.current.screenHeightDp - 64
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
                    .padding(top = 64.dp, bottom = 64.dp)
                    .fillMaxSize()
                    .pullRefresh(refreshState, true),
                color = MaterialTheme.colorScheme.background
            ) {
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(state = scrollState, enabled = true, reverseScrolling = false)
    ) {
        HorizontalDivider()
        YourGroups(groupViewModel, navController)
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        DiscoverSection(groupViewModel, homeViewModel, navController)
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        FeedList(homeViewModel, navController, groupViewModel)
                }
            }
        }
    }
}

@Composable
fun DiscoverSection(
    groupViewModel: GroupViewModel,
    homeViewModel: HomeViewModel,
    navController: NavController
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
           Surface(
               Modifier
                   .padding(8.dp)
                   .size(84.dp),
           ) {
               ConstraintLayout(
                   Modifier.fillMaxSize()
               ) {
                   val (image, text) = createRefs()
                   
                   Image(
                       painter = painterResource(id = R.drawable.discovery),
                       contentDescription = "discover",
                       modifier = Modifier
                           .size(32.dp)
                           .constrainAs(image){
                           centerHorizontallyTo(parent)
                           top.linkTo(parent.top, margin = 16.dp)
                       })
                   Text(text = stringResource(id = R.string.discover_tag),
                       modifier = Modifier.constrainAs(text){
                           top.linkTo(image.bottom, margin = 4.dp)
                           centerHorizontallyTo(parent)
                       },
                       fontSize = TextUnit(12.0f, TextUnitType.Sp),
                       textAlign = TextAlign.Center)
               }
           }
            Surface(
                Modifier
                    .padding(8.dp)
                    .size(84.dp),
            ) {
                ConstraintLayout(
                    Modifier.fillMaxSize()
                ) {
                    val (image, text) = createRefs()

                    Image(
                        painter = painterResource(id = R.drawable.network_connection),
                        contentDescription = "discover",
                        modifier = Modifier
                            .size(32.dp)
                            .constrainAs(image){
                                centerHorizontallyTo(parent)
                                top.linkTo(parent.top, margin = 16.dp)
                            })
                    Text(text = stringResource(id = R.string.new_group),
                        modifier = Modifier.constrainAs(text){
                            top.linkTo(image.bottom, margin = 4.dp)
                            centerHorizontallyTo(parent)
                        },
                        fontSize = TextUnit(12.0f, TextUnitType.Sp),
                        textAlign = TextAlign.Center)
                }
            }

            Surface(
                Modifier
                    .padding(8.dp)
                    .size(84.dp),
            ) {
                ConstraintLayout(
                    Modifier.fillMaxSize()
                ) {
                    val (image, text) = createRefs()

                    Image(
                        painter = painterResource(id = R.drawable.link_up),
                        contentDescription = "discover",
                        modifier = Modifier
                            .size(32.dp)
                            .constrainAs(image){
                                centerHorizontallyTo(parent)
                                top.linkTo(parent.top, margin = 16.dp)
                            })
                    Text(text = stringResource(id = R.string.link_up),
                        modifier = Modifier.constrainAs(text){
                            top.linkTo(image.bottom, margin = 4.dp)
                            centerHorizontallyTo(parent)
                        },
                        fontSize = TextUnit(12.0f, TextUnitType.Sp),
                        textAlign = TextAlign.Center)
                }
            }
        }
}

@Composable
fun YourGroups(groupViewModel: GroupViewModel, navController: NavController) {
    val groupList = groupViewModel.groupListLiveData.observeAsState().value
    ConstraintLayout(
        Modifier
            .fillMaxWidth(),
    ) {
        val (header, list, showMore) = createRefs()
        Text(
            text = stringResource(id = R.string.your_groups),
            Modifier
                .constrainAs(header) {
                    start.linkTo(parent.start, margin = 8.dp)
                    top.linkTo(parent.top)
                },
            fontSize = TextUnit(18.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (!groupList.isNullOrEmpty()) {
            Column(
                Modifier.constrainAs(list) {
                    top.linkTo(header.bottom, margin = 8.dp)
                    centerHorizontallyTo(parent)
                }
            ) {
                groupList.forEach { group ->
                    GroupItem(group, groupViewModel, navController)
                }
            }
        }else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = "")
                Text(text = stringResource(id = R.string.no_groups))
            }
        }
        Text(
            text = stringResource(id = R.string.show_more),
            Modifier
                .clickable {
                    navController.navigate("groups") {
                        launchSingleTop = true
                    }
                }
                .constrainAs(showMore) {
                    top.linkTo(list.bottom, margin = 4.dp)
                    end.linkTo(parent.end, margin = 8.dp)
                },
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun FeedList(
    homeViewModel: HomeViewModel,
    navController: NavController,
    groupViewModel: GroupViewModel
) {
    val feedList = homeViewModel.eventFeeds.observeAsState().value
    if (feedList?.isNotEmpty() == true) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(id = R.string.active_events),
                modifier = Modifier.padding(start = 8.dp, bottom = 16.dp),
                fontSize = TextUnit(18.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                rememberLazyListState()
            ) {
                items(feedList) {eventFeed ->
                    EventItem(navController, groupViewModel, eventFeed)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventItem(
    navController: NavController,
    groupViewModel: GroupViewModel,
    eventFeed: Event
) {
    val pageState = rememberPagerState(pageCount = { eventFeed.imageUrlList.size} )
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp

    Surface(
        modifier = Modifier
            .clickable {
                navController.navigate(route = "event_detail") {
                    launchSingleTop = true
                    groupViewModel.setSelectedEvent(eventFeed)
                }
            }
            .width((screenWidth - 32).dp)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.background,
        shadowElevation = dimensionResource(id = R.dimen.default_elevation)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val (eventImages, dotIndicator, eventTitle, groupName, levyAmount) = createRefs()
            Surface(
                modifier = Modifier
                    .width(96.dp)
                    .constrainAs(eventImages) {
                        start.linkTo(parent.start)
                        centerVerticallyTo(parent)
                    },
                shadowElevation = dimensionResource(id = R.dimen.low_elevation),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.background
            ) {
                HorizontalPager(
                    state = pageState,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    eventFeed.imageUrlList[pageState.currentPage]?.let { imageUrl  -> ImageLoader(
                        imageUrl,
                        context,
                        96,
                        196,
                        R.drawable.event
                    ) }
                }
            }

            Row(
                Modifier.constrainAs(dotIndicator) {
                    bottom.linkTo(eventImages.bottom, margin = 8.dp)
                    start.linkTo(eventImages.start)
                    end.linkTo(eventImages.end)
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
                        .size(8.dp))
                }
            }

            Text(
                text = eventFeed.eventTitle,
                fontSize = TextUnit(17.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.constrainAs(eventTitle) {
                    top.linkTo(parent.top, margin = 8.dp)
                    start.linkTo(eventImages.end, margin = 8.dp)
                }
            )

            Text(
                text = eventFeed.groupName,
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                modifier = Modifier.constrainAs(groupName) {
                    top.linkTo(eventTitle.bottom, margin = 8.dp)
                    start.linkTo(eventImages.end, margin = 8.dp)
                }
            )

            Text(
                text = "Levy: ${eventFeed.levyAmount.toString()}",
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                modifier = Modifier.constrainAs(levyAmount) {
                    top.linkTo(groupName.bottom, margin = 8.dp)
                    start.linkTo(eventImages.end, margin = 8.dp)
                },
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
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
    val coroutineScope = rememberCoroutineScope()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
    ) {
        ProfilePictureLoader(dataStoreManager)
        TopAppBar(
            title = { LaunchedEffect(key1 = 258) { dataStoreManager.readAuthData() } },
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
            placeholder(R.drawable.placeholder)
            error(R.drawable.placeholder)
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
        groupViewModel = GroupViewModel(DataStoreManager(context)),
        dataStoreManager = DataStoreManager(context)
    )
}