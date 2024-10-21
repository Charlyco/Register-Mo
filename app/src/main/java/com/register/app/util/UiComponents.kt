package com.register.app.util

import android.content.Context
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.register.app.R
import com.register.app.model.Event
import com.register.app.model.Group
import com.register.app.model.Member
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.GroupViewModel
import com.register.app.viewmodel.QuestionnaireViewModel
import kotlinx.coroutines.launch


@Composable
fun CircularIndicator() {
    Dialog(onDismissRequest = {  }) {
        Surface(
            color = Color.Transparent
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeCap = StrokeCap.Butt,
                strokeWidth = dimensionResource(id = R.dimen.progress_indicator_stroke),
                trackColor = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val backStackEntry = navController.currentBackStackEntry
    val bottomBarItems = listOf(
        BottomBarItem(HOME, R.drawable.icon_home),
        BottomBarItem("forum", R.drawable.chat),
        BottomBarItem(DIRECT_CHAT, R.drawable.members_2),
        BottomBarItem(ALL_USER_ACTIVITIES, R.drawable.icon_activity),
        BottomBarItem("profile", R.drawable.profile_simple)
    )
BottomAppBar(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        topEnd = 24.dp,
                        topStart = 24.dp
                    )
                ),
            containerColor = MaterialTheme.colorScheme.tertiary,
            tonalElevation = 4.dp,
            windowInsets = BottomAppBarDefaults.windowInsets
        ) {
            bottomBarItems.forEach { item ->
                val selected = item.route == backStackEntry?.destination?.route

                NavigationBarItem(
                    selected = selected,
                    onClick = { navController.navigate(item.route){
                        launchSingleTop = true
                        popUpTo("home") }
                    },
                    modifier = Modifier.size(32.dp),
                    //label = { Text(item.label, color = MaterialTheme.colorScheme.primary) },
                    icon = { Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = "") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        unselectedIconColor = MaterialTheme.colorScheme.onBackground)
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericTopBar(title: String, navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        TopAppBar(
            title = { Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
                ) },
            modifier = Modifier.fillMaxWidth(),
            navigationIcon = { Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "",
                Modifier.clickable { navController.navigateUp()},
                tint = MaterialTheme.colorScheme.onBackground
            )},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent)
        )
    }
}

@Composable
fun PasswordTextBox(label: String, callback: (String) -> Unit) {
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    TextField(
        value = password,
        onValueChange = {
            password = it
            callback(password) },
        label = { Text(
            text = label,
            color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = { Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "",
            tint = Color.Gray)},
        trailingIcon = {
            if (showPassword) {
                IconButton(onClick = { showPassword = false }) {
                    Icon(
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = "hide_password",
                        tint = Color.Gray
                    )
                }
            } else {
                IconButton(
                    onClick = { showPassword = true }) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "hide_password",
                        tint = Color.Gray
                    )
                }
            }
        },
        visualTransformation = if (showPassword) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedLabelColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun ImageLoader(imageUrl: String, context: Context, height: Int, width: Int, placeHolder: Int) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(context).data(data = imageUrl).apply(block = fun ImageRequest.Builder.() {
            transformations()
            placeholder(placeHolder)
            error(placeHolder)
        }).build()
    )
    Image(
        painter = painter,
        contentDescription = "",
        modifier = Modifier
            .width(width.dp)
            .height(height.dp),
        alignment = Alignment.Center,
        contentScale = ContentScale.FillBounds
    )
}

