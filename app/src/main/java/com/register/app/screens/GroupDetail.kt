package com.register.app.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.register.app.R
import com.register.app.dto.JoinChatPayload
import com.register.app.model.Group
import com.register.app.model.Member
import com.register.app.model.MembershipDto
import com.register.app.util.EventItem
import com.register.app.util.ImageLoader
import com.register.app.util.PAID
import com.register.app.util.UNPAID
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.ForumViewModel
import com.register.app.viewmodel.GroupViewModel
import com.register.app.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun GroupDetail(
    navController: NavController,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    forumViewModel: ForumViewModel,
    homeViewModel: HomeViewModel,
    activityViewModel: ActivityViewModel
) {
    var showAllMembers by rememberSaveable{ mutableStateOf(false) }
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    val isUserAdmin = groupViewModel.isUserAdminLiveData.observeAsState().value
    Scaffold(
        topBar = { GroupDetailTopBar(navController, group, groupViewModel, forumViewModel){showAllMembers = it} },
    ) {
        GroupDetailScreen(Modifier.padding(it), navController, groupViewModel, authViewModel, homeViewModel, activityViewModel, group)
        if (showAllMembers) {
            AllMembersList(group, groupViewModel, authViewModel, navController, isUserAdmin) {shouldShow -> showAllMembers = shouldShow}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailTopBar(
    navController: NavController,
    group: Group?,
    groupViewModel: GroupViewModel,
    forumViewModel: ForumViewModel,
    viewAllMembers: (show: Boolean) -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false)}
    val coroutineScope = rememberCoroutineScope()
    val isAdmin = groupViewModel.isUserAdminLiveData.observeAsState().value
    TopAppBar(
        title = { Text(
            text = group?.groupName!!,
            Modifier.padding(start = 32.dp)
            ) },
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "",
                Modifier
                    .size(20.dp)
                    .clickable {
                        navController.navigate("home") {
                            popUpTo("groups") { inclusive = true }
                        }
                    }
            ) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground
        ),
        actions = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Message,
                contentDescription = "",
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable {
                        coroutineScope.launch {
                            navController.navigate("forum") {
                                launchSingleTop = true
                            }
                            forumViewModel.connectToChat(JoinChatPayload(group?.groupName!!, group.groupId))
                            forumViewModel.setSelectedGroup(group)
                        }
                    }
                )

            if (isAdmin == true) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            navController.navigate("membership_request") {
                                launchSingleTop = true
                            }
                        },
                    contentAlignment = Alignment.TopEnd
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.invite_members),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(end = 14.dp)
                            .size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.extraLarge,
                        modifier = Modifier
                            //.clip(CircleShape)
                            //.size(24.dp)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = group?.pendingMemberRequests?.size.toString(),
                            color = Color.White,
                            modifier = Modifier.width(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "menu",
                Modifier.clickable { isExpanded = !isExpanded },
                tint = MaterialTheme.colorScheme.onBackground)
            
            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }) {
                if (isAdmin == true) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.update_group)) },
                        onClick = {
                            isExpanded = false
                            navController.navigate("update_group") {
                                launchSingleTop = true
                            }
                        })
                    DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.add_member)) },
                    onClick = {
                        navController.navigate("add_member") {
                            launchSingleTop = true
                        }
                        isExpanded = false
                    })

                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.create_election)) },
                        onClick = { isExpanded = false })
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.create_activity)) },
                        onClick = {
                            navController.navigate("create_event") {
                                launchSingleTop = true
                            }
                            isExpanded = false })
                }
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.view_members)) },
                    onClick = {
                        viewAllMembers(true)
                        isExpanded = false
                        coroutineScope.launch {groupViewModel.populateGroupMembers(group)}
                    })

                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.elections)) },
                    onClick = { isExpanded = false })
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GroupDetailScreen(
    modifier: Modifier,
    navController: NavController,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    activityViewModel: ActivityViewModel,
    group: Group?
) {
    var showProfileDetail by rememberSaveable { mutableStateOf(false) }
    var showAdminList by rememberSaveable { mutableStateOf(false) }
    var showActivities by rememberSaveable { mutableStateOf(true) }
    val verticalScrollState = rememberScrollState(initial = 0)
    val isRefreshing = groupViewModel.loadingState.observeAsState().value!!
    val coroutineScope = rememberCoroutineScope()
    val refreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { coroutineScope.launch { groupViewModel.reloadGroup(group?.groupId) } })
    Surface(
        Modifier
            .fillMaxSize()
            .verticalScroll(
                state = verticalScrollState,
                enabled = true,
                reverseScrolling = false
            )
            .pullRefresh(refreshState, true),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            Modifier
                .fillMaxWidth()
        ) {
            TopSection(group, groupViewModel)
            HorizontalDivider(Modifier.padding(vertical = 4.dp, horizontal = 16.dp), color = MaterialTheme.colorScheme.onTertiary)
            ActivityRate(groupViewModel)
            HorizontalDivider(Modifier.padding(vertical = 4.dp, horizontal = 16.dp), color = MaterialTheme.colorScheme.onTertiary)
            ActivitiesHeader(group, showActivities) { showActivities = it}
            if (showActivities) {
                Activities(groupViewModel, homeViewModel, activityViewModel, navController, group!!)
            }
            HorizontalDivider(Modifier.padding(vertical = 4.dp, horizontal = 16.dp), color = MaterialTheme.colorScheme.onTertiary)
            GroupProfileHeader(group, showProfileDetail) {showProfileDetail = it}
            if (showProfileDetail) {
                GroupProfile(group, groupViewModel)
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp, horizontal = 16.dp), color = MaterialTheme.colorScheme.onTertiary)
            GroupAdminHeader(group, groupViewModel, showAdminList) {showAdminList = it}

            if (showAdminList) {
                GroupAdminList(group, groupViewModel, navController)
            }
            HorizontalDivider(Modifier.padding(vertical = 4.dp, horizontal = 16.dp), color = MaterialTheme.colorScheme.onTertiary)

            HorizontalDivider(Modifier.padding(vertical = 4.dp, horizontal = 16.dp), color = MaterialTheme.colorScheme.onTertiary)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActivityRate(groupViewModel: GroupViewModel) {
    val context = LocalContext.current
    val activityRate = groupViewModel.activityRateLiveData.observeAsState().value
    val paymentRate = groupViewModel.paymentRateLiveData.observeAsState().value
    val percentPaid = (paymentRate?.eventsPaid?.times(100))?.div(if (paymentRate.eventsDue == 0) 1 else paymentRate.eventsDue)?.toFloat()
    val percentUnpaid = ((paymentRate?.eventsDue?.minus(paymentRate.eventsPaid))?.times(100))?.div(if (paymentRate.eventsDue == 0) 1 else paymentRate.eventsDue)?.toFloat()
    val chartData = PieChartData(
        slices = listOf(
            PieChartData.Slice("Contributions made", percentPaid?: 0f, MaterialTheme.colorScheme.primary),
            PieChartData.Slice("Contributions due", percentUnpaid?: 0f, MaterialTheme.colorScheme.surface)),
        plotType = PlotType.Donut
    )

    val donutChartConfig = PieChartConfig(
        sliceLabelTextColor = MaterialTheme.colorScheme.onBackground,
        showSliceLabels = true,
        labelFontSize = TextUnit(20.0f, TextUnitType.Sp),
        labelColor = MaterialTheme.colorScheme.onBackground,
        strokeWidth = 24f,
        activeSliceAlpha = .9f,
        labelVisible = true,
        isAnimationEnable = true,
        chartPadding = 16,
        backgroundColor = MaterialTheme.colorScheme.background
    )
    ConstraintLayout(
        Modifier
            .padding(top = 4.dp, bottom = 8.dp)
            .fillMaxWidth()
    ) {
        val (chart, title, detail, legend) = createRefs()

        Text(
            text = stringResource(id = R.string.chart_header),
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top, margin = 8.dp)
                start.linkTo(parent.start, margin = 16.dp)
            },
            fontWeight = FontWeight.SemiBold,
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onBackground
            )

        DonutPieChart(
            modifier = Modifier
                .size(140.dp)
                .constrainAs(chart) {
                    start.linkTo(parent.start, margin = 16.dp)
                    top.linkTo(title.bottom, margin = 16.dp)
                },
            pieChartData = chartData,
            pieChartConfig = donutChartConfig
        )
        Column(
            Modifier.constrainAs(legend) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end, margin = 16.dp)
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    Modifier
                        .height(8.dp)
                        .width(20.dp),
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small
                ) {}

                Text(
                    text = stringResource(id = R.string.paid_activities),
                    modifier = Modifier.padding(start = 8.dp)
                    )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    Modifier
                        .height(8.dp)
                        .width(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.small
                ) {}

                Text(
                    text = stringResource(id = R.string.unpaid_activities),
                    modifier = Modifier.padding(start = 8.dp)
                    )
            }
            
        }

        Text(
            text = "You have paid for ${paymentRate?.eventsPaid}/${paymentRate?.eventsDue} activities",
            modifier = Modifier.constrainAs(detail) {
                top.linkTo(chart.bottom, margin = 16.dp)
                centerHorizontallyTo(parent)
            },
            color = Color.Gray,
            fontSize = TextUnit(14.0f, TextUnitType.Sp)
        )

    }
}


