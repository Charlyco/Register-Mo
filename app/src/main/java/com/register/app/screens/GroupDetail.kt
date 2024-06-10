package com.register.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
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
import com.register.app.model.Event
import com.register.app.model.Group
import com.register.app.model.Member
import com.register.app.model.MembershipDto
import com.register.app.util.ImageLoader
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.GroupViewModel
import com.register.app.viewmodel.HomeViewModel

@Composable
fun GroupDetail(navController: NavController, groupViewModel: GroupViewModel, authViewModel: AuthViewModel, homeViewModel: HomeViewModel) {
    var showAllMembers by rememberSaveable{ mutableStateOf(false) }
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    LaunchedEffect(key1 = 259) {
        groupViewModel.getMembershipId(group) }
    Scaffold(
        topBar = { GroupDetailTopBar(navController, group, groupViewModel){showAllMembers = it} },
    ) {
        GroupDetailScreen(Modifier.padding(it), navController, groupViewModel, authViewModel, homeViewModel, group)
        if (showAllMembers) {
            AllMembersList(group, groupViewModel, navController) {shouldShow -> showAllMembers = shouldShow}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailTopBar(
    navController: NavController,
    group: Group?,
    groupViewModel: GroupViewModel,
    function: (show: Boolean) -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false)}
    TopAppBar(
        title = { Text(
            text = group?.groupName!!,
            Modifier.padding(start = 32.dp)
            ) },
        navigationIcon = {
            Icon(
                painter = painterResource(id = R.drawable.caret_back_circle),
                contentDescription = "",
                Modifier
                    .size(48.dp)
                    .clickable { navController.navigateUp() }
            ) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.background,
            navigationIconContentColor = MaterialTheme.colorScheme.background
        ),
        actions = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "menu",
                Modifier.clickable { isExpanded = !isExpanded },
                tint = MaterialTheme.colorScheme.background)

            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }) {
                if (groupViewModel.isUserAdmin()) {
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
                    onClick = { isExpanded = false })

                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.create_election)) },
                        onClick = { isExpanded = false })
                }
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.view_members)) },
                    onClick = {
                        isExpanded = false
                        function(true)
                    })

                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.elections)) },
                    onClick = { isExpanded = false })
            }
        }
    )
}

