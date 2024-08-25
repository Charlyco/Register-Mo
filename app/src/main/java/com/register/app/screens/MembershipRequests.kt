package com.register.app.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import com.register.app.R
import com.register.app.model.Group
import com.register.app.model.MembershipRequest
import com.register.app.util.GenericTopBar
import com.register.app.util.ImageLoader
import com.register.app.util.Utils
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch

@Composable
fun MembershipRequests(
    navController: NavController,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel
) {
    Scaffold(
        topBar = { GenericTopBar(title = stringResource(id = R.string.membership_requests),
            navController = navController, navRoute = "group_detail") },
        containerColor = MaterialTheme.colorScheme.background
    ) {
        val group = groupViewModel.groupDetailLiveData.observeAsState().value
        MembershipRequestList(Modifier.padding(it) ,group, groupViewModel)
    }

}

@Composable
fun MembershipRequestList(modifier: Modifier, group: Group?, groupViewModel: GroupViewModel) {
    val screenHeight = LocalConfiguration.current.screenHeightDp - 64
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (group?.pendingMemberRequests?.isNotEmpty() == true) {
            group.pendingMemberRequests.forEach {request ->
                MembershipRequestItem(request, groupViewModel)
            }
        }else {
            Image(
                painter = painterResource(id = R.drawable.groups),
                contentDescription ="",
                modifier = Modifier.size(72.dp)
            )

            Text(
                text = stringResource(id = R.string.no_requests),
                fontSize = TextUnit(20.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
                )
        }
    }

}

@Composable
fun MembershipRequestItem(
    request: MembershipRequest,
    groupViewModel: GroupViewModel,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showDetail by rememberSaveable { mutableStateOf(false) }
    Surface(
        Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .clickable {
                coroutineScope.launch {
                    groupViewModel.getIndividualMembershipRequest(request.memberEmail)
                }
            },
        color = MaterialTheme.colorScheme.background

    ) {
        Surface(
            Modifier.padding(vertical = 1.dp),
            border = BorderStroke(1.dp, Color.Gray),
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.small
        ) {
            ConstraintLayout {
                val (name, time, email, showDetails, approveBtn, requestDetail) = createRefs()

                Text(
                    text = request.memberFullName?: "",
                    Modifier.constrainAs(name) {
                        start.linkTo(parent.start, margin = 8.dp)
                        top.linkTo(parent.top, margin = 8.dp)
                    })

                Text(
                    text = "Email: ${request.memberEmail}",
                    Modifier.constrainAs(email) {
                        start.linkTo(parent.start, margin = 8.dp)
                        top.linkTo(name.bottom, margin = 8.dp)
                    })

                Text(
                    text = "Request sent on: ${Utils.formatToDDMMYYYY(request.timeOfRequest)}",
                    Modifier.constrainAs(time) {
                        start.linkTo(parent.start, margin = 8.dp)
                        top.linkTo(email.bottom, margin = 8.dp)
                    })

                Button(
                    onClick = {
                        coroutineScope.launch {
                            groupViewModel.getIndividualMembershipRequest(request.memberEmail)
                            showDetail = true
                        }
                    },
                    Modifier
                        .padding(bottom = 8.dp)
                        .constrainAs(showDetails) {
                            start.linkTo(parent.start, margin = 8.dp)
                            top.linkTo(time.bottom, margin = 8.dp)
                        },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.show_detail),
                        Modifier.padding(end = 4.dp)
                    )
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val response = groupViewModel.approveMembershipRequest(request)
                            if (response.status) {
                                Toast.makeText(context, "Request approved", Toast.LENGTH_SHORT).show() }
                        }
                    },
                    Modifier
                        .padding(bottom = 8.dp)
                        .constrainAs(approveBtn) {
                            end.linkTo(parent.end, margin = 8.dp)
                            top.linkTo(time.bottom, margin = 8.dp)
                        }
                ) {
                    Text(
                        text = stringResource(id = R.string.approve),
                        Modifier.padding(end = 4.dp)
                    )
                    Icon(imageVector = Icons.Default.Check, contentDescription = "")
                }

                if (showDetail) {
                    MembershipRequestDetail(groupViewModel, request) { shouldShow ->
                        showDetail = shouldShow
                    }
                }
            }
        }
    }
}

@Composable
fun MembershipRequestDetail(
    groupViewModel: GroupViewModel,
    selectedRequest: MembershipRequest?,
    showDialog: (show: Boolean) -> Unit
) {
    val requestDetail = groupViewModel.pendingMemberLiveData.observeAsState().value
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Dialog(
        onDismissRequest = { showDialog(false) },
    ) {
        Surface(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImageLoader(
                    imageUrl = requestDetail?.imageUrl?: "",
                    context = context,
                    height = 120,
                    width = 120,
                    placeHolder = R.drawable.placeholder
                )

                Text(
                    text = requestDetail?.fullName!!,
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = TextUnit(14.0f, TextUnitType.Sp)
                )

                Text(
                    text = requestDetail.phoneNumber,
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = TextUnit(14.0f, TextUnitType.Sp)
                )

                Text(
                    text = requestDetail.emailAddress,
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = TextUnit(14.0f, TextUnitType.Sp)
                )

                Text(
                    text = "Time of request: ${selectedRequest?.timeOfRequest}",
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = TextUnit(14.0f, TextUnitType.Sp)
                )

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val response = groupViewModel.approveMembershipRequest(selectedRequest!!)
                            if (response.status) {
                                Toast.makeText(context, "Request approved", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    Modifier
                        .padding(horizontal = 4.dp)
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