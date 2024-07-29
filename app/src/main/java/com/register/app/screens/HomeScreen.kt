package com.register.app.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.register.app.R
import com.register.app.model.Event
import com.register.app.model.Member
import com.register.app.util.BottomNavBar
import com.register.app.util.CircularIndicator
import com.register.app.util.GroupItem
import com.register.app.util.ImageLoader
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.GroupViewModel
import com.register.app.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    homeViewModel : HomeViewModel,
    navController: NavController,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    activityViewModel: ActivityViewModel) {

    Scaffold(
        topBar = { HomeTopBar(navController, homeViewModel, authViewModel) },
        bottomBar = { BottomNavBar(navController) },
    ) {
            HomeScreenContent(
                Modifier.padding(it),
                homeViewModel,
                groupViewModel,
                activityViewModel,
                navController
            )

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier,
    homeViewModel: HomeViewModel,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    navController: NavController
) {
    val loadingState = homeViewModel.loadingState.observeAsState()?.value
    val screenHeight = LocalConfiguration.current.screenHeightDp - 64
    val isRefreshing by rememberSaveable { mutableStateOf(false)}
    val refreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { homeViewModel.refreshHomeContents() },
        refreshThreshold = 84.dp,
        refreshingOffset = 64.dp)
    Surface(
        modifier = Modifier
            .height(screenHeight.dp)
            .fillMaxWidth()

    ) {
        if (loadingState == true) {
            CircularIndicator()
        }
        LazyColumn(
            Modifier
                .padding(top = 64.dp)
                .fillMaxSize()
                .pullRefresh(refreshState),
            rememberLazyListState()
        ) {
            item { WelcomeNote() }
            item{ SearchSection(groupViewModel, navController) }
            item {DiscoverSection(groupViewModel, homeViewModel, navController) }
            item {TopGroups(homeViewModel, groupViewModel, navController) }
            item {ActivityFeedList(homeViewModel, navController, groupViewModel, activityViewModel) }
        }
    }
}

@Composable
fun WelcomeNote() {
    Text(
        text = stringResource(id = R.string.welcome_note),
        Modifier.padding(start = 16.dp),
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = TextUnit(18.0f, TextUnitType.Sp)
        )
}

@Composable
fun SearchSection(groupViewModel: GroupViewModel, navController: NavController) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    var searchTag by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    Row(
        Modifier
            .width(screenWidth.dp)
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        HomeSearchBox(screenWidth - 92, searchTag){
            searchTag = it
        }
        Surface(
            shape = MaterialTheme.shapes.medium,
            shadowElevation = dimensionResource(id = R.dimen.low_elevation),
            modifier = Modifier
                .padding(end = 8.dp)
                .size(55.dp),
            color = MaterialTheme.colorScheme.primary
        ) {
            Icon(painter = painterResource(id = R.drawable.search),
                contentDescription = "",
                Modifier
                    .clickable {
                        coroutineScope.launch {
                            val response = groupViewModel.searchGroupByName(searchTag)
                            if (response?.status == true) {
                                navController.navigate("suggested_groups")
                            }
                        }
                    }
                    .padding(16.dp),
                tint = MaterialTheme.colorScheme.onPrimary
                )
        }
    }
}

@Composable
fun HomeSearchBox(
    width: Int,
    searchTag: String,
    function: (searchTag: String) -> Unit
) {
    Surface(
        Modifier
            .padding(horizontal = 8.dp)
            .width(width.dp),
        color = MaterialTheme.colorScheme.onPrimary,
        shadowElevation = dimensionResource(id = R.dimen.low_elevation),
        shape = MaterialTheme.shapes.large
    ) {
        TextField(
            value = searchTag,
            onValueChange = { function(it) },
            modifier = Modifier
                .height(55.dp),
            placeholder = { Text(
                text = stringResource(id = R.string.search_group),
                color = Color.Gray) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "",
                    tint = Color.Gray)
            }
        )
    }
}

