package com.register.app.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.remember
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.register.app.MainActivity
import com.register.app.R
import com.register.app.dto.SpecialLevy
import com.register.app.model.Event
import com.register.app.model.Member
import com.register.app.util.BottomNavBar
import com.register.app.util.CircularIndicator
import com.register.app.util.GroupItem
import com.register.app.util.ImageLoader
import com.register.app.util.PAY_SPECIAL_LEVY
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.GroupViewModel
import com.register.app.viewmodel.HomeViewModel
import com.register.app.viewmodel.QuestionnaireViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    homeViewModel : HomeViewModel,
    navController: NavController,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    questionnaireViewModel: QuestionnaireViewModel,
    activityViewModel: ActivityViewModel,
    mainActivity: MainActivity) {

    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    BackHandler {
        showExitDialog = true
    }

    Scaffold(
        topBar = { HomeTopBar(authViewModel, homeViewModel, navController) },
        bottomBar = { BottomNavBar(navController) },
    ) {
            HomeScreenContent(
                Modifier.padding(it),
                homeViewModel,
                groupViewModel,
                activityViewModel,
                authViewModel,
                questionnaireViewModel,
                navController
            )
        CreateGroupScreen(groupViewModel = groupViewModel, navController) { show->
            groupViewModel.showCreateGroupSheet.postValue(show)
        }
        
        if (showExitDialog) {
            AlertDialog(
                title = { Text(text = stringResource(id = R.string.exit_app_title)) },
                text = { Text(text = stringResource(id = R.string.exit_app)) },
                onDismissRequest = { showExitDialog = false },
                confirmButton = {
                    mainActivity.finish()
                },
                icon = { Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "") }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier,
    homeViewModel: HomeViewModel,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    questionnaireViewModel: QuestionnaireViewModel,
    navController: NavController
) {
    val homeLoadingState = homeViewModel.loadingState.observeAsState().value?: authViewModel.progressLiveData.observeAsState().value
    val groupLoadingState = authViewModel.progressLiveData.observeAsState().value
    val specialLevies = activityViewModel.unpaidSpecialLevyList.observeAsState().value
    val screenHeight = LocalConfiguration.current.screenHeightDp - 64
    val isRefreshing by rememberSaveable { mutableStateOf(false)}
    val coroutineScope = rememberCoroutineScope()
    val refreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            coroutineScope.launch {
                authViewModel.reloadUserData()
                homeViewModel.refreshHomeContents()
                activityViewModel.refreshHomeContents()
                groupViewModel.getAllGroupsForUser()
            }
                    },
        refreshThreshold = 84.dp,
        refreshingOffset = 64.dp)
    Surface(
        modifier = Modifier
            .height(screenHeight.dp)
            .fillMaxWidth()

    ) {
        if (homeLoadingState == true || groupLoadingState == true) {
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
            item {DiscoverSection(groupViewModel, authViewModel, homeViewModel, navController) }
            item {TopGroups(questionnaireViewModel, groupViewModel, navController, authViewModel, activityViewModel) }
            if (!specialLevies.isNullOrEmpty()) {
                item {SpecialLevySection(specialLevies, navController, activityViewModel, groupViewModel) }
            }
            item {ActivityFeedList(navController, groupViewModel, activityViewModel) }
        }
    }
}

@Composable
fun SpecialLevySection(
    specialLevies: List<SpecialLevy>,
    navController: NavController,
    activityViewModel: ActivityViewModel,
    groupViewModel: GroupViewModel
) {
    val outstandingLevies = specialLevies.filter { it.confirmedPayments?.isEmpty() == true }
    Column(
        Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.special_levy),
            Modifier
                .padding(start = 8.dp, bottom = 4.dp)
                .fillMaxWidth(),
            fontSize = TextUnit(18.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        outstandingLevies.forEach { levy ->
            SpecialLevyItem(levy, navController, activityViewModel, groupViewModel)
        }
    }
}

