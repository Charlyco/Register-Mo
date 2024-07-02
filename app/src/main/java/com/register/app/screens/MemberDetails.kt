package com.register.app.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.register.app.model.Member
import com.register.app.model.MembershipDto
import com.register.app.util.CircularIndicator
import com.register.app.util.GenericTopBar
import com.register.app.util.ImageLoader
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch

@Composable
fun MemberDetails(groupViewModel: GroupViewModel, authViewModel: AuthViewModel, navController: NavController) {
    val member = groupViewModel.selectedMember.observeAsState().value
    val memberDetail = groupViewModel.groupMemberLiveData.observeAsState().value
    Scaffold(
        topBar = { GenericTopBar(
            title = memberDetail?.userName?: "",
            navController = navController,
            navRoute = "group_detail") }
    ) {
        MemberDetailsUi(Modifier.padding(it), groupViewModel, authViewModel, navController, member, memberDetail)
    }
}

@Composable
fun MemberDetailsUi(
    modifier: Modifier,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    navController: NavController,
    member: MembershipDto?,
    memberDetail: Member?
) {
    val scrollState = rememberScrollState(initial = 0)
    val context = LocalContext.current
    val isLoading = groupViewModel.loadingState.observeAsState().value

    Surface(
        Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {
        ConstraintLayout(
            Modifier
                .fillMaxWidth()
                .padding(top = 64.dp)
        ) {
            val (pic, info, progress) = createRefs()

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
               MemberInfo(memberDetail, member, authViewModel, groupViewModel, navController)
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
    navController: NavController
) {
    val context = LocalContext.current
    ConstraintLayout(
        Modifier
            .fillMaxSize()
    ) {
        val (name, phone, status, id, adminSection) = createRefs()

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
            text = memberDetail?.phoneNumber!!,
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
            text = member?.memberStatus!!,
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

        if (authViewModel.isUserAdmin()) {
            Surface(
                Modifier
                    .fillMaxWidth()
                    .constrainAs(adminSection) {
                        top.linkTo(status.bottom, margin = 16.dp)
                    },
                color = MaterialTheme.colorScheme.background
            ) {
                AdminMemberActions(memberDetail, member, groupViewModel, authViewModel, navController)
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
    navController: NavController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isUnblockEnabled = (member?.memberStatus == MemberStatus.SUSPENDED.name)
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
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
}