package com.register.app.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.util.BottomNavBar
import com.register.app.util.MemberActivitySwitch
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.GroupViewModel

@Composable
fun AllUserActivities(
    activityViewModel: ActivityViewModel,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) {
       AllActivitiesContent(Modifier.padding(it),activityViewModel, groupViewModel, authViewModel, navController)

    }
}

@Composable
fun AllActivitiesContent(
    modifier: Modifier,
    activityViewModel: ActivityViewModel,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val activityList = activityViewModel.eventFeeds.observeAsState().value
    val userEmail = authViewModel.userLideData.observeAsState().value?.emailAddress
    val paidActivityList = activityList?.filter{ activity -> activity.contributions?.find { it.memberEmail == userEmail } != null}
    val unPaidActivityList = activityList?.filter{ activity -> activity.contributions?.find { it.memberEmail == userEmail } == null}
    var showPaid by rememberSaveable { mutableStateOf(false) }
    var searchTag by rememberSaveable { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .padding(bottom = 72.dp)
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            Modifier
                .padding(top = 16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MemberActivitySwitch { showPaid = it }

            Surface(
                Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
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
                    placeholder = { Text(
                        text = stringResource(id = R.string.search_activity),
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

            if (showPaid) {
                if (paidActivityList != null) {
                LazyColumn(
                    state = rememberLazyListState(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(paidActivityList.filter { activity -> activity.eventTitle.contains(searchTag, ignoreCase = true) }) {

                        EventItemHome(navController, groupViewModel, activityViewModel, it)
                    }
                } }else{
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
                            text = stringResource(id = R.string.no_paid_activities),
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }else {
                if (unPaidActivityList != null) {
                LazyColumn(
                    state = rememberLazyListState(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(unPaidActivityList.filter { activity -> activity.eventTitle.contains(searchTag, ignoreCase = true) }) {
                        EventItemHome(navController, groupViewModel, activityViewModel, it)
                        }
                    }
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
                                .padding(top = 8.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
