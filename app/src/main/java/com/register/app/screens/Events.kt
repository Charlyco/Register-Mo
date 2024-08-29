package com.register.app.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
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
import com.register.app.dto.BulkPaymentModel
import com.register.app.enums.PaymentMethod
import com.register.app.model.Event
import com.register.app.model.Group
import com.register.app.util.ImageLoader
import com.register.app.util.Utils
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch

@Composable
fun Events(
    navController: NavController,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    title: String?,
    activityViewModel: ActivityViewModel
) {
    val isUnpaid = title == "Unpaid Activities"
    var showPayments by rememberSaveable { mutableStateOf(false)}

    BackHandler {
            navController.navigate("home") {
                popUpTo("event") {inclusive = true}
                launchSingleTop = true
            }
    }
    Scaffold(
        topBar = { EventListTopBar(
            title = title!!,
            navController = navController,
            activityViewModel,
            groupViewModel
        ){ showPayments = it }},
        floatingActionButton = { if(authViewModel.isUserAdmin()) {
            NewEventActionButton(groupViewModel, navController) }},
        floatingActionButtonPosition = FabPosition.End
    ) {
        EventScreenDetail(Modifier.padding(it),navController, groupViewModel, activityViewModel, isUnpaid)
        if (showPayments) {
            BulkPaymentListDialog(navController, groupViewModel, activityViewModel) { showPayments = it }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListTopBar(
    title: String,
    navController: NavController,
    activityViewModel: ActivityViewModel,
    groupViewModel: GroupViewModel,
    onShowEvent: (shouldShow: Boolean) -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val isUserAdmin = groupViewModel.isUserAdminLiveData.observeAsState().value
    val bulkPayments = activityViewModel.pendingBulkPayments.observeAsState().value
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        TopAppBar(
            title = { Text(text = title) },
            modifier = Modifier.fillMaxWidth(),
            navigationIcon = { Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "",
                Modifier.clickable { navController.navigateUp()},
                tint = MaterialTheme.colorScheme.onBackground
            )},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent),
            actions = {
                if (isUserAdmin == true) {
                    Box(
                        Modifier
                            .size(32.dp)
                            .clickable {
                                onShowEvent(true)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(painter = painterResource(id = R.drawable.wallet),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.secondary)
                        if (!bulkPayments.isNullOrEmpty()) {
                            Text(
                                text = bulkPayments.size.toString(),
                                fontSize = TextUnit(10.0f, TextUnitType.Sp),
                                color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }
                IconButton(
                    onClick = { isExpanded = !isExpanded }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = stringResource(id = R.string.menu))
                }
                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false },
                    modifier = Modifier.width(160.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.settings)) },
                        onClick = {
                            isExpanded = false
                        },
                        colors = MenuDefaults.itemColors(

                        ),
                        leadingIcon = { Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(id = R.string.settings)
                        ) }
                    )

                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.privacy)) },
                        onClick = {
                            isExpanded = false
                        },
                        leadingIcon = { Icon(
                            imageVector = Icons.Default.PrivacyTip,
                            contentDescription = stringResource(id = R.string.privacy)
                        ) }
                    )

                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.about)) },
                        onClick = {
                            isExpanded = false
                        },
                        leadingIcon = { Icon(
                            imageVector = Icons.Default.Details,
                            contentDescription = stringResource(id = R.string.about)
                        ) }
                    )

                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.sign_out)) },
                        onClick = {
                            isExpanded = false
                            navController.navigate("signin") {
                                popUpTo("home") {
                                    inclusive = true
                                }
                            }
                        },
                        leadingIcon = { Icon(
                            imageVector = Icons.Default.ArrowOutward,
                            contentDescription = stringResource(id = R.string.sign_out)
                        ) }
                    )
                }
            }
        )
    }
}
@Composable
fun EventScreenDetail(
    modifier: Modifier,
    navController: NavController,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    isUnpaid: Boolean
) {
    val feedList = activityViewModel.groupEvents.observeAsState().value
    var selectedEvents = mutableListOf<Event>()

    Surface(
        Modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.End
        ) {
            if (isUnpaid) {
                    Text(
                        text = stringResource(id = R.string.pay_all),
                        fontSize = TextUnit(14.0f, TextUnitType.Sp),
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable {
                                selectedEvents.clear()
                                selectedEvents.addAll(feedList!!)
                                var totalAmount = 0.0
                                if (selectedEvents.isNotEmpty()) {
                                    totalAmount = selectedEvents.sumOf { it.levyAmount ?: 0.0 }
                                }
                                activityViewModel.setBulkPaymentSelection(selectedEvents)
                                navController.navigate("bulk_payment/${totalAmount}") {
                                    launchSingleTop = true
                                }
                            }
                    )
            }
            EventList(navController, groupViewModel, activityViewModel, isUnpaid, feedList) {
                selectedEvents = it.toMutableList()
                var totalAmount = 0.0
                if (selectedEvents.isNotEmpty()) {
                    totalAmount = selectedEvents.sumOf { it.levyAmount?: 0.0 }
                }
                activityViewModel.setBulkPaymentSelection(selectedEvents)
                navController.navigate("bulk_payment/${totalAmount}") {
                    launchSingleTop = true
                }
            }
        }
    }
}

