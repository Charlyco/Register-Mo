package com.register.app.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.register.app.R
import com.register.app.enums.GroupType
import com.register.app.model.Group
import com.register.app.model.Member
import com.register.app.util.GenericTopBar
import com.register.app.util.ImageLoader
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.GroupViewModel
import com.register.app.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun SuggestedGroups(
    groupViewModel: GroupViewModel,
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel
    ) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var selectedGroup : Group? = null
    Scaffold(
        topBar = { GenericTopBar(title = "Find a Group", navController = navController, navRoute = "home") },
        containerColor = MaterialTheme.colorScheme.background
    ) {
        GroupList(Modifier.padding(it), groupViewModel = groupViewModel, navController = navController){ show, group ->
            showDialog = show
            selectedGroup = group
        }
        if (showDialog) {
            var admins : List<Member>? = null
            LaunchedEffect(key1 = 262) {
                admins = selectedGroup?.memberList?.let { groupViewModel.filterAdmins(it) }
            }
            JoinGroupDialog(
                groupViewModel,
                authViewModel,
                activityViewModel,
                homeViewModel,
                admins,
                selectedGroup,
                navController
            ) { showDialog = it }
        }
    }
}

@Composable
fun GroupList(
    modifier: Modifier,
    groupViewModel: GroupViewModel,
    navController: NavHostController,
    showDialog: (shouldShow: Boolean, selectedGroup: Group?) -> Unit
) {
    val groupList = groupViewModel.suggestedGroupList.observeAsState().value
    if (!groupList.isNullOrEmpty()) {
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(top = 64.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(groupList) { group ->
                var admins by rememberSaveable { mutableStateOf<List<Member>?>(null) }
                LaunchedEffect(key1 = 261) {
                    admins = group.memberList?.let { groupViewModel.filterAdmins(it) }
                }
                SuggestedGroupItem(group, admins, groupViewModel) { showDialog, selectedGroup ->
                    showDialog(showDialog, selectedGroup)
                }
            }
        }
    }
}

@Composable
fun SuggestedGroupItem(
    group: Group,
    admins: List<Member>?,
    groupViewModel: GroupViewModel,
    function: (shouldShow: Boolean, selectedGroup: Group?) -> Unit,
) {
    val context = LocalContext.current
    val itemWidth = LocalConfiguration.current.screenWidthDp - 32

    Surface(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(72.dp)
            .clickable {
                function(true, group)
            },
        color = MaterialTheme.colorScheme.background,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary)
    ) {
        ConstraintLayout(
            Modifier
                .fillMaxWidth()) {
            val (logo, name, description, memberCount, memberIcons) = createRefs()

            Surface(
                Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .constrainAs(logo) {
                        start.linkTo(parent.start, margin = 8.dp)
                        centerVerticallyTo(parent)
                    },
                color = Color.Transparent
            ) {
                ImageLoader(group.logoUrl?: "", context, 56, 56, R.drawable.download)
            }

            Text(
                text = group.groupName,
                Modifier
                    .width((itemWidth - 20).dp)
                    .constrainAs(name) {
                        top.linkTo(parent.top, margin = 12.dp)
                        start.linkTo(logo.end, margin = 8.dp)
                    },
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start
            )

            Text(text = group.groupDescription?: "",
                Modifier
                    .width((itemWidth - 20).dp)
                    .constrainAs(description) {
                        bottom.linkTo(parent.bottom, margin = 12.dp)
                        start.linkTo(logo.end, margin = 8.dp)

                    },
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start
            )

            Column(
                Modifier.constrainAs(memberIcons) {
                    top.linkTo(parent.top, margin = 8.dp)
                    end.linkTo(parent.end, margin = 8.dp)
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                admins?.forEach { admin ->
                    Surface(
                        Modifier.clip(CircleShape)
                    ) {
                        ImageLoader(admin.imageUrl?: "", context, 20, 20, R.drawable.placeholder) }
                }
            }

            Surface(
                Modifier
                    .constrainAs(memberCount) {
                        top.linkTo(memberIcons.bottom, margin = 4.dp)
                        centerHorizontallyTo(memberIcons)
                    },
                color = MaterialTheme.colorScheme.tertiary,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "+${group.memberList?.size}",
                    fontSize = TextUnit(10.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp))
            }
        }
    }
}

@Composable
fun JoinGroupDialog(
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    activityViewModel: ActivityViewModel,
    homeViewModel: HomeViewModel,
    admins: List<Member>?,
    selectedGroup: Group?,
    navController: NavHostController,
    function: (show: Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    Dialog(
        onDismissRequest = { function(false) },
        ) {
        Surface(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(280.dp),
            shape = MaterialTheme.shapes.large
        ) {
            ConstraintLayout(
                Modifier.fillMaxWidth()
            ) {
                val (logo, name, description, memberIcons, memberCount, requestBtn) = createRefs()

                Surface(
                    Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .constrainAs(logo) {
                            top.linkTo(parent.top, margin = 16.dp)
                            centerHorizontallyTo(parent)
                        }
                ) {
                    ImageLoader(
                        selectedGroup?.logoUrl ?: "",
                        LocalContext.current,
                        72, 72,
                        R.drawable.download
                    )
                }
                    Text(
                        text = selectedGroup?.groupName!!,
                        fontSize = TextUnit(16.0f, TextUnitType.Sp),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .constrainAs(name) {
                                top.linkTo(logo.bottom, margin = 8.dp)
                                centerHorizontallyTo(parent)
                            }
                        )

                    Text(
                        text = selectedGroup.groupDescription?: "",
                        fontSize = TextUnit(14.0f, TextUnitType.Sp),
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .constrainAs(description) {
                                top.linkTo(name.bottom, margin = 8.dp)
                                centerHorizontallyTo(parent)
                            }
                    )

                    Row(
                        Modifier.constrainAs(memberIcons) {
                            top.linkTo(description.bottom, margin = 8.dp)
                            centerHorizontallyTo(parent)
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        admins?.forEach { admin ->
                            Surface(
                                Modifier.clip(CircleShape)
                            ) {
                                ImageLoader(admin.imageUrl?: "", LocalContext.current, 20, 20, R.drawable.placeholder) }
                        }
                    }

                    Surface(
                        Modifier
                            .constrainAs(memberCount) {
                                top.linkTo(memberIcons.bottom, margin = 4.dp)
                                centerHorizontallyTo(memberIcons)
                            },
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = "+${selectedGroup.memberList?.size}",
                            fontSize = TextUnit(10.0f, TextUnitType.Sp),
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 4.dp))
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val response = groupViewModel.requestToJoinGroup(selectedGroup)
                                if (response.status) {
                                    Toast.makeText(context,
                                        "You have successfully joined ${selectedGroup.groupName}",
                                        Toast.LENGTH_LONG).show()
                                    function(false)
                                    navController.navigateUp()
                                    authViewModel.reloadUserData()
                                    homeViewModel.refreshHomeContents()
                                    activityViewModel.refreshHomeContents()
                                    groupViewModel.getAllGroupsForUser()

                                }else {
                                    Toast.makeText(context, "You are a member of this group already", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .constrainAs(requestBtn) {
                                top.linkTo(memberCount.bottom, margin = 8.dp)
                                centerHorizontallyTo(parent)
                            },
                        shape = MaterialTheme.shapes.small
                        ) {
                        if (selectedGroup.groupType == GroupType.OPEN.name)  {
                            Text(text = stringResource(id = R.string.join_group))
                        } else if (selectedGroup.groupType == GroupType.CLOSED.name) {
                        Text(text = stringResource(id = R.string.request_to_join))
                    }
                }
            }
        }
    }
}