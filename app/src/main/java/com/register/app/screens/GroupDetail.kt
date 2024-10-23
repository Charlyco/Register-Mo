package com.register.app.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.RadioButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.register.app.R
import com.register.app.dto.JoinChatPayload
import com.register.app.dto.SpecialLevy
import com.register.app.enums.Designation
import com.register.app.enums.FormStatus
import com.register.app.model.Group
import com.register.app.model.Member
import com.register.app.util.CircularIndicator
import com.register.app.util.EventItem
import com.register.app.util.GROUP_NOTIFICATIONS
import com.register.app.util.ImageLoader
import com.register.app.util.PAID
import com.register.app.util.SPECIAL_LEVY_DETAIL
import com.register.app.util.UNPAID
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.ForumViewModel
import com.register.app.viewmodel.GroupViewModel
import com.register.app.viewmodel.HomeViewModel
import com.register.app.viewmodel.QuestionnaireViewModel
import kotlinx.coroutines.launch

@Composable
fun GroupDetail(
    navController: NavController,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    forumViewModel: ForumViewModel,
    homeViewModel: HomeViewModel,
    questionnaireViewModel: QuestionnaireViewModel,
    activityViewModel: ActivityViewModel
) {
    var showAllMembers by rememberSaveable{ mutableStateOf(false) }
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    val membershipId = groupViewModel.membershipId.observeAsState().value
    val isUserAdmin = groupViewModel.isUserAdminLiveData.observeAsState().value
    val questionnaires = questionnaireViewModel.groupQuestionnaires.observeAsState().value
    var showQuestionnaireDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(group) {
        activityViewModel.getActivitiesForGroup(group!!)
    }

    LaunchedEffect(questionnaires) {
        if (questionnaires != null) {
            showQuestionnaireDialog = questionnaires.any {
                it.status ==  FormStatus.ACTIVE.name && !it.responders.contains(membershipId?:"")}
        }
    }

    BackHandler {
        navController.navigate("home") {
            popUpTo("group_detail") {inclusive = true}
            launchSingleTop = true
        }
    }

    Scaffold(
        topBar = { GroupDetailTopBar(
            navController,
            group,
            groupViewModel,
            forumViewModel,
            authViewModel,
            questionnaireViewModel,
            activityViewModel
            ){showAllMembers = it} },
    ) {
        GroupDetailScreen(
            Modifier.padding(it),
            navController,
            groupViewModel,
            authViewModel,
            homeViewModel,
            activityViewModel,
            group)
        if (showAllMembers) {
            MembersListBottomSheet(
                group,
                groupViewModel,
                forumViewModel,
                navController,
                isUserAdmin) {shouldShow -> showAllMembers = shouldShow}
        }
            if(showQuestionnaireDialog) {
                QuestionnaireDialog(navController) { showQuestionnaireDialog = it}
        }
    }
}