@Composable
fun GroupDetailScreen(
    modifier: Modifier,
    navController: NavController,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    group: Group?
) {
    var showProfileDetail by rememberSaveable { mutableStateOf(true) }
    var showAdminList by rememberSaveable { mutableStateOf(false) }
    var showRequests by rememberSaveable { mutableStateOf(false) }
    val verticalScrollState = rememberScrollState(initial = 0)
    Surface(
        Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            Modifier
                .verticalScroll(
                    state = verticalScrollState,
                    enabled = true,
                    reverseScrolling = false
                )
                .fillMaxWidth()
        ) {
          TopSection(group, groupViewModel)
            ActivityRate(group, groupViewModel, navController)
            HorizontalDivider(Modifier.padding(vertical = 4.dp, horizontal = 16.dp))
            GroupEvents(groupViewModel, homeViewModel, navController, group)
            HorizontalDivider(Modifier.padding(vertical = 4.dp, horizontal = 16.dp))
            GroupProfileHeader(group, showProfileDetail) {showProfileDetail = it}
            if (showProfileDetail) {
                GroupProfile(group, groupViewModel)
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
            GroupAdminHeader(group, showAdminList) {showAdminList = it}

            if (showAdminList) {
                GroupAdminList(group, groupViewModel, navController)
            }
            HorizontalDivider(Modifier.padding(vertical = 4.dp, horizontal = 16.dp))
            if (authViewModel.isUserAdmin()) {
                PendingMembershipRequestHeader(group, showRequests) { showRequests = it}
            }
            if (showRequests) {
                MembershipRequestList(group, groupViewModel)
            }
            HorizontalDivider(Modifier.padding(vertical = 4.dp, horizontal = 16.dp))
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActivityRate(group: Group?, groupViewModel: GroupViewModel, navController: NavController) {
    val activityRate = groupViewModel.activityRateLiveData.observeAsState().value
    val chartData = PieChartData(
        slices = listOf(
            PieChartData.Slice("Contributions made", activityRate!!, MaterialTheme.colorScheme.primary),
            PieChartData.Slice("Contributions due", (100 - activityRate), MaterialTheme.colorScheme.surface)),
        plotType = PlotType.Donut
    )

    val donutChartConfig = PieChartConfig(
        sliceLabelTextColor = MaterialTheme.colorScheme.onBackground,
        showSliceLabels = true,
        labelFontSize = TextUnit(24.0f, TextUnitType.Sp),
        labelColor = MaterialTheme.colorScheme.onBackground,
        strokeWidth = 42f,
        activeSliceAlpha = .9f,
        labelVisible = true,
        isAnimationEnable = true,
        chartPadding = 16,
        backgroundColor = MaterialTheme.colorScheme.background
    )
    ConstraintLayout(
        Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth()
    ) {
        val (idText, chart, title, detail) = createRefs()

        Text(
            text = "Your membership ID: ${groupViewModel.membershipId.observeAsState().value}",
            modifier = Modifier.constrainAs(idText) {
                start.linkTo(parent.start, margin = 16.dp)
                top.linkTo(parent.top)
            },
            fontSize = TextUnit(17.0f, TextUnitType.Sp)
        )
        Text(
            text = stringResource(id = R.string.chart_header),
            modifier = Modifier.constrainAs(title) {
                top.linkTo(idText.bottom, margin = 8.dp)
                centerHorizontallyTo(parent)
            },
            fontWeight = FontWeight.SemiBold,
            fontSize = TextUnit(17.0f, TextUnitType.Sp)
            )
        Text(
            text = "You have paid for 3/5 contribution",
            modifier = Modifier.constrainAs(detail) {
                top.linkTo(title.bottom, margin = 4.dp)
                centerHorizontallyTo(parent)
            },
            color = Color.Gray,
            fontSize = TextUnit(14.0f, TextUnitType.Sp)
            )
        DonutPieChart(
            modifier = Modifier
                .size(160.dp)
                .constrainAs(chart) {
                    centerHorizontallyTo(parent)
                    top.linkTo(detail.bottom, margin = 16.dp)
                },
            pieChartData = chartData,
            pieChartConfig = donutChartConfig)

    }
}

@Composable
fun GroupEvents(
    groupViewModel: GroupViewModel,
    homeViewModel: HomeViewModel,
    navController: NavController,
    group: Group?
) {
    val eventList = groupViewModel.activeGroupEvents.observeAsState().value
    ConstraintLayout (
        Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .fillMaxWidth(),
    ) {
        val (header, list, shoeAll) = createRefs()
        Text(
            text = stringResource(id = R.string.active_events),
            Modifier.constrainAs(header) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            },
            fontWeight = FontWeight.SemiBold,
            fontSize = TextUnit(18.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onBackground
            )
        if (eventList?.isNotEmpty() == true) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .constrainAs(list) {
                        top.linkTo(header.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                    }
                ) {
                   eventList.forEach { event ->
                       EventItem(event, groupViewModel, homeViewModel, navController)
                   }
                }
            Text(
                text = stringResource(id = R.string.show_more),
                Modifier
                    .clickable {
                        groupViewModel.getAllEventsForGroup(group?.groupName)
                        navController.navigate("events")
                    }
                    .constrainAs(shoeAll) {
                        end.linkTo(parent.end)
                        top.linkTo(list.bottom, margin = 4.dp)
                    },
                color = MaterialTheme.colorScheme.primary
                )
        }else{
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription ="",
                    Modifier
                        .size(48.dp)
                        .padding(vertical = 16.dp))
                Text(
                    text = stringResource(id = R.string.empty_event))
            }
        }
    }
}