@Composable
fun GroupSearchBox(groupViewModel: GroupViewModel, navController: NavController, width: Int) {
    var searchTag by rememberSaveable { mutableStateOf("") }
    Surface(
        Modifier
            .padding(horizontal = 8.dp)
            .width(width.dp),
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
}

@Composable
fun EventItem(
    event: Event,
    group: Group,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    val percent = (event.contributions?.size?.times(100))?.div(group.memberList?.size!!)
    val sliderWidth = LocalConfiguration.current.screenWidthDp - 140
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp)
            .clickable {
                coroutineScope.launch {
                    activityViewModel.setSelectedEvent(event)
                    groupViewModel.getComplianceRate(event) // calculate the compliance rate of this user and set the value to a liveData
                    groupViewModel.isUserAdmin(group) //determine if this user is an admin in the group to which this activity belong
                }
                navController.navigate("event_detail") {
                    launchSingleTop = true
                }
            },
        shape = MaterialTheme.shapes.extraSmall,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onTertiary),
        color = MaterialTheme.colorScheme.background
    ) {
        ConstraintLayout(
            Modifier
                .fillMaxSize()
        ) {
            val (eventTitle, date, rate) = createRefs()

            Text(
                text = event.eventTitle,
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.constrainAs(eventTitle) {
                    start.linkTo(parent.start, margin = 8.dp)
                    top.linkTo(parent.top, margin = 8.dp)
                }
            )
            Row(
                modifier = Modifier.constrainAs(date) {
                    end.linkTo(parent.end, margin = 8.dp)
                    top.linkTo(parent.top, margin = 8.dp)
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp))

                Text(
                    text = Utils.formatToDDMMYYYY(event.dateCreated),
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    color = Color.Gray,
                )
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp)
                    .constrainAs(rate) {
                        centerHorizontallyTo(parent)
                        top.linkTo(eventTitle.bottom, margin = 8.dp)
                        bottom.linkTo(parent.bottom, margin = 8.dp)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Slider(
                    value = event.contributions?.size?.toFloat()?: 0.0f,
                    onValueChange = {},
                    enabled = false ,
                    steps = group.memberList?.size?.minus(1)!!,
                    valueRange = 0f .. group.memberList.size.toFloat(),
                    colors = SliderDefaults.colors(
                        disabledThumbColor = Color.Transparent,
                        disabledActiveTrackColor = MaterialTheme.colorScheme.primary,
                        disabledInactiveTrackColor = MaterialTheme.colorScheme.surface,
                        disabledActiveTickColor = MaterialTheme.colorScheme.primary,
                        disabledInactiveTickColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .width(sliderWidth.dp)
                        .height(8.dp)
                )

                Text(
                    text = "${percent}% Compliance",
                    fontSize = TextUnit(12.0f, TextUnitType.Sp),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun GroupItem(
    group: Group,
    admins: List<Member>?,
    groupViewModel: GroupViewModel,
    questionnaireViewModel: QuestionnaireViewModel,
    activityViewModel: ActivityViewModel,
    navController: NavController,
    width: Int,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val user = authViewModel.userLideData.observeAsState().value
    Surface(
        Modifier
            .width(width.dp)
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .height(72.dp)
            .clickable {
                coroutineScope.launch {
                    groupViewModel.setSelectedGroupDetail(group)
                    navController.navigate("group_detail") { launchSingleTop = true }
                    questionnaireViewModel.getQuestionnaires(group.groupId)
                    activityViewModel.getAllSpecialLeviesForGroup(group.groupId, user?.emailAddress!!)
                }
            },
        color = MaterialTheme.colorScheme.background,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
            ) {
            //val (logo, name, description, memberCount, memberIcons) = createRefs()
            Surface(
                Modifier
                    .size(56.dp)
                    .clip(CircleShape),
//                    .constrainAs(logo) {
//                        start.linkTo(parent.start, margin =4.dp)
//                        centerVerticallyTo(parent) },
                color = Color.Transparent
            ) {
                ImageLoader(group.logoUrl?: "", context, 56, 56, R.drawable.download)
            }

            Column(
                Modifier
                    .width((width - 120).dp),
//                    .constrainAs(name) {
//                        top.linkTo(parent.top, margin = 12.dp)
//                        start.linkTo(logo.end, margin = 4.dp)
//                    },
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = group.groupName,
                    fontSize = TextUnit(16.0f, TextUnitType.Sp),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(text = group.groupDescription?: "",
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                Modifier.padding(end = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    Modifier
                        .padding(bottom = 4.dp),
//                        top.linkTo(parent.top, margin = 8.dp)
//                        end.linkTo(parent.end, margin = 8.dp)
//                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    admins?.forEach { admin ->
                        Surface(
                            Modifier.clip(CircleShape)
                        ) {
                            ImageLoader(admin.imageUrl?: "", context, 20, 20, R.drawable.placeholder) }
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = "+${group.memberList?.size}",
                        fontSize = TextUnit(10.0f, TextUnitType.Sp),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 4.dp))
                }
            }

        }
    }
}

@Composable
fun MemberActivitySwitch(switchView: (showDetails: Boolean) -> Unit) {
    val screenWidth = LocalConfiguration.current.screenWidthDp / 2
    var showPaid by rememberSaveable { mutableStateOf(false)}
    ConstraintLayout(
        Modifier.fillMaxWidth()
    ) {
        val (paid, unPaid, paidLiner, unPaidLiner) = createRefs()
        Text(
            text = stringResource(id = R.string.paid_activities),
            Modifier
                .width((screenWidth - 24).dp)
                .padding(top = 4.dp)
                .clickable {
                    showPaid = true
                    switchView(showPaid)
                }
                .constrainAs(paid) {
                    start.linkTo(parent.start)
                },
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(id = R.string.unpaid_activities),
            Modifier
                .width((screenWidth - 24).dp)
                .padding(top = 4.dp)
                .clickable {
                    showPaid = false
                    switchView(showPaid)
                }
                .constrainAs(unPaid) {
                    end.linkTo(parent.end)
                },
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold
        )
        if (showPaid) {
            Surface(
                Modifier
                    .width((screenWidth - 24).dp)
                    .height(4.dp)
                    .padding(vertical = 1.dp)
                    .constrainAs(paidLiner) {
                        start.linkTo(parent.start, margin = 1.dp)
                        top.linkTo(paid.bottom, margin = 8.dp)
                    },
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary
            ) {}
        }

        if (!showPaid) {
            Surface(
                Modifier
                    .width((screenWidth - 24).dp)
                    .height(4.dp)
                    .padding(vertical = 1.dp)
                    .constrainAs(unPaidLiner) {
                        end.linkTo(parent.end, margin = 1.dp)
                        top.linkTo(unPaid.bottom, margin = 8.dp)
                    },
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary
            ) {}
        }
    }
}