@Composable
fun EventList(
    navController: NavController,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    isUnpaid: Boolean,
    feedList: List<Event>?,
    onItemsSelected: (List<Event>) -> Unit
) {
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    val selectedEvents = mutableListOf<Event>()
    if (feedList?.isNotEmpty() == true) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            rememberLazyListState()
        ) {
            if (selectedEvents.isNotEmpty()) {
                item{
                    Text(
                        text = stringResource(id = R.string.pay_selection),
                        fontSize = TextUnit(14.0f, TextUnitType.Sp),
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .clickable {
                                onItemsSelected(selectedEvents)
                            }
                    )
                }
            }
            items(feedList) {eventFeed ->
                EventFeedItem(eventFeed, group!!, groupViewModel, activityViewModel,  navController, isUnpaid) {
                    if (it) selectedEvents.add(eventFeed) else selectedEvents.remove(eventFeed)
                }
            }
        }
    }
}

@Composable
fun EventFeedItem(
    event: Event,
    group: Group,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    navController: NavController,
    isUnpaid: Boolean,
    onSelectionChanged: (isSelected: Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val percent = (event.contributions?.size?.times(100))?.div(group.memberList?.size!!)
    val sliderWidth = LocalConfiguration.current.screenWidthDp - 140
    var isChecked by rememberSaveable { mutableStateOf(false) }


    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
        shape = MaterialTheme.shapes.extraSmall,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onTertiary),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            if (isUnpaid) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = {
                        isChecked = !isChecked
                        onSelectionChanged(isChecked)},
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                )
            }
            ConstraintLayout(
                Modifier
                    .fillMaxSize()
                    .clickable {
                        coroutineScope.launch {
                            activityViewModel.setSelectedEvent(event)
                            groupViewModel.getComplianceRate(event) // calculate the compliance rate of this user and set the value to a liveData
                            groupViewModel.isUserAdmin(group) //determine if this user is an admin in the group to which this activity belong
                        }
                        navController.navigate("event_detail") {
                            launchSingleTop = true
                        }
                    }
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
}