@Composable
fun SpecialLevyItem(
    levy: SpecialLevy,
    navController: NavController,
    activityViewModel: ActivityViewModel,
    groupViewModel: GroupViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    Surface(
        modifier = Modifier
            .clickable {
                coroutineScope.launch {
                    activityViewModel.setSelectedSpecialLevy(levy)
                    groupViewModel.reloadGroup(levy.groupId) // load group details
                    navController.navigate(route = PAY_SPECIAL_LEVY) {
                        launchSingleTop = true
                    }
                }
            }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val (eventTitle, groupName, levyAmount, icon) = createRefs()

            Text(
                text = levy.levyTitle!!,
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.constrainAs(eventTitle) {
                    top.linkTo(parent.top, margin = 4.dp)
                    start.linkTo(parent.start, margin = 8.dp)
                },
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = levy.groupName!!,
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                modifier = Modifier.constrainAs(groupName) {
                    top.linkTo(eventTitle.bottom, margin = 4.dp)
                    bottom.linkTo(levyAmount.top, margin = 4.dp)
                    start.linkTo(parent.start, margin = 8.dp)
                }
            )

            Text(
                text = "Levy: ${levy.levyAmount.toString()}",
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                modifier = Modifier.constrainAs(levyAmount) {
                    bottom.linkTo(parent.bottom, margin = 4.dp)
                    start.linkTo(parent.start, margin = 8.dp)
                },
                color = MaterialTheme.colorScheme.onBackground,
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "",
                modifier = Modifier.constrainAs(icon) {
                    centerVerticallyTo(parent)
                    end.linkTo(parent.end, margin = 10.dp)
                }
            )
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
    val context = LocalContext.current
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
                            } else {
                                Toast
                                    .makeText(context, response?.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }
                    .padding(16.dp),
                tint = Color(context.getColor(R.color.background_color))
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
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 8.dp),
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
                navController.navigate("support") {
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
fun TopGroups(
    questionnaireViewModel: QuestionnaireViewModel,
    groupViewModel: GroupViewModel,
    navController: NavController,
    authViewModel: AuthViewModel,
    activityViewModel: ActivityViewModel
) {
    val groupList = groupViewModel.groupListLiveData.observeAsState().value
    val itemWidth = LocalConfiguration.current.screenWidthDp - 16
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
            Column(
                Modifier.constrainAs(list) {
                    top.linkTo(header.bottom, margin = 8.dp)
                    start.linkTo(parent.start, margin = 8.dp)
                }
            ) {
                groupList.take(3).forEach { group ->
                    var admins by remember { mutableStateOf<List<Member>?>(null) }
                    LaunchedEffect(groupList) {
                        admins = group.memberList?.let { groupViewModel.filterAdmins(it) }
                    }
                    GroupItem(group, admins, groupViewModel, questionnaireViewModel, activityViewModel, navController, itemWidth, authViewModel)
                }
            }
        }
        Text(
            text = stringResource(id = R.string.show_more),
            modifier = Modifier
                .clickable { navController.navigate("groups") }
                .padding(end = 8.dp, top = 4.dp)
                .constrainAs(showMore) {
                    end.linkTo(parent.end, margin = 8.dp)
                    top.linkTo(list.bottom, margin = 8.dp)
                },
            textAlign = TextAlign.End,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun ActivityFeedList(
    navController: NavController,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel
) {
    val feedList = activityViewModel.eventFeeds.observeAsState().value
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
                feedList.take(3).forEach { eventFeed ->
                    EventItemHome(navController, groupViewModel, activityViewModel, eventFeed)
                }
            }
            Text(
                text = stringResource(id = R.string.show_more),
                modifier = Modifier
                    .clickable { navController.navigate("all_user_activities") }
                    .padding(end = 8.dp, top = 4.dp, bottom = 8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.secondary
            )
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
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier
            .clickable {
                coroutineScope.launch {
                    activityViewModel.setSelectedEvent(eventFeed)
                    groupViewModel.reloadGroup(eventFeed.groupId) // load group details
                    navController.navigate(route = "event_detail") {
                        launchSingleTop = true
                    }
                    // check if user is admin for the group that owns the selected activity
                    groupViewModel.getComplianceRate(eventFeed)
                }
            }
            .padding(horizontal = 8.dp, vertical = 4.dp),
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
                    .width(80.dp)
                    .height(80.dp)
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
                        80,
                        80,
                        R.drawable.placeholder_doc
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
                },
                color = MaterialTheme.colorScheme.primary
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    navController: NavController
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val userData = authViewModel.userLideData.observeAsState().value
    val coroutineScope = rememberCoroutineScope()
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
                    Icon(imageVector = Icons.Default.Notifications,
                        contentDescription = stringResource(
                        id = R.string.notifications),
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                navController.navigate("notifications") {
                                    launchSingleTop = true
                                    coroutineScope.launch {
                                        homeViewModel.populateNotifications()
                                    }
                                }
                            }
                        )
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