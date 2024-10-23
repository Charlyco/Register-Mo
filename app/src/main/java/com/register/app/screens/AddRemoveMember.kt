package com.register.app.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.util.AN_ERROR_OCCURRED
import com.register.app.util.CircularIndicator
import com.register.app.util.GenericTopBar
import com.register.app.util.ImageLoader
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch

@Composable
fun AddRemoveMember(authViewModel: AuthViewModel, groupViewModel: GroupViewModel, navController: NavController) {
    Surface(
        Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            MemberSearchBar(authViewModel)
            NewMemberDetail(groupViewModel, authViewModel, navController)
        }
    }
}

@Composable
fun MemberSearchBar(authViewModel: AuthViewModel) {
    var searchTag by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    Surface(
        Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.onPrimary,
        shadowElevation = dimensionResource(id = R.dimen.low_elevation),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = searchTag,
                onValueChange = { searchTag = it },
                modifier = Modifier
                    .height(55.dp),
                placeholder = { Text(
                    text = stringResource(id = R.string.search_by_email),
                    color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                    focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
            )
            Surface(
                Modifier
                    .size(55.dp)
                    .clickable {
                        coroutineScope.launch { authViewModel.getMemberDetails(searchTag) }
                    },
                color = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "",
                    Modifier
                        .size(32.dp)
                        .padding(16.dp),
                    tint =MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun NewMemberDetail(groupViewModel: GroupViewModel, authViewModel: AuthViewModel, navController: NavController) {
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    val memberDetails = authViewModel.intendedMemberLiveData.observeAsState().value
    val progressState = authViewModel.progressLiveData.observeAsState().value
    val loadingState = groupViewModel.loadingState.observeAsState().value
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Surface(
        Modifier
            .fillMaxWidth()
            .padding(top = 72.dp)
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
        color = MaterialTheme.colorScheme.background
        ) {
            if (memberDetails != null) {
                ConstraintLayout(
                    Modifier
                        .fillMaxWidth()
                ) {
                    val (photo, details, progress) = createRefs()
                    Surface(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                            .constrainAs(details) {
                                top.linkTo(photo.bottom, margin = (-64).dp)
                                centerHorizontallyTo(parent)
                            }
                    ) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(top = 72.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            DetailItem("Name:", memberDetails.fullName)
                            DetailItem("Email:", memberDetails.emailAddress)
                            DetailItem("Phone:", memberDetails.phoneNumber)

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        val response = groupViewModel.addMemberToGroup(group?.groupId, memberDetails.emailAddress)
                                        if (response) {
                                            Toast.makeText(context, "Member added to ${group?.groupName}", Toast.LENGTH_LONG).show()
                                            navController.navigateUp()
                                        }else {
                                            Toast.makeText(context, AN_ERROR_OCCURRED, Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 32.dp)
                                    .height(55.dp),
                                shape = MaterialTheme.shapes.large
                            ) {
                            Text(text = stringResource(id = R.string.add_to_group))
                        }
                    }
                    }
                    Surface(
                        Modifier
                            .size(140.dp)
                            .constrainAs(photo) {
                                top.linkTo(parent.top, margin = 16.dp)
                                centerHorizontallyTo(parent)
                            },
                        color = MaterialTheme.colorScheme.background,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        ImageLoader(
                            imageUrl = memberDetails.imageUrl?: "",
                            context = context,
                            height = 140,
                            width = 140,
                            placeHolder = R.drawable.placeholder
                        )
                    }
                    if (loadingState == true || progressState == true) {
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
}

@Composable
fun DetailItem(title: String, value: String) {
    Row(
        Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
       Text(
           text = title,
           color = MaterialTheme.colorScheme.onBackground,
           fontSize = TextUnit(14.0f, TextUnitType.Sp),
           modifier = Modifier.padding(horizontal = 4.dp)
       )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
