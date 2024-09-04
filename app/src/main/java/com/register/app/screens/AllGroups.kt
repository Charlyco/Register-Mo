package com.register.app.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.model.Member
import com.register.app.util.DataStoreManager
import com.register.app.util.GenericTopBar
import com.register.app.util.GroupItem
import com.register.app.viewmodel.GroupViewModel
import com.register.app.viewmodel.QuestionnaireViewModel

@Composable
fun AllGroups(
    navController: NavController,
    dataStoreManager: DataStoreManager,
    groupViewModel: GroupViewModel,
    questionnaireViewModel: QuestionnaireViewModel
) {
    BackHandler {
        navController.navigate("home") {
            popUpTo("groups") {inclusive = true}
            launchSingleTop = true
        }
    }

    Scaffold(
        topBar = { GenericTopBar(title = stringResource(id = R.string.groups), navController) },
        floatingActionButton = { NewGroupFab(navController, dataStoreManager, groupViewModel) },
        floatingActionButtonPosition = FabPosition.End
    ) {
        GroupsScreenContent(Modifier.padding(it), navController, questionnaireViewModel, groupViewModel)
        CreateGroupScreen(groupViewModel = groupViewModel, navController) { show->
            groupViewModel.showCreateGroupSheet.postValue(show)
        }
    }
}

@Composable
fun NewGroupFab(
    navController: NavController,
    dataStoreManager: DataStoreManager,
    groupViewModel: GroupViewModel
) {
    Surface(
        Modifier
            .size(56.dp)
            .clip(CircleShape)
            .clickable { groupViewModel.showCreateGroupSheet.postValue(true) },
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = dimensionResource(id = R.dimen.default_elevation)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "",
            Modifier.size(24.dp)
        )
    }
}

@Composable
fun GroupsScreenContent(
    modifier: Modifier,
    navController: NavController,
    questionnaireViewModel: QuestionnaireViewModel,
    groupViewModel: GroupViewModel
) {
    val groupList = groupViewModel.groupListLiveData.observeAsState().value
    val loadingState = groupViewModel.loadingState.observeAsState().value
    val screenWidth = LocalConfiguration.current.screenWidthDp
    var searchTag by rememberSaveable { mutableStateOf("") }
    Surface(
        Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            Modifier
                .padding(top = 64.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
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

            Surface(
                Modifier
                    .padding(horizontal = 8.dp)
                    .width(screenWidth.dp),
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
                        text = stringResource(id = R.string.search_group),
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

            if (!groupList.isNullOrEmpty()) {
                LazyColumn(
                    Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(groupList.filter { group -> group.groupName.contains(searchTag, ignoreCase = true) }) { group ->
                        var admins by rememberSaveable { mutableStateOf<List<Member>?>(null) }
                        LaunchedEffect(key1 = 260) {
                            admins = group.memberList?.let { groupViewModel.filterAdmins(it) }
                        }
                        GroupItem(
                            group,
                            admins,
                            groupViewModel,
                            questionnaireViewModel,
                            navController,
                            screenWidth - 8
                        )
                    }
                }
            }
        }
    }
}
