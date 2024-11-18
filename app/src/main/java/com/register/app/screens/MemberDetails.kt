package com.register.app.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
import com.register.app.dto.ChangeMemberStatusDto
import com.register.app.dto.Payment
import com.register.app.dto.RemoveMemberModel
import com.register.app.dto.SpecialLevy
import com.register.app.enums.Designation
import com.register.app.enums.MemberStatus
import com.register.app.enums.PaymentMethod
import com.register.app.model.Event
import com.register.app.model.Member
import com.register.app.model.MembershipDto
import com.register.app.util.ASSIGN_LEVY
import com.register.app.util.CircularIndicator
import com.register.app.util.ImageLoader
import com.register.app.util.MemberActivitySwitch
import com.register.app.util.PAY_SPECIAL_LEVY
import com.register.app.util.SPECIAL_LEVY_DETAIL
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch

@Composable
fun MemberDetails(
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    activityViewModel: ActivityViewModel,
    navController: NavController
) {
    val member = groupViewModel.selectedMember.observeAsState().value
    val memberDetail = groupViewModel.groupMemberLiveData.observeAsState().value
    var showPaymentRecord by rememberSaveable { mutableStateOf(false) }
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    LaunchedEffect(member) {
        activityViewModel.getAllSpecialLeviesForGroup(group?.groupId!!, member?.emailAddress!!)
    }
    Scaffold{
        MemberDetailsUi(Modifier.padding(it), groupViewModel, authViewModel, navController, member, memberDetail) { showPaymentRecord = it }
        if (showPaymentRecord) {
            PaymentRecord(groupViewModel, activityViewModel, member, navController) {showRecord ->
                showPaymentRecord = showRecord
            }
        }
    }
}

@Composable
fun MemberDetailsUi(
    modifier: Modifier,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    navController: NavController,
    member: MembershipDto?,
    memberDetail: Member?,
    callback: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val isLoading = groupViewModel.loadingState.observeAsState().value
    val screenHeight = LocalConfiguration.current.screenHeightDp

    Surface(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState(initial = 0)),
        color = MaterialTheme.colorScheme.primary
    ) {
        ConstraintLayout(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            val (navBtn, pic, info, progress) = createRefs()
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "back",
                tint = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .constrainAs(navBtn) {
                        top.linkTo(parent.top, margin = 16.dp)
                        start.linkTo(parent.start, margin = 16.dp)
                    }
                    .clickable {
                        navController.navigateUp()
                    })
            Surface(
                Modifier
                    .height((screenHeight - 192).dp)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .constrainAs(info) {
                        //top.linkTo(pic.bottom, margin = (-40).dp)
                        bottom.linkTo(parent.bottom)
                        centerHorizontallyTo(parent)
                    },
                color = MaterialTheme.colorScheme.background
            ){
               MemberInfo(memberDetail, member, authViewModel, groupViewModel, navController) {callback(it)}
            }

            Surface(
                Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .constrainAs(pic) {
                        bottom.linkTo(info.top, margin = (-40).dp)
                        centerHorizontallyTo(parent)
                    }
            ) {
                ImageLoader(
                    imageUrl = memberDetail?.imageUrl ?: "",
                    context = context,
                    height = 160,
                    width = 160,
                    placeHolder = R.drawable.placeholder
                )
            }
            if (isLoading == true) {
                Surface(
                    Modifier.constrainAs(progress) {
                        centerHorizontallyTo(parent)
                        centerVerticallyTo(parent)
                    },
                    color = Color.Transparent
                ) {
                    CircularIndicator()
                }
            }
        }
    }
}

