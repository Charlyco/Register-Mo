package com.register.app.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.dto.ChangeMemberStatusDto
import com.register.app.dto.RemoveMemberModel
import com.register.app.enums.MemberStatus
import com.register.app.model.Event
import com.register.app.model.Member
import com.register.app.model.MembershipDto
import com.register.app.util.CircularIndicator
import com.register.app.util.GenericTopBar
import com.register.app.util.ImageLoader
import com.register.app.util.PAID
import com.register.app.util.UNPAID
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch
import retrofit2.Callback

@Composable
fun MemberDetails(groupViewModel: GroupViewModel, authViewModel: AuthViewModel,activityViewModel: ActivityViewModel, navController: NavController) {
    val member = groupViewModel.selectedMember.observeAsState().value
    val memberDetail = groupViewModel.groupMemberLiveData.observeAsState().value
    var showPaymentRecord by rememberSaveable { mutableStateOf(false) }
    Scaffold{
        MemberDetailsUi(Modifier.padding(it), groupViewModel, authViewModel, navController, member, memberDetail) { showPaymentRecord = it }
        if (showPaymentRecord) {
            PaymentRecord(groupViewModel, activityViewModel, member, navController) {showPaymentRecord = it}
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

    Surface(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState(initial = 0)),
        color = MaterialTheme.colorScheme.primary
    ) {
        ConstraintLayout(
            Modifier
                .fillMaxWidth()
        ) {
            val (navBtn, pic, info, progress ) = createRefs()
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
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .constrainAs(info) {
                        top.linkTo(pic.bottom, margin = (-40).dp)
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
                        top.linkTo(parent.top, margin = 32.dp)
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
            text = "Phone: ${memberDetail?.phoneNumber!!}" ,
            modifier = Modifier
                .padding(start = 16.dp)
                .constrainAs(phone) {
                    top.linkTo(name.bottom, margin = 4.dp)
                },
            fontSize = TextUnit(14.0f, TextUnitType.Sp))

        Text(
            text = "Membership Id: ${member?.membershipId}",
            modifier = Modifier
                .padding(start = 16.dp)
                .constrainAs(id) {
                    top.linkTo(phone.bottom, margin = 4.dp)
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

        Surface(
            Modifier
                .fillMaxWidth()
                .constrainAs(activityRate) {
                    top.linkTo(status.bottom, margin = 16.dp)
                    centerHorizontallyTo(parent)
                },
            color = Color.Transparent
        ) {
            ActivityRate(groupViewModel = groupViewModel)
        }

        if (authViewModel.isUserAdmin()) {
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
                                if (response.status) Toast.makeText(context, "Member suspended", Toast.LENGTH_SHORT).show()
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
                                val response = groupViewModel.expelMember(RemoveMemberModel(member?.membershipId!!, memberDetail.emailAddress, group?.groupId!!))
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
                            contentDescription = "suspend user",
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
                            //
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
                                if (response.status) Toast.makeText(context, "Member suspended", Toast.LENGTH_SHORT).show()
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
        Button(
            onClick = { callback(true) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        ) {
            Text(text = stringResource(id = R.string.payment_record))
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
        modifier = Modifier.height(screenHeight.dp),
        sheetState = sheetState,
        sheetMaxWidth = screenWidth.dp,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Column {
            PaidActivities(groupViewModel, activityViewModel, navController)
            HorizontalDivider(Modifier.padding(vertical = 4.dp, horizontal = 16.dp))
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
    val eventList = groupViewModel.unpaidActivities.observeAsState().value
    ConstraintLayout (
        Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .fillMaxWidth(),
    ) {
        val (header, list, shoeAll) = createRefs()
        Text(
            text = stringResource(id = R.string.unpaid_activities),
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
                    ActivityItem(event, groupViewModel, activityViewModel, navController)
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
                        .padding(vertical = 16.dp))
                Text(
                    text = stringResource(id = R.string.no_outstanding_activities))
            }
        }
    }
}

@Composable
fun ActivityItem(
    event: Event,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    Row(
        Modifier
            .clickable {
                navController.navigate("event_detail") {
                    launchSingleTop = true
                    coroutineScope.launch { activityViewModel.setSelectedEvent(event) }
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
                tint = MaterialTheme.colorScheme.secondary)
        }

        Text(
            text = event.eventTitle,
            fontSize = TextUnit(16.0f, TextUnitType.Sp)
        )
    }
}

@Composable
fun PaidActivities(
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    navController: NavController
) {
    val eventList = groupViewModel.paidActivities.observeAsState().value
    ConstraintLayout (
        Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .fillMaxWidth(),
    ) {
        val (header, list, shoeAll) = createRefs()
        Text(
            text = stringResource(id = R.string.paid_activities),
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
                    ActivityItem(event, groupViewModel, activityViewModel, navController)
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
                        .padding(vertical = 16.dp))
                Text(
                    text = stringResource(id = R.string.no_paid_activities))
            }
        }
    }
}