@Composable
fun EventItem(
    event: Event,
    groupViewModel: GroupViewModel,
    homeViewModel: HomeViewModel,
    navController: NavController
) {
        Row(
            Modifier
                .clickable {
                    navController.navigate("event_detail") {
                        launchSingleTop = true
                        groupViewModel.setSelectedEvent(event)
                    }
                }
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                Modifier
                    .padding(end = 16.dp)
                    .size(36.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.extraSmall,
            ) {
               Icon(
                   imageVector = Icons.Default.EventAvailable,
                   contentDescription = "",
                   Modifier
                       .size(20.dp)
                       .padding(8.dp),
                   tint = MaterialTheme.colorScheme.primary)
            }

            Text(
                text = event.eventTitle,
                fontSize = TextUnit(16.0f, TextUnitType.Sp)
            )
    }
}

@Composable
fun MembershipRequestList(group: Group?, groupViewModel: GroupViewModel) {
    val screenHeight = LocalConfiguration.current.screenHeightDp - 64
    if (group?.pendingMemberRequests?.isNotEmpty() == true) {
        LaunchedEffect(key1 = 257) {
            groupViewModel.getIndividualMembershipRequest(group.pendingMemberRequests)
        }
        val membershipRequest = groupViewModel.pendingMemberList.observeAsState().value
        if (membershipRequest?.isNotEmpty() == true) {
            Column {
                membershipRequest.forEach {request ->
                    MembershipRequestItem(request, groupViewModel)
                }
            }
        }
    }
}

@Composable
fun MembershipRequestItem(request: Member, groupViewModel: GroupViewModel) {
    val context = LocalContext.current
    Surface(
        Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.background

    ) {
        Surface(
            Modifier.padding(vertical = 1.dp),
            border = BorderStroke(1.dp, Color.Gray),
            color = MaterialTheme.colorScheme.background
        ) {
            ConstraintLayout {
                val (pic, name, phone, email, approveBtn) = createRefs()

                Surface(
                    Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .constrainAs(pic) {
                            centerVerticallyTo(parent)
                            start.linkTo(parent.start, margin = 8.dp)
                        },
                ) {
                    ImageLoader(imageUrl = request.imageUrl?: "", context = context, height = 84, width = 84, R.drawable.event)
                }

                Text(
                    text = request.fullName,
                    Modifier.constrainAs(name) {
                        start.linkTo(pic.end, margin = 8.dp)
                        top.linkTo(parent.top, margin = 8.dp)
                    })

                Text(
                    text = "Phone: ${request.phoneNumber}",
                    Modifier.constrainAs(phone) {
                        start.linkTo(pic.end, margin = 8.dp)
                        top.linkTo(name.bottom, margin = 8.dp)
                    })

                Text(
                    text = "Email: ${request.emailAddress}",
                    Modifier.constrainAs(email) {
                        start.linkTo(pic.end, margin = 8.dp)
                        top.linkTo(phone.bottom, margin = 8.dp)
                    })

                Button(
                    onClick = {
                        groupViewModel.approveMembershipRequest(request.emailAddress) },
                    Modifier
                        .padding(horizontal = 4.dp)
                        .constrainAs(approveBtn) {
                            end.linkTo(parent.end, margin = 8.dp)
                            top.linkTo(email.bottom, margin = 8.dp)
                            bottom.linkTo(parent.bottom, margin = 4.dp)
                        }
                ) {
                    Text(
                        text = stringResource(id = R.string.approve),
                        Modifier.padding(end = 4.dp)
                    )
                    Icon(imageVector = Icons.Default.Check, contentDescription = "")
                }
            }
        }
    }
}