@Composable
fun NewEventActionButton(
    groupViewModel: GroupViewModel,
    navController: NavController
) {
    Surface(
        Modifier
            .size(56.dp)
            .clip(CircleShape)
            .clickable { },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulkPaymentListDialog(
    navController: NavController,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    onShowDialogChanged: (Boolean) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val paymentList = activityViewModel.pendingBulkPayments.observeAsState().value
    var showImage by rememberSaveable { mutableStateOf(false) }
    var selectedPayment by rememberSaveable { mutableStateOf<BulkPaymentModel?>(null) }
    ModalBottomSheet(
        onDismissRequest = {
            onShowDialogChanged(false)
        },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        sheetMaxWidth = screenWidth.dp,
        modifier = Modifier
            .height(screenHeight.dp)
            .padding(start = 4.dp, end = 4.dp, top = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.payment),
            fontWeight = FontWeight.SemiBold,
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            modifier = Modifier.fillMaxWidth()
            ,
            textAlign = TextAlign.Center)

        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        if (!paymentList.isNullOrEmpty()) {
            LazyColumn(
                Modifier.fillMaxWidth(),
                rememberLazyListState(),
            ) {
                items(paymentList) { payment ->
                    BulkPaymentItemComposable(payment, groupViewModel, activityViewModel) { show, selected ->
                        showImage = show
                        selectedPayment = selected
                    }
                }
            }
        }
        if (showImage) {
            ConfirmPaymentDialog(selectedPayment, activityViewModel, groupViewModel) {showImage = it}
        }
    }
}

@Composable
fun BulkPaymentItemComposable(
    payment: BulkPaymentModel,
    groupViewModel: GroupViewModel,
    activityViewModel1: ActivityViewModel,
    callBack: (Boolean, BulkPaymentModel) -> Unit = { _, _ -> }
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable { callBack(true, payment) },
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = payment.payerFullName,
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = payment.groupName,
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Amount to pay: ${payment.amountToPay}",
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onBackground
        )
        HorizontalDivider()
    }
}

@Composable
fun ConfirmPaymentDialog(
    selectedPayment: BulkPaymentModel?,
    activityViewModel: ActivityViewModel,
    groupViewModel: GroupViewModel,
    callback: (show: Boolean) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var showReasonInputDialog by rememberSaveable { mutableStateOf(false) }
    var reason by rememberSaveable { mutableStateOf("") }
    Dialog(
        onDismissRequest = { callback(false)}) {
        Surface(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState(initial = 0)),
            color = MaterialTheme.colorScheme.background
        ) {
            Dialog(onDismissRequest = { showReasonInputDialog = false}) {
                Surface(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = MaterialTheme.shapes.small
                ) {
                    ConstraintLayout(
                        Modifier.fillMaxWidth()
                    ) {
                        val (input, btn) = createRefs()

                        TextField(
                            value = reason,
                            onValueChange = { reason = it },
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = MaterialTheme.colorScheme.background,
                                unfocusedContainerColor = MaterialTheme.colorScheme.background
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Proceed",
                        modifier = Modifier
                            .constrainAs(btn) {
                                top.linkTo(input.bottom, margin = 8.dp)
                                end.linkTo(parent.end, margin = 8.dp)

                            }
                            .clickable {
                                coroutineScope.launch {
                                    val response =
                                        activityViewModel.rejectBulkPayment(selectedPayment, reason)
                                    if (response.status) {
                                        callback(false)
                                        Toast
                                            .makeText(context, response.message, Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                            }
                        )
                    }
                }
            }
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImageLoader(
                    selectedPayment?.imageUrl ?: "",
                    LocalContext.current,
                    screenHeight - 180,
                    screenWidth - 16,
                    R.drawable.event
                )
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = stringResource(id = R.string.list_of_activities_paid),
                        fontSize = TextUnit(14.0f, TextUnitType.Sp),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                        )

                    selectedPayment?.eventItemDtos?.forEach { eventItemDto ->
                        Text(
                            text = "--*-- ${eventItemDto.eventTitle}",
                            fontSize = TextUnit(14.0f, TextUnitType.Sp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                    Button(
                        onClick = {
                            showReasonInputDialog = true
                        },
                        Modifier
                            .width(((screenWidth/ 2) - 16).dp),
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Text(text = stringResource(id = R.string.reject_payment))
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val response = activityViewModel.confirmBulkPayment(
                                    selectedPayment, PaymentMethod.BANK_TRANSFER.name)
                                if (response.status) {
                                    callback(false)
                                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                                }else {
                                    Toast.makeText(context, response.message,
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        Modifier
                            .width(((screenWidth/ 2) - 16).dp),
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                }
            }
        }
    }
}