@Composable
fun QuestionnaireDialog(navController: NavController, onDismiss: (Boolean) -> Unit) {
    Dialog(
        onDismissRequest = { onDismiss(false) },
        ) {
        Surface(
            Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(horizontal = 24.dp),
            shape = MaterialTheme.shapes.small

        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.questionnaire_waiting),
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 16.dp)
                )

                Button(
                    onClick = {
                        onDismiss(false)
                        navController.navigate("quest_response") {
                            launchSingleTop = true
                        }
                    },
                    ) {
                    Text(text = stringResource(id = R.string.take_action))
                }
            }
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
    authViewModel: AuthViewModel,
    questionnaireViewModel: QuestionnaireViewModel,
    activityViewModel: ActivityViewModel,
    viewAllMembers: (show: Boolean) -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false)}
    val coroutineScope = rememberCoroutineScope()
    val isAdmin = groupViewModel.isUserAdminLiveData.observeAsState().value
    TopAppBar(
        title = { Text(
            text = group?.groupName?: "Refresh screen",
            Modifier.padding(start = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
                    .padding(end = 8.dp)
                    .clickable {
                        coroutineScope.launch {
                            navController.navigate("forum") {
                                launchSingleTop = true
                            }
                            forumViewModel.connectToChat(
                                JoinChatPayload(
                                    group?.groupName!!,
                                    group.groupId
                                )
                            )
                            forumViewModel.setSelectedGroup(group)
                        }
                    }
                )
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable {
                        coroutineScope.launch {
                            navController.navigate(GROUP_NOTIFICATIONS) {
                                launchSingleTop = true
                            }
                            groupViewModel.getGroupNotifications(group?.groupId)
                        }
                    },
                tint = MaterialTheme.colorScheme.secondary
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
                            .padding(end = 8.dp)
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
                onDismissRequest = { isExpanded = false },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                ) {
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
                        isExpanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.create_activity)) },
                        onClick = {
                            navController.navigate("create_event") {
                                launchSingleTop = true
                            }
                            isExpanded = false
                        })
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.create_questionnaire)) },
                        onClick = {
                            navController.navigate("questionnaire") {
                                launchSingleTop = true
                            }
                            isExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.goto_questionnaire)) },
                        onClick = {
                            coroutineScope.launch {
                                questionnaireViewModel.getQuestionnaires(group?.groupId!!)
                                navController.navigate("quest_response") {
                                    launchSingleTop = true
                                }
                            }
                            isExpanded = false
                        }
                    )

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
                    onClick = {
                        isExpanded = false
                        groupViewModel.getGroupElections(group?.groupId)
                        navController.navigate("elections") {
                            launchSingleTop = true
                        }
                    }
                )

                DropdownMenuItem(
                    text = {Text(text = stringResource(id = R.string.leave_group))},
                    onClick = {
                        isExpanded = false
                        coroutineScope.launch {
                            val response = groupViewModel.leaveGroup(group)
                            if (response.status) {
                                authViewModel.reloadUserData()
                                navController.navigate("home") {
                                    popUpTo("group_detail") {inclusive = true}
                                }
                                authViewModel.reloadUserData()
                                groupViewModel.getAllGroupsForUser()
                            }
                        }
                    }
                )
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
    var showActivities by rememberSaveable { mutableStateOf(false) }
    var showSpecialLevies by rememberSaveable { mutableStateOf(false) }
    val loadingState = groupViewModel.loadingState.observeAsState().value
    val isRefreshing = groupViewModel.loadingState.observeAsState().value!!
    val isAdmin = groupViewModel.isUserAdminLiveData.observeAsState().value
    val coroutineScope = rememberCoroutineScope()
    val refreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { coroutineScope.launch {
            groupViewModel.reloadGroup(group?.groupId)
            //activityViewModel.getAllSpecialLeviesForGroup(group?.groupId!!)
        } })
    Surface(
        Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (loadingState == true) {
            CircularIndicator()
        }
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .pullRefresh(refreshState, true),
            state = rememberLazyListState()
        ) {
            item{ TopSection(group, groupViewModel) }
            item{ HorizontalDivider(Modifier.padding(vertical = 4.dp, horizontal = 16.dp), color = MaterialTheme.colorScheme.onTertiary) }
            item{ ActivityRate(activityViewModel) }
            item{ HorizontalDivider(Modifier.padding(vertical = 4.dp, horizontal = 16.dp), color = MaterialTheme.colorScheme.onTertiary) }
            item{ ActivitiesHeader(group, showActivities) { showActivities = it} }
            if (showActivities) {
                item{ Activities(groupViewModel, homeViewModel, activityViewModel, navController, group!!) }
            }
            item{ HorizontalDivider(Modifier.padding(vertical = 4.dp, horizontal = 16.dp), color = MaterialTheme.colorScheme.onTertiary) }
            item{ GroupProfileHeader(group, showProfileDetail) {showProfileDetail = it} }
            if (showProfileDetail) {
                item{ GroupProfile(group, groupViewModel) }
            }
            item{ HorizontalDivider(Modifier.padding(vertical = 8.dp, horizontal = 16.dp), color = MaterialTheme.colorScheme.onTertiary) }
//            if (isAdmin == true) {
//                item{ SpecialLeviesHeader(group, groupViewModel, showSpecialLevies) {showSpecialLevies = it} }
//                if (showSpecialLevies) {
//                    item{ SpecialLevies(group, groupViewModel, activityViewModel, navController) }
//                }
//            }
//            item{ HorizontalDivider(Modifier.padding(vertical = 4.dp, horizontal = 16.dp), color = MaterialTheme.colorScheme.onTertiary) }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActivityRate(activityViewModel: ActivityViewModel) {
    val paymentRate = activityViewModel.paymentRateLiveData.observeAsState().value
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
        strokeWidth = 32f,
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
                .size(120.dp)
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
    val membershipId = groupViewModel.membershipId.observeAsState().value
    val userEmail = group.memberList?.find { it.membershipId == membershipId }?.emailAddress
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ActivitySwitch(activityViewModel, userEmail) { showPaid = it }
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
    val eventList = activityViewModel.paidActivities.observeAsState().value
    val membershipId = groupViewModel.membershipId.observeAsState().value
    val userEmail = group?.memberList?.find { it.membershipId == membershipId }?.emailAddress
    val coroutineScope = rememberCoroutineScope()
    val paidSpecialLevies = activityViewModel.paidSpecialLevyList.observeAsState().value
    ConstraintLayout (
        Modifier
            .fillMaxWidth(),
    ) {
        val (header, list, shoeAll) = createRefs()

        if (eventList?.isNotEmpty() == true || paidSpecialLevies?.isNotEmpty() == true) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .constrainAs(list) {
                        top.linkTo(parent.top, margin = 16.dp)
                        start.linkTo(parent.start)
                    }
                ) {
                if( eventList?.isNotEmpty() == true ) {  // show regular events
                    eventList.forEach { event ->
                        EventItem(event, group!!, groupViewModel, activityViewModel, navController)
                    }
                }
                if (paidSpecialLevies?.isNotEmpty() == true) { // show special levies
                    paidSpecialLevies.forEach { levy ->
                        SpecialLevyItem(
                            levy = levy,
                            navController = navController,
                            activityViewModel = activityViewModel,
                            groupViewModel = groupViewModel
                        )
                    }
                }
            }
            Text(
                text = stringResource(id = R.string.show_more),
                Modifier
                    .clickable {
                        coroutineScope.launch {
                            activityViewModel.populateActivities(PAID, userEmail)
                            activityViewModel.getBulkPayments(group?.groupId)
                        }
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
                Surface(
                    Modifier
                        .padding(top = 16.dp)
                        .size(64.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_activity),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(Color.White),
                        contentScale = ContentScale.Fit
                        )
                }
                Text(
                    text = stringResource(id = R.string.empty_event),
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
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
    val eventList = activityViewModel.unpaidActivities.observeAsState().value
    val membershipId = groupViewModel.membershipId.observeAsState().value
    val userEmail = group?.memberList?.find { it.membershipId == membershipId }?.emailAddress
    val coroutineScope = rememberCoroutineScope()
    val unpaidSpecialLevies = activityViewModel.unpaidSpecialLevyList.observeAsState().value
    ConstraintLayout (
        Modifier
            .fillMaxWidth(),
    ) {
        val (list, shoeAll) = createRefs()

        if (eventList?.isNotEmpty() == true || unpaidSpecialLevies?.isNotEmpty() == true) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .constrainAs(list) {
                        top.linkTo(parent.top, margin = 16.dp)
                        start.linkTo(parent.start)
                    }
            ) {
                if( eventList?.isNotEmpty() == true ) {  // show regular events
                    eventList.forEach { event ->
                        EventItem(event, group!!, groupViewModel, activityViewModel, navController)
                    }
                }
                if (unpaidSpecialLevies?.isNotEmpty() == true) {  // show special levies
                    unpaidSpecialLevies.forEach { levy ->
                        SpecialLevyItem(
                            levy = levy,
                            navController = navController,
                            activityViewModel = activityViewModel,
                            groupViewModel = groupViewModel
                        )
                    }
                }
            }
            Text(
                text = stringResource(id = R.string.show_more),
                Modifier
                    .clickable {
                        coroutineScope.launch {
                            activityViewModel.populateActivities(UNPAID, userEmail)
                            activityViewModel.getBulkPayments(group?.groupId)
                        }
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
                Surface(
                    Modifier
                        .padding(top = 16.dp)
                        .size(64.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_activity),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(Color.White),
                        contentScale = ContentScale.Fit
                    )
                }
                Text(
                    text = stringResource(id = R.string.no_unpaid_activities),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    textAlign = TextAlign.Center
                    )
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
                imageUrl = group?.logoUrl?: "",
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
fun SpecialLeviesHeader(
    group: Group?,
    groupViewModel: GroupViewModel,
    showAdminList: Boolean, shouldShow: (Boolean) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clickable {
                shouldShow(!showAdminList)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.special_levy),
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
fun SpecialLevies(
    group: Group?,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    navController: NavController
) {
    val levies = activityViewModel.allGroupSpecialLevies.observeAsState().value
    var filteredLevies by remember { mutableStateOf(listOf<SpecialLevy>()) }
    var selected by rememberSaveable { mutableStateOf("NOT COMPLIED") }
    LaunchedEffect(selected) {
        if (levies?.isNotEmpty() == true) {
            when (selected) {
                "COMPLIED" -> filteredLevies = levies.filter { it.confirmedPayments?.isNotEmpty() == true }
                "NOT COMPLIED" -> filteredLevies = levies.filter { it.confirmedPayments?.isEmpty() == true }
                else -> {}
            }
        }

    }

    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RadioButton(
                    selected = selected == "COMPLIED",
                    onClick = { selected = "COMPLIED" }
                )
                Text(
                    text = stringResource(id = R.string.complied),
                    fontSize = TextUnit(12.0f, TextUnitType.Sp),
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected == "NOT COMPLIED",
                    onClick = {
                        selected = "NOT COMPLIED"
                    }
                )
                Text(
                    text = stringResource(id = R.string.not_complied),
                    fontSize = TextUnit(12.0f, TextUnitType.Sp),
                )
            }
        }

        if (filteredLevies.isNotEmpty()) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                filteredLevies.forEach { levy ->
                    SpecialLevyItemGroup(levy, group, groupViewModel, activityViewModel, navController)
                }
            }
        }
    }
}

