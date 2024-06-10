package com.register.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.register.app.R
import com.register.app.model.Group
import com.register.app.util.BottomNavBar
import com.register.app.util.DataStoreManager
import com.register.app.util.DateFormatter.Companion.formatDateTime
import com.register.app.util.GenericTopBar
import com.register.app.util.GroupItem
import com.register.app.util.ImageLoader
import com.register.app.viewmodel.GroupViewModel

@Composable
fun AllGroups(navController: NavController, dataStoreManager: DataStoreManager, groupViewModel: GroupViewModel) {
    Scaffold(
        topBar = { GenericTopBar(title = stringResource(id = R.string.groups), navController, navRoute = "home") },
        bottomBar = { BottomNavBar(navController = navController) },
        floatingActionButton = { NewGroupFab(navController, dataStoreManager, groupViewModel) },
        floatingActionButtonPosition = FabPosition.End
    ) {
        GroupsScreenContent(Modifier.padding(it), navController, dataStoreManager, groupViewModel)
        CreateGroupScreen(groupViewModel = groupViewModel) { show->
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
    dataStoreManager: DataStoreManager,
    groupViewModel: GroupViewModel
) {
    val groupList = groupViewModel.groupListLiveData.observeAsState().value
    Surface(
        Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            Modifier
                .padding(top = 64.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GroupSearchBox(groupViewModel, navController)

            if (!groupList.isNullOrEmpty()) {
                LazyColumn(
                    Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(groupList) { group ->
                        GroupItem(group, groupViewModel, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun GroupSearchBox(groupViewModel: GroupViewModel, navController: NavController) {
    var searchTag by rememberSaveable { mutableStateOf("") }
    Surface(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = dimensionResource(id = R.dimen.default_elevation),
        shape = MaterialTheme.shapes.large
    ) {
        TextField(
            value = searchTag,
            onValueChange = { searchTag = it },
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
            placeholder = { Text(text = stringResource(id = R.string.search_group)) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "")
            }
        )
    }
}

@Preview
@Composable
fun PreviewGroup() {
    AllGroups(
        navController = rememberNavController(),
        dataStoreManager = DataStoreManager.getInstance(LocalContext.current),
        groupViewModel = GroupViewModel(DataStoreManager.getInstance(LocalContext.current))
    )
}

@Preview
@Composable
fun PreviewGroupItem() {
    GroupItem(
        group = Group(1, "IHS-2008", "200d set of Isuikwuato High School",
            "charlyco@gmail.com", "+234-7037590923", "12 Achuzilam avenue Umuoma Nekede Owerri","Onuoha Charles", "", listOf(), listOf(), listOf(), "", "", "CLOSED", "" ),
        navController = rememberNavController(),
        groupViewModel = GroupViewModel(DataStoreManager.getInstance(LocalContext.current))
    )
}