@Composable
fun ActivitiesHeader(
    group: Group?,
    showActivities: Boolean,
    callback: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clickable { callback(!showActivities) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.activities),
            fontSize = TextUnit(18.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold
        )
        if (showActivities) {
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
fun Activities(
    groupViewModel: GroupViewModel,
    homeViewModel: HomeViewModel,
    activityViewModel: ActivityViewModel,
    navController: NavController,
    group: Group) {
    var showPaid by rememberSaveable { mutableStateOf(true) }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ActivitySwitch { showPaid = it }
        if (showPaid) {
            PaidEvents(groupViewModel, homeViewModel, activityViewModel, navController, group)
        }else {
            UnpaidEvents(groupViewModel, homeViewModel, activityViewModel, navController, group)
        }
    }
}

@Composable
fun PaidEvents(
    groupViewModel: GroupViewModel,
    homeViewModel: HomeViewModel,
    activityViewModel: ActivityViewModel,
    navController: NavController,
    group: Group?
) {
    val eventList = groupViewModel.paidActivities.observeAsState().value
    ConstraintLayout (
        Modifier
            .fillMaxWidth(),
    ) {
        val (header, list, shoeAll) = createRefs()

        if (eventList?.isNotEmpty() == true) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .constrainAs(list) {
                        top.linkTo(parent.top, margin = 16.dp)
                        start.linkTo(parent.start)
                    }
                ) {
                   eventList.forEach { event ->
                       EventItem(event, group!!, groupViewModel, activityViewModel, navController)
                   }
                }
            Text(
                text = stringResource(id = R.string.show_more),
                Modifier
                    .clickable {
                        groupViewModel.populateActivities(PAID)
                        navController.navigate("events/Paid Activities")
                    }
                    .constrainAs(shoeAll) {
                        end.linkTo(parent.end)
                        top.linkTo(list.bottom, margin = 4.dp)
                    },
                color = MaterialTheme.colorScheme.tertiary
                )
        }else{
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription ="",
                    Modifier
                        .size(32.dp)
                        .padding(vertical = 16.dp))
                Text(
                    text = stringResource(id = R.string.empty_event))
            }
        }
    }
}