@Composable
fun SpecialLevyItemGroup(
    levy: SpecialLevy,
    group: Group?,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    navController: NavController
) {
    Surface(
        modifier = Modifier
            .clickable {
                activityViewModel.setSelectedSpecialLevy(levy)
                navController.navigate(SPECIAL_LEVY_DETAIL) {
                    launchSingleTop = true
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersListBottomSheet(
    group: Group?,
    groupViewModel: GroupViewModel,
    forumViewModel: ForumViewModel,
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

        GroupMemberList(members, group, navController, groupViewModel, forumViewModel){function(it)}
    }
}

@Composable
fun GroupMemberList(
    members: List<Member>?,
    group: Group?,
    navController: NavController,
    groupViewModel: GroupViewModel,
    forumViewModel: ForumViewModel,
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
                        MemberItem(member, group, navController, groupViewModel, forumViewModel){function(it)}
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
    forumViewModel: ForumViewModel,
    displayCallback: (showState: Boolean) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val designation = group?.memberList?.find { it.emailAddress == member.emailAddress }?.designation
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clickable {
                coroutineScope.launch {
                    groupViewModel.setSelectedMember(member)
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
            ConstraintLayout(
                Modifier.fillMaxWidth(),
            ) {
                val (profile, name, icon, admin_text) = createRefs()

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
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(name) {
                            start.linkTo(profile.end, margin = 8.dp)
                            centerVerticallyTo(parent)
                        }
                )

                if (designation == Designation.ADMIN.name) {
                    Text(
                        text = stringResource(id = R.string.admin),
                        fontSize = TextUnit(10.0f, TextUnitType.Sp),
                        color = Color(context.getColor(R.color.teal_200)),
                        modifier = Modifier.constrainAs(admin_text) {
                            end.linkTo(icon.start, margin = 4.dp)
                            centerVerticallyTo(parent)
                        }
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.message_circle_lines),
                    contentDescription = "",
                    Modifier
                        .size(24.dp)
                        .clickable {
                            coroutineScope.launch {
                                val recipientId =
                                    group?.memberList?.find { it.emailAddress == member.emailAddress }!!.membershipId
                                val senderId = groupViewModel.membershipId.value
                                forumViewModel.fetUserChats(recipientId, senderId!!)
                                forumViewModel.subScribeToDirectChat(member.emailAddress, group)
                            }
                            navController.navigate("admin_chat/${member.emailAddress}") {
                                launchSingleTop = true
                            }
                        }
                        .constrainAs(icon) {
                            end.linkTo(parent.end, margin = 8.dp)
                            centerVerticallyTo(parent)
                        },
                        tint = Color(context.getColor(R.color.purple_700))
                )
            }
            HorizontalDivider(Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
fun ActivitySwitch(activityViewModel: ActivityViewModel, userEmail: String?, switchView: (showDetails: Boolean) -> Unit) {
    val screenWidth = LocalConfiguration.current.screenWidthDp / 2
    var showPaid by rememberSaveable { mutableStateOf(true)}

    LaunchedEffect(showPaid) {
        activityViewModel.populateActivities(PAID, userEmail)
    }

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
                    activityViewModel.populateActivities(PAID, userEmail)
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
                    activityViewModel.populateActivities(UNPAID, userEmail)
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