@Composable
fun PendingMembershipRequestHeader(
    group: Group?,
    showRequests: Boolean,
    callback: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clickable { callback(!showRequests) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.membership_request),
            fontSize = TextUnit(18.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold
        )
        if (showRequests) {
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
fun GroupAdminHeader(group: Group?, showAdminList: Boolean, shouldShow: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clickable { shouldShow(!showAdminList) },
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
                    .clickable { shouldShow(true) }
            )
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
    Surface(
        Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomStart = 32.dp,
                    bottomEnd = 32.dp
                )
            ),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = dimensionResource(id = R.dimen.low_elevation)
    ) {
        ConstraintLayout(
            Modifier.fillMaxSize()
        ) {
            val (bgImage, logo, header, description, id) = createRefs()

            Surface(
                Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .constrainAs(logo) {
                        top.linkTo(parent.top, margin = 64.dp)
                        centerHorizontallyTo(parent)
                    },
                color = MaterialTheme.colorScheme.background
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
                text = group?.groupDescription!!,
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .constrainAs(header) {
                        top.linkTo(logo.bottom, margin = 8.dp)
                        centerHorizontallyTo(parent)
                    },
                color = MaterialTheme.colorScheme.background,
                textAlign = TextAlign.Center
            )
        }
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
                text = group?.email?: "",
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
                text = group?.phone?: "",
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
fun GroupAdminList(group: Group?, groupViewModel: GroupViewModel, navController: NavController) {
    val itemWidth = (LocalConfiguration.current.screenWidthDp / 2) - 36
    if (group?.adminList?.isNotEmpty() == true) {
        LaunchedEffect(key1 = 256) {
            groupViewModel.getIndividualAdminDetail()
        }
        val adminList = groupViewModel.groupAdminList.observeAsState().value
        if (adminList?.isNotEmpty() == true) {
            LazyRow(
                Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .height(204.dp),
                state = rememberLazyListState(),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(adminList) { admin ->
                    AdminItem(admin)
                }
                if (groupViewModel.isUserAdmin()) {
                    item{
                        Surface(
                            Modifier
                                .height(200.dp)
                                .width(itemWidth.dp)
                                .padding(horizontal = 8.dp)
                                .clickable { TODO("to be implemented") },
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

@Composable
fun AdminItem(admin: Member) {
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
                text = admin.memberPost,
                Modifier.constrainAs(office) {
                    centerHorizontallyTo(parent)
                    top.linkTo(parent.top, margin = 8.dp) },
                fontSize = TextUnit(17.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
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
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

//                Text(
//                    text = admin.emailAddress,
//                    Modifier
//                        .padding(horizontal = 2.dp)
//                        .constrainAs(email) {
//                        centerHorizontallyTo(parent)
//                        top.linkTo(name.bottom, margin = 8.dp)
//                    },
//                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
//                    color = Color.DarkGray
//                )

            Text(
                text = admin.phoneNumber,
                Modifier.constrainAs(phone) {
                    centerHorizontallyTo(parent)
                    top.linkTo(name.bottom, margin = 8.dp) },
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = Color.DarkGray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllMembersList(group: Group?, groupViewModel: GroupViewModel, navController: NavController, function: (show: Boolean) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp
    ModalBottomSheet(
        onDismissRequest = { function(false) },
        modifier = Modifier.height(screenHeight.dp),
        sheetState = sheetState,
        sheetMaxWidth = screenWidth.dp,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        if (group?.memberList?.isNotEmpty() == true) {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(group.memberList) { member ->
                    MemberItem(member, navController, groupViewModel)
                }
            }
        }
    }
}

@Composable
fun MemberItem(member: MembershipDto, navController: NavController, groupViewModel: GroupViewModel) {
    val memberDetail = groupViewModel.getMemberDetails(member.email)
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clickable {  },
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
                        imageUrl = memberDetail.imageUrl ?: "",
                        context = context,
                        height = 42,
                        width = 42,
                        placeHolder = R.drawable.placeholder
                    )
                }

                Text(
                    text = memberDetail.fullName,
                    fontSize = TextUnit(16.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            HorizontalDivider(Modifier.padding(2.dp))
        }
    }
}