@Composable
fun UnpaidEvents(
    groupViewModel: GroupViewModel,
    homeViewModel: HomeViewModel,
    activityViewModel: ActivityViewModel,
    navController: NavController,
    group: Group?
) {
    val eventList = groupViewModel.unpaidActivities.observeAsState().value
    ConstraintLayout (
        Modifier
            .fillMaxWidth(),
    ) {
        val (list, shoeAll) = createRefs()

        if (eventList?.isNotEmpty() == true) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .constrainAs(list) {
                        top.linkTo(parent.top, margin = 16.dp)
                        start.linkTo(parent.start)
                    }
            ) {
                eventList.forEach { event ->
                    EventItem(event, group!!, groupViewModel, activityViewModel, navController)
                }
            }
            Text(
                text = stringResource(id = R.string.show_more),
                Modifier
                    .clickable {
                        groupViewModel.populateActivities(UNPAID)
                        navController.navigate("events/Unpaid Activities")
                    }
                    .constrainAs(shoeAll) {
                        end.linkTo(parent.end)
                        top.linkTo(list.bottom, margin = 4.dp)
                    },
                color = MaterialTheme.colorScheme.tertiary
            )
        }else{
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription ="",
                    Modifier
                        .size(32.dp)
                        .padding(vertical = 16.dp))
                Text(
                    text = stringResource(id = R.string.no_unpaid_activities))
            }
        }
    }
}

@Composable
fun GroupProfileHeader(group: Group?, showProfileDetail: Boolean, showProfile: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clickable { showProfile(!showProfileDetail) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.group_profile),
            fontSize = TextUnit(18.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold
        )
        if (showProfileDetail) {
            Icon(
                painter = painterResource(id = R.drawable.up_arrow_solid),
                contentDescription = "",
                Modifier
                    .size(16.dp)
                    .clickable { showProfile(false) }
            )
        }else {
            Icon(
                painter = painterResource(id = R.drawable.forward_arrow_solid),
                contentDescription = "",
                Modifier
                    .size(16.dp)
                    .clickable { showProfile(true) }
            )
        }
    }
}