@Composable
fun DiscoverSection(
    groupViewModel: GroupViewModel,
    homeViewModel: HomeViewModel,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            Modifier.clickable {
                coroutineScope.launch {
                    groupViewModel.getAllGroupsForUser()
                }
              navController.navigate("groups")
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                Modifier
                    .size(64.dp),
                color = MaterialTheme.colorScheme.onTertiary,
                shape = MaterialTheme.shapes.small
            ) {
                Image(
                    painter = painterResource(id = R.drawable.groups),
                    contentDescription = "discover",
                    modifier = Modifier
                        .padding(10.dp)
                        .size(32.dp)
                )
            }
            Text(
                text = stringResource(id = R.string.your_groups),
                fontSize = TextUnit(12.0f, TextUnitType.Sp),
                textAlign = TextAlign.Center
            )
        }
        Column(
            Modifier.clickable {
                navController.navigate("colleagues") {
                    launchSingleTop = true
                }
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                Modifier
                    .size(64.dp),
                color = MaterialTheme.colorScheme.onTertiary,
                shape = MaterialTheme.shapes.small
            ) {
                Image(
                    painter = painterResource(id = R.drawable.customer_support),
                    contentDescription = "discover",
                    modifier = Modifier
                        .padding(10.dp)
                        .size(32.dp))
            }
            Text(text = stringResource(id = R.string.customer_support),
                fontSize = TextUnit(12.0f, TextUnitType.Sp),
                textAlign = TextAlign.Center)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                Modifier
                    .size(64.dp),
                color = MaterialTheme.colorScheme.onTertiary,
                shape = MaterialTheme.shapes.small
            ) {
                Image(
                    painter = painterResource(id = R.drawable.forum),
                    contentDescription = "discover",
                    modifier = Modifier
                        .padding(10.dp)
                        .size(32.dp))
            }
            Text(text = stringResource(id = R.string.explore),
                fontSize = TextUnit(12.0f, TextUnitType.Sp),
                textAlign = TextAlign.Center)
        }
        Column(
            Modifier.clickable { groupViewModel.showCreateGroupSheet.postValue(true)},
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                Modifier
                    .size(64.dp),
                color = MaterialTheme.colorScheme.onTertiary,
                shape = MaterialTheme.shapes.small
            ) {
                Image(
                    painter = painterResource(id = R.drawable.events),
                    contentDescription = "discover",
                    modifier = Modifier
                        .padding(10.dp)
                        .size(32.dp))
            }
            Text(text = stringResource(id = R.string.new_group),
                fontSize = TextUnit(12.0f, TextUnitType.Sp),
                textAlign = TextAlign.Center)

        }
    }
}
@Composable
fun TopGroups(homeViewModel: HomeViewModel, groupViewModel: GroupViewModel, navController: NavController) {
    val groupList = groupViewModel.groupListLiveData.observeAsState().value
    ConstraintLayout(
        Modifier
            .padding(top = 16.dp)
            .fillMaxWidth(),
    ) {
        val (header, list, showMore) = createRefs()
        Text(
            text = stringResource(id = R.string.top_groups),
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
            LazyRow(
                Modifier.constrainAs(list) {
                    top.linkTo(header.bottom, margin = 8.dp)
                    start.linkTo(parent.start, margin = 8.dp)
                }
            ) {
                items(groupList) { group ->
                    var admins by rememberSaveable { mutableStateOf<List<Member>?>(null) }
                    LaunchedEffect(key1 = 260) {
                        admins = group.memberList?.let { groupViewModel.filterAdmins(it) }
                    }
                    GroupItem(group, admins, groupViewModel, navController)
                }
            }
        }
    }
}