@Composable
fun MemberInfo(
    memberDetail: Member?,
    member: MembershipDto?,
    authViewModel: AuthViewModel,
    groupViewModel: GroupViewModel,
    navController: NavController,
    callback: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val isAdmin = groupViewModel.isUserAdminLiveData.observeAsState().value
    ConstraintLayout(
        Modifier
            .fillMaxSize()
    ) {
        val (name, phone, status, id, activityRate, adminSection) = createRefs()

        Text(
            text = "${memberDetail?.fullName}(${memberDetail?.userName})",
            modifier = Modifier
                .padding(start = 16.dp)
                .constrainAs(name) {
                    top.linkTo(parent.top, margin = 42.dp)
                    centerHorizontallyTo(parent)
                },
            fontSize = TextUnit(18.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold)

        Text(
            text = memberDetail?.phoneNumber!! ,
            modifier = Modifier
                .padding(start = 16.dp)
                .constrainAs(phone) {
                    top.linkTo(name.bottom, margin = 4.dp)
                    centerHorizontallyTo(parent)
                },
            fontSize = TextUnit(14.0f, TextUnitType.Sp))

        Text(
            text = "Membership Id: ${member?.membershipId}",
            modifier = Modifier
                .padding(start = 16.dp)
                .constrainAs(id) {
                    top.linkTo(phone.bottom, margin = 4.dp)
                    centerHorizontallyTo(parent)
                },
            fontSize = TextUnit(14.0f, TextUnitType.Sp))

        Text(
            text = "Status: ${member?.memberStatus!!}",
            modifier = Modifier
                .padding(start = 16.dp)
                .constrainAs(status) {
                    top.linkTo(id.bottom, margin = 4.dp)
                },
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            color = if (member.memberStatus == MemberStatus.ACTIVE.name) {
                Color(context.getColor(R.color.teal_200))
            }else {
                Color.Red
            }
        )
        if (isAdmin == true) {
            Surface(
                Modifier
                    .fillMaxWidth()
                    .constrainAs(activityRate) {
                        top.linkTo(status.bottom, margin = 16.dp)
                        centerHorizontallyTo(parent)
                    },
                color = Color.Transparent
            ) {
                MemberActivityRate(groupViewModel = groupViewModel)
            }
            Surface(
                Modifier
                    .fillMaxWidth()
                    .constrainAs(adminSection) {
                        top.linkTo(activityRate.bottom, margin = 16.dp)
                    },
                color = MaterialTheme.colorScheme.background
            ) {
                AdminMemberActions(memberDetail, member, groupViewModel, authViewModel, navController) { callback(it) }
            }
        }
    }
}

@Composable
fun AdminMemberActions(
    memberDetail: Member,
    member: MembershipDto?,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    navController: NavController,
    callback: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isUnblockEnabled = (member?.memberStatus == MemberStatus.SUSPENDED.name)
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    var showChangeRoleDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                Modifier
                    .clickable {
                        if (isUnblockEnabled) {
                            Toast.makeText(context, "This member is already on ${memberDetail.status}", Toast.LENGTH_LONG).show()
                        } else{
                            coroutineScope.launch {
                                val response = groupViewModel.changeMemberStatus(
                                    member?.membershipId!!, ChangeMemberStatusDto(MemberStatus.SUSPENDED.name, group?.groupId))
                                if (response.status) {
                                    Toast.makeText(context, "Member suspended", Toast.LENGTH_SHORT).show()
                                }else {
                                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface {
                    if (isUnblockEnabled) {
                        Icon(
                            painter = painterResource(id = R.drawable.suspend_user),
                            contentDescription = "suspend user",
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray)
                    }else {
                        Image(
                            painter = painterResource(id = R.drawable.suspend_user),
                            contentDescription = "suspend user",
                            modifier = Modifier.size(48.dp))
                    }
                }

                Text(
                    text = stringResource(id = R.string.suspend_user),
                    color = if (isUnblockEnabled) Color.Gray else MaterialTheme.colorScheme.onBackground)
            }

            Column(
                Modifier
                    .clickable {
                        if (isUnblockEnabled) {
                            Toast.makeText(context, "This member is already on ${memberDetail.status}", Toast.LENGTH_LONG).show()
                        } else{
                            coroutineScope.launch {
                                val response = groupViewModel.expelMember(RemoveMemberModel(member?.membershipId!!,
                                    memberDetail.emailAddress, group?.groupId!!))
                                Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                                if (response.status) {
                                    navController.navigateUp()
                                }
                            }
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface {
                    if (isUnblockEnabled) {
                        Icon(
                            painter = painterResource(id = R.drawable.expel),
                            contentDescription = "expel user",
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray)
                    }else {
                        Image(
                            painter = painterResource(id = R.drawable.expel),
                            contentDescription = "suspend user",
                            modifier = Modifier.size(48.dp))
                    }
                }

                Text(
                    text = stringResource(id = R.string.expel_member),
                    color = if (isUnblockEnabled) Color.Gray else MaterialTheme.colorScheme.onBackground)
            }

            Column(
                Modifier
                    .clickable {
                        if (isUnblockEnabled) {
                            Toast.makeText(context, "This member is currently not eligible", Toast.LENGTH_LONG).show()
                        } else{
                            showChangeRoleDialog = true
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface {
                    if (isUnblockEnabled) {
                        Icon(
                            painter = painterResource(id = R.drawable.change_role),
                            contentDescription = "change role",
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray)
                    }else {
                        Image(
                            painter = painterResource(id = R.drawable.change_role),
                            contentDescription = "change role",
                            modifier = Modifier.size(48.dp))
                    }
                }

                Text(
                    text = stringResource(id = R.string.change_role),
                    color = if (isUnblockEnabled) Color.Gray else MaterialTheme.colorScheme.onBackground
                )
            }

            Column(
                Modifier
                    .clickable {
                        if (!isUnblockEnabled) {
                            Toast.makeText(context, "This member is currently active", Toast.LENGTH_LONG).show()
                        } else{
                            coroutineScope.launch {
                                val response = groupViewModel.changeMemberStatus(
                                    member?.membershipId!!, ChangeMemberStatusDto(MemberStatus.ACTIVE.name, group?.groupId))
                                if (response.status) {
                                    Toast.makeText(context, "Member recalled", Toast.LENGTH_SHORT).show()
                                }else {
                                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface {
                    if (!isUnblockEnabled) {
                        Icon(
                            painter = painterResource(id = R.drawable.recall),
                            contentDescription = "suspend user",
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray)
                    }else {
                        Image(
                            painter = painterResource(id = R.drawable.recall),
                            contentDescription = "suspend user",
                            modifier = Modifier.size(48.dp))
                    }
                }

                Text(
                    text = stringResource(id = R.string.recall_member),
                    color = if (!isUnblockEnabled)  Color.Gray else MaterialTheme.colorScheme.onBackground
                )
            }

        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { callback(true) },
                modifier = Modifier
                    .width(180.dp)
                    .padding(horizontal = 8.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color(context.getColor(R.color.background_color))),
            ) {
                Text(text = stringResource(id = R.string.payment_record))
            }

            Button(
                onClick = {
                    navController.navigate(ASSIGN_LEVY) {
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .width(180.dp)
                    .padding(horizontal = 8.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color(context.getColor(R.color.background_color))),
            ) {
                Text(text = stringResource(id = R.string.assign_levy))
            }
        }
    }

    if (showChangeRoleDialog) {
        ChangeRoleDialog(groupViewModel, navController, member, memberDetail) { showChangeRoleDialog = it }
    }
}

@Composable
fun ChangeRoleDialog(
    groupViewModel: GroupViewModel,
    navController: NavController,
    membership: MembershipDto?,
    member: Member,
    onDismiss: (Boolean) -> Unit
) {
    val isAdmin = membership?.designation == Designation.ADMIN.name
    var isChecked by rememberSaveable { mutableStateOf(false) }
    var newOffice by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val officeList = listOf("Select Office",
        "PRESIDENT",
        "SECRETARY",
        "TREASURER",
        "FINANCIAL_SECRETARY",
        "ADMIN",
        "LEADER",
        "ORGANIZER",
        "CHIEF")

    Dialog(
        onDismissRequest = { onDismiss(false) },
        ) {
        Surface(
            Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.change_role),
                    fontSize = TextUnit(16.0f, TextUnitType.Sp),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp)
                )
                if (isAdmin) {
                    Text(text = stringResource(id = R.string.removeAdmin))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = {isChecked = !isChecked})
                            Text(
                                text = "Are you sure you want to remove ${member.fullName} as Admin?",
                                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                            )
                    }
                }else {
                    SelectOffice(officeList = officeList) { newOffice = it }
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (isChecked) {
                                val response = groupViewModel.removeAdmin(membership, "MEMBER")
                                Toast.makeText(context, "Action ${response.message}", Toast.LENGTH_LONG).show()
                                if (response.status) {
                                    navController.navigateUp()
                                }
                            }else {
                                if (newOffice.isNotEmpty()) {
                                    val response = groupViewModel.makeAdmin(membership, newOffice)
                                    Toast.makeText(context, "Action ${response.message}", Toast.LENGTH_LONG).show()
                                    if (response.status) {
                                        navController.navigateUp()
                                    }
                                }else {
                                    Toast.makeText(context, "Please select an office", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 16.dp),
                    shape = MaterialTheme.shapes.medium
                    ) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentRecord(
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    member: MembershipDto?,
    navController: NavController,
    callback: (Boolean) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp
    ModalBottomSheet(
        onDismissRequest = { callback(false) },
        modifier = Modifier
            .verticalScroll(rememberScrollState(initial = 0))
            .height(screenHeight.dp),
        sheetState = sheetState,
        sheetMaxWidth = screenWidth.dp,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        MemberActivities(groupViewModel, activityViewModel, navController)
    }
}

@Composable
fun MemberActivities(
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    navController: NavController
) {
    var showPaid by rememberSaveable { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MemberActivitySwitch { showPaid = it }
        if (showPaid) {
            PaidActivities(groupViewModel, activityViewModel, navController)
        }else {
            UnpaidActivities(groupViewModel, activityViewModel, navController)
        }
    }
}

@Composable
fun UnpaidActivities(
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    navController: NavController
) {
    val eventList = groupViewModel.memberUnpaidActivities.observeAsState().value
    val unpaidSpecialLevies = activityViewModel.memberUnpaidSpecialLevyList.observeAsState().value
    ConstraintLayout (
        Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .fillMaxWidth(),
    ) {
        val (header, list) = createRefs()
        if (eventList?.isNotEmpty() == true || unpaidSpecialLevies?.isNotEmpty() == true) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .constrainAs(list) {
                        top.linkTo(header.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                    }
            ) {
                if( eventList?.isNotEmpty() == true ) {  // show regular events
                    eventList.forEach { event ->
                        ActivityItem(event, groupViewModel, activityViewModel, navController, false)
                    }
                }
                if (unpaidSpecialLevies?.isNotEmpty() == true) {  // show special levies
                    unpaidSpecialLevies.forEach { levy ->
                        LevyItem(levy, navController, activityViewModel, groupViewModel, false)
                    }
                }
            }
        } else{
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription ="",
                    Modifier
                        .size(56.dp),
                    tint = MaterialTheme.colorScheme.secondary)
                        //.padding(vertical = 16.dp))
                Text(
                    text = stringResource(id = R.string.no_outstanding_activities))
            }
        }
    }
}

@Composable
fun LevyItem(
    levy: SpecialLevy,
    navController: NavController,
    activityViewModel: ActivityViewModel,
    groupViewModel: GroupViewModel,
    isPaid: Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    var showMarkAsPaidDialog by rememberSaveable { mutableStateOf(false) }
    val selectedMember = groupViewModel.selectedMember.observeAsState().value
    Surface(
        modifier = Modifier
            .clickable {
                if (isPaid) {
                    showMarkAsPaidDialog = true
                }else{
                    activityViewModel.setSelectedSpecialLevy(levy)
                    navController.navigate(SPECIAL_LEVY_DETAIL)
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
        if (showMarkAsPaidDialog) {
            MarkLevyAsPaidDialog { showMarkAsPaidDialog = it}
        }
    }
}

@Composable
fun MarkLevyAsPaidDialog(
    callback: (Boolean) -> Unit
) {
    Dialog(
        onDismissRequest = { callback(false) }
    ) {
        Surface(
            Modifier
                .height(120.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            Text(
                text = stringResource(id = R.string.alread_paid),
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = TextAlign.Center,
                fontSize = TextUnit(14.0f, TextUnitType.Sp)
            )
        }
    }
}

@Composable
fun ActivityItem(
    event: Event,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    navController: NavController,
    isPaid: Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    val selectedMember = groupViewModel.selectedMember.observeAsState().value
    var showMarkAsPaid by rememberSaveable { mutableStateOf(false) }

    Surface(
        Modifier
            .clickable {
                showMarkAsPaid = true
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
                text = event.eventTitle,
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.constrainAs(eventTitle) {
                    top.linkTo(parent.top, margin = 4.dp)
                    start.linkTo(parent.start, margin = 8.dp)
                },
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = event.groupName!!,
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                modifier = Modifier.constrainAs(groupName) {
                    top.linkTo(eventTitle.bottom, margin = 4.dp)
                    bottom.linkTo(levyAmount.top, margin = 4.dp)
                    start.linkTo(parent.start, margin = 8.dp)
                }
            )

            Text(
                text = "Levy: ${event.levyAmount.toString()}",
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
    if (showMarkAsPaid) {
        MarActivityAsPaidDialog(event, activityViewModel, groupViewModel, selectedMember, isPaid) { showMarkAsPaid = it }
    }
}

@Composable
fun MarActivityAsPaidDialog(
    event: Event,
    activityViewModel: ActivityViewModel,
    groupViewModel: GroupViewModel,
    selectedMember: MembershipDto?,
    isPaid: Boolean,
    callback: (Boolean) -> Unit
) {
    var amountPaid by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    val member = groupViewModel.groupMemberLiveData.observeAsState().value
    Dialog(
        onDismissRequest = { callback(false) }
    ) {
        Surface(
            Modifier
                .height(120.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            if (isPaid) {
                Text(
                    text = stringResource(id = R.string.alread_paid),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    textAlign = TextAlign.Center,
                    fontSize = TextUnit(14.0f, TextUnitType.Sp)
                )
            }else {
                Column(
                    Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.mark_paid),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        textAlign = TextAlign.Center,
                        fontSize = TextUnit(14.0f, TextUnitType.Sp),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = MaterialTheme.shapes.medium,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary)
                    ) {
                        TextField(
                            value = amountPaid,
                            onValueChange = { amountPaid = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dimensionResource(id = R.dimen.text_field_height)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.background,
                                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            placeholder = { Text(text = stringResource(id = R.string.amount_paid)) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            )
                        )
                    }

                    Button(
                        onClick = {
                            val payment = Payment(
                                "",
                                selectedMember?.membershipId!!,
                                selectedMember.emailAddress,
                                member?.fullName!!,
                                event.eventTitle,
                                event.eventId,
                                PaymentMethod.CARD_PAYMENT.name,
                                group?.groupName!!,
                                group.groupId,
                                amountPaid.toDouble()
                            )
                            coroutineScope.launch {
                                val response = activityViewModel.confirmPayment(
                                    payment,
                                    amountPaid.toDouble(),
                                    group.groupId
                                )
                                callback(false)
                                Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color(context.getColor(R.color.background_color))
                        ),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                }
            }
        }
    }
}

@Composable
fun PaidActivities(
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    navController: NavController
) {
    val eventList = groupViewModel.memberPaidActivities.observeAsState().value
    val paidSpecialLevies = activityViewModel.memberPaidSpecialLevyList.observeAsState().value
    ConstraintLayout (
        Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .fillMaxWidth(),
    ) {
        val (header, list, shoeAll) = createRefs()
        if (eventList?.isNotEmpty() == true || paidSpecialLevies?.isNotEmpty() == true) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .constrainAs(list) {
                        top.linkTo(header.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                    }
            ) {
                if( eventList?.isNotEmpty() == true ) {  // show regular events
                    eventList.forEach { event ->
                        ActivityItem(
                            event,
                            groupViewModel,
                            activityViewModel,
                            navController,
                            true
                        )
                    }
                }
                if (paidSpecialLevies?.isNotEmpty() == true) { // show special levies
                    paidSpecialLevies.forEach { levy ->
                        LevyItem(levy, navController, activityViewModel, groupViewModel, true)
                    }
                }
            }
        }else{
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription ="",
                    Modifier
                        .size(56.dp),
                    tint = MaterialTheme.colorScheme.secondary)
                Text(
                    text = stringResource(id = R.string.no_paid_activities))
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MemberActivityRate(groupViewModel: GroupViewModel) {
    val context = LocalContext.current
    val paymentRate = groupViewModel.memberPaymentRateLiveData.observeAsState().value
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
        isAnimationEnable = true,
        chartPadding = 16,
        backgroundColor = MaterialTheme.colorScheme.background
    )
    ConstraintLayout(
        Modifier
            .padding(top = 4.dp, bottom = 8.dp)
            .fillMaxWidth()
    ) {
        val (chart, title, detail, legend, percentage) = createRefs()

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

        Text(
            text = "$percentPaid%",
            modifier = Modifier
                .constrainAs(percentage) {
                    centerHorizontallyTo(chart)
                    centerVerticallyTo(chart)
                },
            fontSize = TextUnit(18.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold
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
