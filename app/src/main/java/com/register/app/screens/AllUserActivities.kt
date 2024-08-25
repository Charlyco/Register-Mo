package com.register.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.util.BottomNavBar
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.GroupViewModel

@Composable
fun AllUserActivities(
    activityViewModel: ActivityViewModel,
    groupViewModel: GroupViewModel,
    navController: NavController
) {
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) {
       AllActivitiesContent(Modifier.padding(it),activityViewModel, groupViewModel, navController)

    }
}

@Composable
fun AllActivitiesContent(
    modifier: Modifier,
    activityViewModel: ActivityViewModel,
    groupViewModel: GroupViewModel,
    navController: NavController
) {
    val activityList = activityViewModel.eventFeeds.observeAsState().value
    var searchTag by rememberSaveable { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .padding(bottom = 72.dp)
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            Modifier
                .padding(top = 16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                Modifier
                    .padding(horizontal = 16.dp)
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

            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (activityList != null) {
                    items(activityList.filter { activity -> activity.eventTitle.contains(searchTag, ignoreCase = true) }) {
                        EventItemHome(navController, groupViewModel, activityViewModel, it)
                    }
                }
            }
        }
    }
}