@Composable
fun ActivityFeedList(
    homeViewModel: HomeViewModel,
    navController: NavController,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel
) {
    val feedList = homeViewModel.eventFeeds.observeAsState().value
    if (feedList?.isNotEmpty() == true) {
        Column(
            Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(id = R.string.active_events),
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                fontSize = TextUnit(18.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                feedList.forEach { eventFeed ->
                    EventItemHome(navController, groupViewModel, activityViewModel, eventFeed)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventItemHome(
    navController: NavController,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    eventFeed: Event
) {
    val pageState = rememberPagerState(pageCount = { eventFeed.imageUrlList?.size!!} )
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier
            .clickable {
                coroutineScope.launch {
                    groupViewModel.reloadGroup(eventFeed.groupId) // load group details
                    activityViewModel.setSelectedEvent(eventFeed)
                    groupViewModel.getComplianceRate(eventFeed)
                    if (groupViewModel.groupDetailLiveData.value != null) {
                        groupViewModel.reloadGroup(eventFeed.groupId) // Load the details of the group that owns the activity
                        groupViewModel.isUserAdmin() // check if user is admin for the group that owns the selected activity
                        navController.navigate(route = "event_detail") {
                            launchSingleTop = true
                        }
                    }
                }
            }
            .padding(horizontal = 8.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.background,
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val (eventImages, dotIndicator, eventTitle, groupName, levyAmount, icon) = createRefs()
            Surface(
                modifier = Modifier
                    .width(84.dp)
                    .height(84.dp)
                    .constrainAs(eventImages) {
                        start.linkTo(parent.start)
                        centerVerticallyTo(parent)
                    },
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.background
            ) {
                HorizontalPager(
                    state = pageState,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    eventFeed.imageUrlList?.get(pageState.currentPage)?.let { imageUrl  -> ImageLoader(
                        imageUrl,
                        context,
                        84,
                        84,
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
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.constrainAs(eventTitle) {
                    top.linkTo(parent.top, margin = 4.dp)
                    start.linkTo(eventImages.end, margin = 8.dp)
                }
            )

            Text(
                text = eventFeed.groupName!!,
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                modifier = Modifier.constrainAs(groupName) {
                    top.linkTo(eventTitle.bottom, margin = 4.dp)
                    bottom.linkTo(levyAmount.top, margin = 4.dp)
                    start.linkTo(eventImages.end, margin = 8.dp)
                }
            )

            Text(
                text = "Levy: ${eventFeed.levyAmount.toString()}",
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                modifier = Modifier.constrainAs(levyAmount) {
                    bottom.linkTo(parent.bottom, margin = 4.dp)
                    start.linkTo(eventImages.end, margin = 8.dp)
                },
                color = MaterialTheme.colorScheme.onBackground,
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "",
                modifier = Modifier.constrainAs(icon) {
                    centerVerticallyTo(parent)
                    end.linkTo(parent.end, margin = 10.dp)
                })
        }
    }

}

@Composable
fun ErrorState(homeViewModel: HomeViewModel) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    navController: NavController,
    homeViewModel: HomeViewModel,
    authViewModel: AuthViewModel
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val coroutineScope = rememberCoroutineScope()
    val userData = authViewModel.userLideData.observeAsState().value
    Surface {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        ) {
            ProfilePictureLoader(userData)
            TopAppBar(
                title = {  Text(
                    text = "Welcome ${userData?.userName}!",
                    color = MaterialTheme.colorScheme.onBackground) } ,
                modifier = Modifier.width((screenWidth - 40).dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface),
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
}

@Composable
fun ProfilePictureLoader(user: Member?) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current).data(data = user?.imageUrl?: "").apply(block = fun ImageRequest.Builder.() {
            transformations(CircleCropTransformation())
            crossfade(true)
            placeholder(R.drawable.placeholder)
            error(R.drawable.placeholder)
        }).build(),
        contentScale = ContentScale.Fit
    )
    Surface(
        modifier = Modifier.size(40.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.onTertiary
    ) {
        Image(
            painter = painter,
            contentDescription = ""
        )
    }
}