@Composable
fun TopSection(group: Group?, groupViewModel: GroupViewModel) {
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val membershipId = groupViewModel.membershipId.observeAsState().value

    ConstraintLayout(
        Modifier.fillMaxSize()
    ) {
        val (logo, description, id, groupSize) = createRefs()

        Surface(
            Modifier
                .size(88.dp)
                .clip(CircleShape)
                .constrainAs(logo) {
                    top.linkTo(parent.top, margin = 64.dp)
                    centerHorizontallyTo(parent)
                },
            color = MaterialTheme.colorScheme.onTertiary
        ) {
            ImageLoader(
                imageUrl = group?.logoUrl!!,
                context = context,
                height = 80,
                width = 80,
                placeHolder = R.drawable.download
            )
        }

        Text(
            text = "${group?.memberList?.size} members",
            modifier = Modifier
                .constrainAs(groupSize) {
                    centerHorizontallyTo(parent)
                    top.linkTo(logo.bottom, margin = 4.dp) },
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            color = Color.Gray
        )

        Text(
            text = "Membership ID: ${groupViewModel.membershipId.value}",
            modifier = Modifier
                .constrainAs(id) {
                    centerHorizontallyTo(parent)
                    top.linkTo(groupSize.bottom, margin = 8.dp) },
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = group?.groupDescription!!,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .constrainAs(description) {
                    top.linkTo(id.bottom, margin = 8.dp)
                    start.linkTo(parent.start, margin = 8.dp)
                },
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GroupProfile(group: Group?, groupViewModel: GroupViewModel) {
    Column(
        Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.address),
            modifier = Modifier.padding(vertical = 4.dp),
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Surface(
            Modifier
                .fillMaxWidth()
                .height(48.dp),
            border = BorderStroke(1.dp, Color.Gray),
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.background
        ) {
            Text(
                text = group?.address?: "",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = stringResource(id = R.string.group_email),
            modifier = Modifier.padding(vertical = 4.dp),
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Surface(
            Modifier
                .fillMaxWidth()
                .height(48.dp),
            border = BorderStroke(1.dp, Color.Gray),
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.background
        ) {
            Text(
                text = group?.groupEmail?: "",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = stringResource(id = R.string.phone),
            modifier = Modifier.padding(vertical = 4.dp),
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Surface(
            Modifier
                .fillMaxWidth()
                .height(48.dp),
            border = BorderStroke(1.dp, Color.Gray),
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.background
        ) {
            Text(
                text = group?.phoneNumber?: "",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = stringResource(id = R.string.date_established),
            modifier = Modifier.padding(vertical = 4.dp),
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Surface(
            Modifier
                .fillMaxWidth()
                .height(48.dp),
            border = BorderStroke(1.dp, Color.Gray),
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.background
        ) {
            Text(
                text = group?.dateCreated?: "",
                modifier = Modifier.padding(horizontal = 8.dp, 10.dp),
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = stringResource(id = R.string.created_by),
            modifier = Modifier.padding(vertical = 4.dp),
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Surface(
            Modifier
                .fillMaxWidth()
                .height(48.dp),
            border = BorderStroke(1.dp, Color.Gray),
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.background
        ) {
            Text(
                text = group?.creatorName?: "",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = stringResource(id = R.string.member_count),
            modifier = Modifier.padding(vertical = 4.dp),
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onBackground
        )
        Surface(
            Modifier
                .fillMaxWidth()
                .height(48.dp),
            border = BorderStroke(1.dp, Color.Gray),
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.background
        ) {
            Text(
                text = group?.memberList?.size.toString(),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun GroupAdminHeader(group: Group?, groupViewModel: GroupViewModel, showAdminList: Boolean, shouldShow: (Boolean) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clickable {
                coroutineScope.launch {
                    group?.memberList?.let { groupViewModel.filterAdmins(it) }
                }
                shouldShow(!showAdminList)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.group_admin),
            fontSize = TextUnit(18.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold
        )
        if (showAdminList) {
            Icon(
                painter = painterResource(id = R.drawable.up_arrow_solid),
                contentDescription = "",
                Modifier
                    .size(16.dp)
                    .clickable { shouldShow(false) }
            )
        }else {
            Icon(
                painter = painterResource(id = R.drawable.forward_arrow_solid),
                contentDescription = "",
                Modifier
                    .size(16.dp)
                    .clickable {
                        coroutineScope.launch {
                            group?.memberList?.let { groupViewModel.filterAdmins(it) }
                        }
                        shouldShow(true)
                    }
            )
        }
    }
}

@Composable
fun GroupAdminList(group: Group?, groupViewModel: GroupViewModel, navController: NavController) {
    val itemWidth = (LocalConfiguration.current.screenWidthDp / 2) - 36
    val isAdmin = groupViewModel.isUserAdminLiveData.observeAsState().value
    if (group?.memberList?.isNotEmpty() == true) {
        val loadingState = groupViewModel.loadingState.observeAsState().value
        val adminList: List<Member>? = groupViewModel.groupAdminList.observeAsState().value
        Column(
            Modifier
                .fillMaxWidth()
        ) {
            if (loadingState == true) {
                LinearProgressIndicator(
                    Modifier
                        .height(4.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    trackColor = MaterialTheme.colorScheme.secondary,
                )
            }
            if (adminList?.isNotEmpty() == true) {
                LazyRow(
                    Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .height(204.dp),
                    state = rememberLazyListState(),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(adminList) { admin ->
                        val membershipDto = group.memberList.find { membershipDto -> membershipDto.emailAddress == admin.emailAddress }
                        if (membershipDto != null) {
                            AdminItem(admin, membershipDto)
                        }
                    }
                    if (isAdmin == true) {
                        item{
                            Surface(
                                Modifier
                                    .height(200.dp)
                                    .width(itemWidth.dp)
                                    .padding(horizontal = 8.dp)
                                    .clickable {
                                        navController.navigate("modify_admin") {
                                            launchSingleTop = true
                                        }
                                    },
                                shape = MaterialTheme.shapes.small,
                                shadowElevation = dimensionResource(id = R.dimen.default_elevation),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                ConstraintLayout(
                                    Modifier.fillMaxSize()
                                ) {
                                    val (icon, text) = createRefs()
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "edit",
                                        Modifier.constrainAs(icon) {
                                            centerHorizontallyTo(parent)
                                            centerVerticallyTo(parent)
                                        })
                                    Text(
                                        text = stringResource(id = R.string.change_admin),
                                        Modifier.constrainAs(text) {
                                            top.linkTo(icon.bottom, margin = 8.dp)
                                            centerHorizontallyTo(parent)
                                        }
                                    )
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
fun AdminItem(admin: Member, membershipDto: MembershipDto) {
    val itemWidth = (LocalConfiguration.current.screenWidthDp / 2) - 36
    val context = LocalContext.current
    Surface(
        Modifier
            .width(itemWidth.dp)
            .height(200.dp)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        shadowElevation = dimensionResource(id = R.dimen.default_elevation),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.background
    ) {
        ConstraintLayout(
            Modifier.fillMaxWidth()
        ) {
            val (profilePic, name, office, phone, email) = createRefs()

            Text(
                text = membershipDto.memberOffice,
                Modifier.constrainAs(office) {
                    centerHorizontallyTo(parent)
                    top.linkTo(parent.top, margin = 8.dp) },
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Surface(
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .constrainAs(profilePic) {
                        centerHorizontallyTo(parent)
                        top.linkTo(office.bottom, margin = 16.dp)
                    },
                ) {
                ImageLoader(
                    imageUrl = admin.imageUrl ?: "",
                    context = context,
                    height = 44,
                    width = 44,
                    placeHolder = R.drawable.placeholder
                )
            }

            Text(
                text = admin.fullName,
                Modifier
                    .padding(horizontal = 2.dp)
                    .constrainAs(name) {
                        centerHorizontallyTo(parent)
                        top.linkTo(profilePic.bottom, margin = 8.dp)
                    },
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = admin.phoneNumber,
                Modifier.constrainAs(phone) {
                    centerHorizontallyTo(parent)
                    top.linkTo(name.bottom, margin = 8.dp) },
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllMembersList(
    group: Group?,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    navController: NavController,
    isUserAdmin: Boolean?,
    function: (show: Boolean) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val members = groupViewModel.memberDetailsList.observeAsState().value
    ModalBottomSheet(
        onDismissRequest = { function(false) },
        modifier = Modifier.height(screenHeight.dp),
        sheetState = sheetState,
        sheetMaxWidth = screenWidth.dp,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Text(
            text = stringResource(id = R.string.all_members),
            fontWeight = FontWeight.SemiBold,
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center)

        SearchField(members, group, navController, groupViewModel, authViewModel){function(it)}
    }
}

@Composable
fun SearchField(
    members: List<Member>?,
    group: Group?,
    navController: NavController,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    function: (Boolean) -> Unit
) {
    var searchTag by rememberSaveable { mutableStateOf("") }

        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onPrimary,
                shadowElevation = dimensionResource(id = R.dimen.low_elevation),
                shape = MaterialTheme.shapes.large
            ) {
                TextField(
                    value = searchTag,
                    onValueChange = { searchTag = it },
                    modifier = Modifier
                        .height(55.dp),
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.search_members),
                            color = Color.Gray
                        )
                    },
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
                            tint = Color.Gray
                        )
                    }
                )
            }
            if (members?.isNotEmpty() == true) {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(members.filter { member -> member.fullName.contains(searchTag, ignoreCase = true) }) { member ->
                        MemberItem(member, group, navController, groupViewModel, authViewModel){function(it)}
                }
            }
        }
    }
}

@Composable
fun MemberItem(
    member: Member,
    group: Group?,
    navController: NavController,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    displayCallback: (showState: Boolean) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clickable {
                coroutineScope.launch {
                    groupViewModel.setSelectedMember(member)
                    /*
                    * Since I have used the membershipDto list from group model to fetch member details in groupViewmodel
                    * I need to match each detail with the corresponding membershipDto*/
                    val membershipDto =
                        group?.memberList?.find { membershipDto -> membershipDto.emailAddress == member.emailAddress }
                    groupViewModel.setSelectedMembership(membershipDto!!)
                    Log.d("MEMBERSHIP", membershipDto.toString())
                    navController.navigate("member_detail") {
                        launchSingleTop = true
                    }
                    displayCallback(false)
                }
            },
        color = MaterialTheme.colorScheme.background,
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            Modifier.fillMaxWidth()
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    Modifier.padding(end = 16.dp),
                    color = MaterialTheme.colorScheme.background,
                    border = BorderStroke(1.dp, Color.Gray),
                    shape = MaterialTheme.shapes.small
                ) {
                    ImageLoader(
                        imageUrl = member.imageUrl ?: "",
                        context = context,
                        height = 42,
                        width = 42,
                        placeHolder = R.drawable.placeholder
                    )
                }

                Text(
                    text = member.fullName,
                    fontSize = TextUnit(16.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            HorizontalDivider(Modifier.padding(2.dp))
        }
    }
}

@Composable
fun ActivitySwitch(switchView: (showDetails: Boolean) -> Unit) {
    val screenWidth = LocalConfiguration.current.screenWidthDp / 2
    var showPaid by rememberSaveable { mutableStateOf(true)}
    ConstraintLayout(
        Modifier.fillMaxSize()
    ) {
        val (paid, unPaid, paidLiner, unPaidLiner) = createRefs()
        Text(
            text = stringResource(id = R.string.paid_activities),
            Modifier
                .width((screenWidth - 24).dp)
                .fillMaxHeight()
                .padding(top = 4.dp)
                .clickable {
                    showPaid = true
                    switchView(showPaid)
                }
                .constrainAs(paid) {
                    start.linkTo(parent.start)
                },
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(id = R.string.unpaid_activities),
            Modifier
                .width((screenWidth - 24).dp)
                .fillMaxHeight()
                .padding(top = 4.dp)
                .clickable {
                    showPaid = false
                    switchView(showPaid)
                }
                .constrainAs(unPaid) {
                    end.linkTo(parent.end)
                },
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold
        )
        if (showPaid) {
            Surface(
                Modifier
                    .width((screenWidth - 24).dp)
                    .height(4.dp)
                    .padding(vertical = 1.dp)
                    .constrainAs(paidLiner) {
                        start.linkTo(parent.start, margin = 1.dp)
                        top.linkTo(paid.bottom, margin = 8.dp)
                    },
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary
            ) {}
        }

        if (!showPaid) {
            Surface(
                Modifier
                    .width((screenWidth - 24).dp)
                    .height(4.dp)
                    .padding(vertical = 1.dp)
                    .constrainAs(unPaidLiner) {
                        end.linkTo(parent.end, margin = 1.dp)
                        top.linkTo(unPaid.bottom, margin = 8.dp)
                    },
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary
            ) {}
        }
    }
}
