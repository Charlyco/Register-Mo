package com.register.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.model.Group
import com.register.app.model.Member
import com.register.app.util.ImageLoader
import com.register.app.viewmodel.GroupViewModel

@Composable
fun GroupDetail(navController: NavController, groupViewModel: GroupViewModel) {
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    Scaffold(
        topBar = { GroupDetailTopBar(navController, group) },
    ) {
       GroupDetailScreen(Modifier.padding(it), navController, groupViewModel, group)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailTopBar(navController: NavController, group: Group?) {
    TopAppBar(
        title = { Text(
            text = group?.groupName!!,
            Modifier.padding(start = 32.dp)
            ) },
        navigationIcon = {
            Icon(
                painter = painterResource(id = R.drawable.caret_back_circle),
                contentDescription = "",
                Modifier.size(24.dp)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
fun GroupDetailScreen(
    modifier: Modifier,
    navController: NavController,
    groupViewModel: GroupViewModel,
    group: Group?
) {
    var showProfileDetail by rememberSaveable { mutableStateOf(false) }
    var showAdminList by rememberSaveable { mutableStateOf(false) }
    Surface(
        Modifier
            .padding(top = 64.dp)
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            Modifier.fillMaxWidth()
        ) {
            item { TopSection(group) }
            item {
                GroupProfileHeader(group, showProfileDetail) {showProfileDetail = it}
            }
            if (showProfileDetail) {
                item { GroupProfile(group, groupViewModel) }
            }
            item { HorizontalDivider() }
            item { GroupAdminHeader(group, showAdminList) {showAdminList = it} }

            if (showAdminList) {
                item { GroupAdminList(group, groupViewModel, navController) }
            }
            item { HorizontalDivider() }
        }
    }
}


@Composable
fun GroupAdminHeader(group: Group?, showAdminList: Boolean, shouldShow: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clickable { shouldShow(!showAdminList) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.group_admin),
            fontSize = TextUnit(18.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold
        )
        if (showAdminList) {
            Icon(
                painter = painterResource(id = R.drawable.up_arrow_solid),
                contentDescription = "",
                Modifier
                    .size(16.dp)
                    .clickable { shouldShow(false) }
            )
        }else {
            Icon(
                painter = painterResource(id = R.drawable.forward_arrow_solid),
                contentDescription = "",
                Modifier
                    .size(16.dp)
                    .clickable { shouldShow(true) }
            )
        }
    }
}

@Composable
fun GroupProfileHeader(group: Group?, showProfileDetail: Boolean, showProfile: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clickable { showProfile(!showProfileDetail) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.group_profile),
            fontSize = TextUnit(18.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold
        )
        if (showProfileDetail) {
            Icon(
                painter = painterResource(id = R.drawable.up_arrow_solid),
                contentDescription = "",
                Modifier
                    .size(16.dp)
                    .clickable { showProfile(false) }
            )
        }else {
            Icon(
                painter = painterResource(id = R.drawable.forward_arrow_solid),
                contentDescription = "",
                Modifier
                    .size(16.dp)
                    .clickable { showProfile(true) }
            )
        }
    }
}

@Composable
fun TopSection(group: Group?) {
    val context = LocalContext.current
    Surface(
        Modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondary
    ) {
        ConstraintLayout(
            Modifier.fillMaxSize()
        ) {
            val (logo, header, description) = createRefs()

            Surface(
                Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .constrainAs(logo) {
                        top.linkTo(parent.top, margin = 32.dp)
                        centerHorizontallyTo(parent)
                    },
                color = MaterialTheme.colorScheme.background
            ) {
                ImageLoader(imageUrl = group?.logoUrl!!, context = context, height = 80, width = 80)
            }

            Text(
                text = stringResource(id = R.string.group_description),
                modifier = Modifier
                    .constrainAs(header) {
                    top.linkTo(logo.bottom, margin = 16.dp)
                    centerHorizontallyTo(parent)
                },
                fontSize = TextUnit(22.0f, TextUnitType.Sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
                )

            Text(
                text = group?.groupDescription!!,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .constrainAs(description) {
                        top.linkTo(header.bottom)
                        centerHorizontallyTo(parent)
                    },
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun GroupProfile(group: Group?, groupViewModel: GroupViewModel) {
    Column(
        Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.address),
            modifier = Modifier.padding(vertical = 4.dp),
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Surface(
            Modifier
                .fillMaxWidth()
                .height(48.dp),
            border = BorderStroke(1.dp, Color.Gray),
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.background
        ) {
            Text(
                text = group?.address?: "",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = stringResource(id = R.string.group_email),
            modifier = Modifier.padding(vertical = 4.dp),
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Surface(
            Modifier
                .fillMaxWidth()
                .height(48.dp),
            border = BorderStroke(1.dp, Color.Gray),
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.background
        ) {
            Text(
                text = group?.email?: "",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = stringResource(id = R.string.phone),
            modifier = Modifier.padding(vertical = 4.dp),
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Surface(
            Modifier
                .fillMaxWidth()
                .height(48.dp),
            border = BorderStroke(1.dp, Color.Gray),
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.background
        ) {
            Text(
                text = group?.phone?: "",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = stringResource(id = R.string.date_established),
            modifier = Modifier.padding(vertical = 4.dp),
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Surface(
            Modifier
                .fillMaxWidth()
                .height(48.dp),
            border = BorderStroke(1.dp, Color.Gray),
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.background
        ) {
            Text(
                text = group?.dateCreated?: "",
                modifier = Modifier.padding(horizontal = 8.dp, 10.dp),
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = stringResource(id = R.string.created_by),
            modifier = Modifier.padding(vertical = 4.dp),
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Surface(
            Modifier
                .fillMaxWidth()
                .height(48.dp),
            border = BorderStroke(1.dp, Color.Gray),
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.background
        ) {
            Text(
                text = group?.creatorName?: "",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

    }
}

@Composable
fun GroupAdminList(group: Group?, groupViewModel: GroupViewModel, navController: NavController) {
    if (group?.adminList?.isNotEmpty() == true) {
        LaunchedEffect(key1 = 256) {
            groupViewModel.getIndividualAdminDetail()
        }
        val adminList = groupViewModel.groupAdminList.observeAsState().value
        if (adminList?.isNotEmpty() == true) {
            LazyColumn(
                Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(adminList) { admin ->
                    AdminItem(admin)
                }
            }
        }
    }
}

@Composable
fun AdminItem(admin: Member) {
    Surface(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        shadowElevation = dimensionResource(id = R.dimen.default_elevation),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.background
    ) {
        val context = LocalContext.current
        ConstraintLayout {
            val (profilePic, name, office) = createRefs()

            Surface(
                Modifier
                    .size(48.dp)
                    .constrainAs(profilePic) {
                        centerVerticallyTo(parent)
                        start.linkTo(parent.start, margin = 8.dp)
                    },
                shape = MaterialTheme.shapes.small
            ) {
                ImageLoader(imageUrl = admin.imageUrl ?: "", context = context, height = 44, width = 44)
            }

            Text(
                text = "Name: ${admin.fullName}",
                Modifier.constrainAs(name) {
                    start.linkTo(profilePic.end, margin = 16.dp)
                    top.linkTo(parent.top, margin = 8.dp)
                },
                fontSize = TextUnit(18.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = "Office: ${admin.memberPost}",
                Modifier.constrainAs(office) {
                    start.linkTo(profilePic.end, margin = 16.dp)
                    top.linkTo(name.bottom, margin = 8.dp)
                },
                color = Color.DarkGray
            )
        }
    }
}
