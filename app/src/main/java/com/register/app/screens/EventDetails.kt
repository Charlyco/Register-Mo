package com.register.app.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.dto.CommentReply
import com.register.app.dto.EventComment
import com.register.app.dto.ReactionType
import com.register.app.model.Event
import com.register.app.util.DataStoreManager
import com.register.app.util.ImageLoader
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import com.register.app.dto.Payment
import com.register.app.enums.AdminActions
import com.register.app.enums.PaymentMethod
import com.register.app.util.CircularIndicator
import com.register.app.util.Utils
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.GroupViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun EventDetails(
    dataStoreManager: DataStoreManager,
    navController: NavController,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    activityViewModel: ActivityViewModel
) {
    val selectedEvent = activityViewModel.selectedEvent.observeAsState().value
    var showPayments by rememberSaveable { mutableStateOf(false) }
    BackHandler {
        navController.navigate("home") {
            popUpTo("event_detail") { inclusive = true }
            launchSingleTop = true
        }
    }

    Scaffold(
        topBar = { EventDetailTopBar(navController, groupViewModel, activityViewModel, selectedEvent){ showPayments = it } }
    ) {
        EventDetailContent(Modifier.padding(it), dataStoreManager, navController, groupViewModel, activityViewModel, authViewModel)
        if (showPayments) {
            PaymentScreen(navController, groupViewModel, activityViewModel) {
                shouldShow -> showPayments = shouldShow
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventDetailContent(
    modifier: Modifier,
    dataStoreManager: DataStoreManager,
    navController: NavController,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel
) {
    val event = activityViewModel.selectedEvent.observeAsState().value
    //val commentList = groupViewModel.eventCommentLiveData.observeAsState().value
    val pageState = rememberPagerState(pageCount = { event?.imageUrlList?.size?: 0} )
    var showDetails by rememberSaveable { mutableStateOf(true)}
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp - 64
    val scrollState = rememberScrollState(initial = 0)
    val isLoading = activityViewModel.loadingState.observeAsState().value
    var likeList = 0
    var loveList = 0

    event?.eventReactionsList?.forEach { eventReaction ->
        when (eventReaction.reactionType) {
            ReactionType.LIKE.name -> {likeList++}
            ReactionType.LOVE.name -> {loveList++}
        }
    }

    Surface(
        Modifier
            .padding(top = 72.dp)
            .fillMaxSize()
            .verticalScroll(state = scrollState, enabled = true, reverseScrolling = false),
        color = MaterialTheme.colorScheme.background
    ) {
        ConstraintLayout(
            Modifier
                .fillMaxSize()) {
            val (eventImages, dotIndicator, evenDetails, tab,
                commentTab, date) = createRefs()

            Surface(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .constrainAs(eventImages) {
                        top.linkTo(parent.top)
                        centerHorizontallyTo(parent)
                    },
                shape = MaterialTheme.shapes.large
            ) {
                HorizontalPager(
                    state = pageState,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    event?.imageUrlList?.get(pageState.currentPage)
                        ?.let { imageUrl  -> ImageLoader(
                            imageUrl,
                            context,
                            280,
                            screenWidth,
                            R.drawable.event
                        ) }
                }
            }

            Row(
                Modifier.constrainAs(dotIndicator) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(eventImages.bottom, margin = 16.dp)
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pageState.pageCount) { iterations ->
                    val color = if (pageState.currentPage == iterations) {
                        MaterialTheme.colorScheme.primary
                    }else MaterialTheme.colorScheme.surface

                    Box(modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp))
                }
            }

            Surface(
                Modifier.constrainAs(date) {
                    end.linkTo(eventImages.end, margin = 16.dp)
                    top.linkTo(eventImages.top, margin = 16.dp)
                },
                color = MaterialTheme.colorScheme.background,
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onBackground)
                    Text(
                        text = Utils.formatToDDMMYYYY(event?.dateCreated?: LocalDateTime.now().toString()),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = TextUnit(12.0f, TextUnitType.Sp)
                    )
                }
            }

            Surface(
                Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .padding(horizontal = 8.dp)
                    .constrainAs(tab) {
                        top.linkTo(eventImages.bottom, margin = 8.dp)
                        centerHorizontallyTo(parent)
                    },
                shape = MaterialTheme.shapes.small
            ) {
                TabSwitch() {
                    showDetails = it
                }
            }

            if (isLoading == true) {
                CircularIndicator()
            }

            if(showDetails) {
                Surface(
                    Modifier
                        .fillMaxWidth()
                        .constrainAs(evenDetails) {
                            top.linkTo(tab.bottom, margin = 4.dp)
                            centerHorizontallyTo(parent)
                        },
                    color = MaterialTheme.colorScheme.background,
                ) {
                    ViewEventDetails(event, groupViewModel, activityViewModel, navController)
                }
            }
            if (!showDetails) {
                Surface(
                    Modifier
                        .fillMaxWidth()
                        .constrainAs(commentTab) {
                            top.linkTo(tab.bottom, margin = 4.dp)
                            centerHorizontallyTo(parent)
                        },
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Column(
                        Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth(),
                            color = Color.Transparent,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            CommentBox(groupViewModel, activityViewModel, event)
                        }

                        if (event?.eventComments?.isNotEmpty() == true) {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(vertical = 1.dp)
                                    .height(screenHeight.dp),
                                verticalArrangement = Arrangement.Top
                            ) {
                                items(event.eventComments) { comment ->
                                    CommentItem(comment, activityViewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    callback: (Boolean) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val paymentList = activityViewModel.selectedEvent.observeAsState().value?.pendingEvidenceOfPayment
    var showImage by rememberSaveable { mutableStateOf(false) }
    var selectedPayment by rememberSaveable { mutableStateOf<Payment?>(null) }
    ModalBottomSheet(
        onDismissRequest = {
            callback(false)
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
                    PaymentItem(payment, groupViewModel, activityViewModel) { show, selected ->
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
fun ConfirmPaymentDialog(
    selectedPayment: Payment?,
    activityViewModel: ActivityViewModel,
    groupViewModel: GroupViewModel,
    callback: (show: Boolean) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp
    var amountPaid by rememberSaveable { mutableStateOf("") }
    var outstanding by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
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
            if (showReasonInputDialog) {
                Dialog(onDismissRequest = { showReasonInputDialog = false}) {
                    Surface(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ConstraintLayout(
                            Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            val (input, btn) = createRefs()
                            Surface(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp)
                                    .constrainAs(input) {
                                        top.linkTo(parent.top, margin = 4.dp)
                                        centerHorizontallyTo(parent)
                                    },
                                shape = MaterialTheme.shapes.small,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
                            ) {
                                TextField(
                                    value = reason,
                                    onValueChange = { reason = it },
                                    colors = TextFieldDefaults.colors(
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedContainerColor = MaterialTheme.colorScheme.background,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.background
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                )
                            }

                            Surface(
                                Modifier
                                    .size(64.dp)
                                    .padding(8.dp)
                                    .constrainAs(btn) {
                                        bottom.linkTo(parent.bottom, margin = 8.dp)
                                        centerHorizontallyTo(parent)
                                    },
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Proceed",
                                    modifier = Modifier

                                        .clickable {
                                            coroutineScope.launch {
                                                val response =
                                                    activityViewModel.rejectPayment(
                                                        selectedPayment,
                                                        reason
                                                    )
                                                if (response.status) {
                                                    showReasonInputDialog = false
                                                    callback(false)
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            response.message,
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                }
                                            }
                                        }
                                )
                            }

                        }
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

                Surface(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, 4.dp),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                    shape = MaterialTheme.shapes.small
                ) {
                    TextField(
                        value = amountPaid,
                        onValueChange = { amountPaid = it},
                        placeholder = { Text(text = stringResource(id = R.string.amount_paid))},
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                            ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                        )
                }
//                Surface(
//                    Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 16.dp, vertical = 4.dp),
//                    color = Color.Transparent,
//                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
//                    shape = MaterialTheme.shapes.small
//                ) {
//                    TextField(
//                        value = outstanding.toString(),
//                        onValueChange = { outstanding = it},
//                        placeholder = { Text(text = stringResource(id = R.string.amount_paid))},
//                        colors = TextFieldDefaults.colors(
//                            focusedContainerColor = MaterialTheme.colorScheme.background,
//                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
//                            focusedIndicatorColor = Color.Transparent,
//                            unfocusedIndicatorColor = Color.Transparent,
//                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
//                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
//                        ),
//                        keyboardOptions = KeyboardOptions(
//                            keyboardType = KeyboardType.Number
//                        )
//                    )
//                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            showReasonInputDialog = true
                        },
                        Modifier
                            .width(120.dp),
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Text(text = stringResource(id = R.string.reject_payment))
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val response = activityViewModel.confirmPayment(selectedPayment, amountPaid.toDouble(),
                                    0.0, group?.groupId!!)
                                if (response.status) {
                                    callback(false)
                                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                                }else {
                                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        Modifier
                            .width(120.dp),
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentItem(
    payment: Payment,
    groupViewModel: GroupViewModel,
    activityViewModel1: ActivityViewModel,
    callBack: (Boolean, Payment) -> Unit = { _, _ -> }
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable { callBack(true, payment) },
        horizontalAlignment = Alignment.Start
    ) {
        HorizontalDivider()
        Text(
            text = payment.eventTitle,
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
            text = payment.payerEmail,
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun EventDetailTopBar(
    navController: NavController,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    selectedEvent: Event?,
    callback: (Boolean) -> Unit
) {
    //val topBarWidth = LocalConfiguration.current.screenWidthDp - 32
    //val payment = activityViewModel.unapprovedPayments.observeAsState().value
    val isUserAdmin = groupViewModel.isUserAdminLiveData.observeAsState().value
    val context = LocalContext.current
    Surface(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(top = 16.dp, start = 8.dp, end = 8.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.onPrimary,
        shadowElevation = dimensionResource(id = R.dimen.default_elevation)
    ) {
        ConstraintLayout(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            val (navBtn, eventTitle, payments) = createRefs()
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                contentDescription = "",
                modifier = Modifier
                    .clickable {
                        navController.navigateUp()
                    }
                    .constrainAs(navBtn) {
                        start.linkTo(parent.start, margin = 8.dp)
                        centerVerticallyTo(parent)
                    }
            )

            Text(
                text = selectedEvent?.eventTitle?: "",
                Modifier.constrainAs(eventTitle) {
                    centerHorizontallyTo(parent)
                    centerVerticallyTo(parent)
                }
            )
            
            if (isUserAdmin == true) {
                 Box(
                     Modifier
                         .size(32.dp)
                         .clickable {
                             callback(true)
                         }
                         .constrainAs(payments) {
                             end.linkTo(parent.end, margin = 8.dp)
                             centerVerticallyTo(parent)

                         },
                     contentAlignment = Alignment.Center
                 ) {
                     Icon(painter = painterResource(id = R.drawable.wallet),
                         contentDescription = "",
                         tint = MaterialTheme.colorScheme.secondary)
                     if (!selectedEvent?.pendingEvidenceOfPayment.isNullOrEmpty()) {
                         Text(
                             text = selectedEvent?.pendingEvidenceOfPayment?.size.toString(),
                             fontSize = TextUnit(10.0f, TextUnitType.Sp),
                             color = MaterialTheme.colorScheme.onBackground)
                     }
                 }
            }
        }
    }
}

@Composable
fun TabSwitch(switchView: (showDetails: Boolean) -> Unit) {
    val screenWidth = LocalConfiguration.current.screenWidthDp / 2
    var showDetails by rememberSaveable { mutableStateOf(true)}
    ConstraintLayout(
        Modifier.fillMaxSize()
    ) {
        val (detailBg, commentBg, detailsTop, commentTop) = createRefs()
        Text(
            text = stringResource(id = R.string.details),
            Modifier
                .width(screenWidth.dp)
                .fillMaxHeight()
                .padding(top = 4.dp)
                .clickable {
                    showDetails = true
                    switchView(showDetails)
                }
                .constrainAs(detailBg) {
                    start.linkTo(parent.start)
                },
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(id = R.string.view_comments),
            Modifier
                .width(screenWidth.dp)
                .fillMaxHeight()
                .padding(top = 4.dp)
                .clickable {
                    showDetails = false
                    switchView(showDetails)
                }
                .constrainAs(commentBg) {
                    end.linkTo(parent.end)
                },
            textAlign = TextAlign.Center
        )
        if (showDetails) {
            Surface(
                Modifier
                    .width((screenWidth - 24).dp)
                    .height(40.dp)
                    .padding(vertical = 1.dp)
                    .constrainAs(detailsTop) {
                        start.linkTo(parent.start, margin = 1.dp)
                        centerVerticallyTo(parent)
                    },
                shadowElevation = dimensionResource(id = R.dimen.low_elevation),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.background
            ) {
                Text(
                    text = stringResource(id = R.string.details),
                    Modifier
                        .width(screenWidth.dp)
                        .fillMaxHeight()
                        .padding(top = 4.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        if (!showDetails) {
            Surface(
                Modifier
                    .width((screenWidth - 24).dp)
                    .height(40.dp)
                    .padding(vertical = 1.dp)
                    .constrainAs(commentTop) {
                        end.linkTo(parent.end, margin = 1.dp)
                        centerVerticallyTo(parent)
                    },
                shadowElevation = dimensionResource(id = R.dimen.low_elevation),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.background
            ) {
                Text(
                    text = stringResource(id = R.string.view_comments),
                    Modifier
                        .width(screenWidth.dp)
                        .fillMaxHeight()
                        .padding(top = 4.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ViewEventDetails(
    event: Event?,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    navController: NavController
) {
    val isUserAdmin = groupViewModel.isUserAdminLiveData.observeAsState().value
    ConstraintLayout(
        Modifier.fillMaxSize()
    ) {
        val (levyAmount, description, paymentData, compliance, adminActions) = createRefs()

        Surface(
            modifier = Modifier
                .constrainAs(levyAmount) {
                    top.linkTo(parent.top, margin = 4.dp)
                    start.linkTo(parent.start, margin = 4.dp)
                },
            color = MaterialTheme.colorScheme.primary,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "Levy: #${event?.levyAmount}",
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }

        Column(
            modifier = Modifier.constrainAs(paymentData) {
                top.linkTo(parent.top, margin = 2.dp)
                end.linkTo(parent.end, margin = 4.dp)
            },
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "${event?.contributions?.size} paid",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = TextUnit(14.0f, TextUnitType.Sp))

            Text(
                text = "${(groupViewModel.groupDetailLiveData.value?.memberList?.size)?.minus((event?.contributions?.size!!))} unpaid",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
        }

        Column(
            Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .constrainAs(description) {
                    top.linkTo(levyAmount.bottom, margin = 16.dp)
                },
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(id = R.string.description),
                Modifier.paddingFromBaseline(bottom = 8.dp),
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                text = event?.eventDescription!!,
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground,
                //modifier = Modifier.paddingFromBaseline(bottom = 16.dp)
            )

        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(compliance) {
                    top.linkTo(description.bottom, margin = 8.dp)
                    //bottom.linkTo(parent.bottom, margin = 8.dp)
                },
            color = MaterialTheme.colorScheme.background,
        ) {
            Compliance(groupViewModel, activityViewModel, event, navController)
        }
        if (isUserAdmin == true) {
            Surface(
                modifier = Modifier
                    .constrainAs(adminActions) {
                        centerHorizontallyTo(parent)
                        top.linkTo(compliance.bottom, margin = 16.dp)}
            ){
                AdminActions(event, groupViewModel, activityViewModel, navController)
            }
        }
    }
}

@Composable
fun Compliance(groupViewModel: GroupViewModel, activityViewModel: ActivityViewModel, event: Event?, navController: NavController) {
    val hasUserPaid = activityViewModel.hasPaid.observeAsState().value
    var showPaidList by rememberSaveable { mutableStateOf(false) }
    val isUserAdmin = groupViewModel.isUserAdminLiveData.observeAsState().value
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    val sliderWidth = LocalConfiguration.current.screenWidthDp - 140
    val percent = (event?.contributions?.size?.times(100))?.div(group?.memberList?.size!!)

    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(id = R.string.compliance),
            modifier = Modifier.paddingFromBaseline(bottom = 8.dp),
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold
        )

        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Slider(
                value = event?.contributions?.size?.toFloat()?: 0.0f,
                onValueChange = {},
                enabled = false ,
                steps = group?.memberList?.size?.minus(1)!!,
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
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.progress),
                fontSize = TextUnit(12.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground)

            Text(
                text = "${event?.amountRealized} realized",
                fontSize = TextUnit(12.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary)
        }

        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = if (hasUserPaid == true) {
                    "You have paid for this activity" } else "You are yet to pay for this activity",
                color = if (hasUserPaid == true) {
                    Color.Green
                } else {
                    Color.Red
                },
                fontSize = TextUnit(14.0f, TextUnitType.Sp)
            )
        }
        if (hasUserPaid == false) {
            Button(
                onClick = {
                    navController.navigate("payment") {
                        launchSingleTop = true
                    }
                },
                Modifier
                    .fillMaxWidth()
                    .padding(start = 64.dp, end = 64.dp, top = 32.dp)
            ) {
                Text(text = stringResource(id = R.string.pay_now))
            }
        }

        PaidListHeader(groupViewModel, event, showPaidList) {
            showPaidList = it
        }
        if (showPaidList) {
            if (!event?.contributions.isNullOrEmpty()) {
                val memberList  = activityViewModel.paidMembersList.observeAsState().value
                memberList?.forEach { member ->
                    Row(
                        Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = member.fullName,
                            fontSize = TextUnit(14.0f, TextUnitType.Sp)
                        )
                        member.memberPost?.let { post ->
                            Text(text = post,
                                fontSize = TextUnit(14.0f, TextUnitType.Sp))
                        }
                    }
                }
            }else {
                Text(
                    text = stringResource(id = R.string.no_payment),
                    Modifier.padding(start = 16.dp),
                    color = Color.DarkGray,
                    fontSize = TextUnit(14.0f, TextUnitType.Sp))
            }
        }
    }
}

@Composable
fun AdminActions(event: Event?, groupViewModel: GroupViewModel, activityViewModel: ActivityViewModel, navController: NavController) {
    var showCompleteDialog by rememberSaveable { mutableStateOf(false) }
    var action by rememberSaveable { mutableStateOf("") }
    var descriptionText by rememberSaveable { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    Surface(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(
                RoundedCornerShape(
                    bottomEnd = 32.dp,
                    bottomStart = 32.dp
                )
            ),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showCompleteDialog) {
                AdminActionDialog(activityViewModel, event!!, action, descriptionText, navController) {showCompleteDialog = it}
            }

            Text(
                text = stringResource(id = R.string.admin_actions),
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                Modifier
                    .padding(vertical = 16.dp, horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    Modifier.clickable {
                        descriptionText = R.string.confirm_event_completion
                        action = AdminActions.COMPLETE.name
                        showCompleteDialog = true
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.completed_activity),
                        contentDescription = "",
                        Modifier.size(32.dp),
                        tint = Color.Green)
                    Text(text = stringResource(id = R.string.mark_completed),
                        fontSize = TextUnit(14.0f, TextUnitType.Sp),
                        color = MaterialTheme.colorScheme.onBackground)
                }
                Column(
                    Modifier.clickable {
                        descriptionText = R.string.archive_activity_description
                        action = AdminActions.ARCHIVE.name
                        showCompleteDialog = true
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.archive_down_minimlistic),
                        contentDescription = "",
                        Modifier.size(32.dp),
                        tint = Color.Magenta)
                    Text(text = stringResource(id = R.string.archive),
                        fontSize = TextUnit(14.0f, TextUnitType.Sp),
                        color = MaterialTheme.colorScheme.onBackground)
                }
            }

            Row(
                Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    Modifier.clickable {
                        descriptionText = R.string.delete_activity_description
                        action = AdminActions.DELETE.name
                        showCompleteDialog = true
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = "",
                        Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.secondary)
                    Text(text = stringResource(id = R.string.delete_activity),
                        fontSize = TextUnit(14.0f, TextUnitType.Sp),
                        color = MaterialTheme.colorScheme.onBackground)
                }
                Column(
                    Modifier.clickable {
                        coroutineScope.launch {
                            activityViewModel.generateReport(event, context)
                        }
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.analytics),
                        contentDescription = "",
                        Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary)
                    Text(text = stringResource(id = R.string.generate_report),
                        fontSize = TextUnit(14.0f, TextUnitType.Sp),
                        color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }
    }
}

@Composable
fun AdminActionDialog(
    activityViewModel: ActivityViewModel,
    event: Event,
    action: String,
    descriptionText: Int,
    navController: NavController,
    callback: (Boolean) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    Dialog(onDismissRequest = { callback(false) }) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.medium
        ) {
            ConstraintLayout(
                Modifier.padding(8.dp)
            ) {
                val (text, confirm, cancel) = createRefs()
                Text(
                    text = stringResource(id = descriptionText),
                    Modifier
                        .padding(end = 8.dp)
                        .constrainAs(text) {
                            top.linkTo(parent.top, margin = 8.dp)
                            start.linkTo(parent.start, margin = 8.dp)
                        },
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    textAlign = TextAlign.Start
                )
                Row(
                    Modifier
                        .clickable { callback(false) }
                        .constrainAs(cancel) {
                            start.linkTo(parent.start, margin = 16.dp)
                            top.linkTo(text.bottom, margin = 32.dp)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "cancel",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                Row(
                    Modifier
                        .clickable {
                            when (action) {
                                AdminActions.COMPLETE.name -> {
                                    coroutineScope.launch {
                                        val response =
                                            activityViewModel.markActivityCompleted(event)
                                        if (response.status) {
                                            Toast
                                                .makeText(
                                                    context, "Activity completed",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                            navController.navigateUp()
                                        } else {
                                            Toast
                                                .makeText(
                                                    context, response.message,
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }
                                        callback(false)
                                    }
                                }

                                AdminActions.ARCHIVE.name -> {
                                    coroutineScope.launch {
                                        val response = activityViewModel.archiveActivity(event)
                                        if (response.status) {
                                            Toast
                                                .makeText(
                                                    context, "Activity archived",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                            navController.navigateUp()
                                        } else {
                                            Toast
                                                .makeText(
                                                    context, response.message,
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }
                                        callback(false)
                                    }
                                }

                                AdminActions.DELETE.name -> {
                                    coroutineScope.launch {
                                        val response = activityViewModel.deleteActivity(event)
                                        if (response.status) {
                                            Toast
                                                .makeText(
                                                    context, "Activity deleted",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                            navController.navigateUp()
                                        } else {
                                            Toast
                                                .makeText(
                                                    context, response.message,
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }
                                        callback(false)
                                    }
                                }

                                else -> {
                                    callback(false)
                                }
                            }
                        }
                        .constrainAs(confirm) {
                            end.linkTo(parent.end, margin = 16.dp)
                            top.linkTo(text.bottom, margin = 32.dp)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "cancel",
                        tint = Color(LocalContext.current.getColor(R.color.teal_200))
                    )
                }
            }
        }
    }
}

@Composable
fun PaidListHeader(
    groupViewModel: GroupViewModel,
    event: Event?,
    showPaidList: Boolean,
    callback: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clickable { callback(!showPaidList) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.paid_list),
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary
        )
        if (showPaidList) {
            Icon(
                painter = painterResource(id = R.drawable.up_arrow_solid),
                contentDescription = "",
                Modifier
                    .size(16.dp)
                    .clickable { callback(false) },
                tint = MaterialTheme.colorScheme.secondary
            )
        }else {
            Icon(
                painter = painterResource(id = R.drawable.forward_arrow_solid),
                contentDescription = "",
                Modifier
                    .size(16.dp)
                    .clickable { callback(true) },
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun CommentBox(groupViewModel: GroupViewModel, activityViewModel: ActivityViewModel, event: Event?) {
    var commentText by rememberSaveable {  mutableStateOf("")}
    val screenWidth = LocalConfiguration.current.screenWidthDp - 84
    val coroutineScope = rememberCoroutineScope()
    Row(
        Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
        ) {
        TextField(
            value = commentText,
            onValueChange ={ commentText = it },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            placeholder = { Text(text = stringResource(id = R.string.comment)) },
            modifier = Modifier.width(screenWidth.dp)

        )
        IconButton(onClick = { coroutineScope.launch {
            activityViewModel.postComment(commentText, event?.eventId) }
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send reply",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ReactToEvent(likeList: Int, loveList: Int) {
    Row(
        modifier =Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            Modifier
                .height(28.dp)
                .clickable { }
                .padding(horizontal = 2.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.onPrimary
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = painterResource(id = R.drawable.like),
                    contentDescription = "React",
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(text = likeList.toString())
            }
        }

        Surface(
            Modifier
                .height(28.dp)
                .clickable { }
                .padding(horizontal = 2.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.onPrimary
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = painterResource(id = R.drawable.heart),
                    contentDescription = "React",
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .size(24.dp),
                    tint = Color.Red
                )
                Text(text = loveList.toString())
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: EventComment,
    activityViewModel: ActivityViewModel
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp - 8
    var showCommentReplyBox by rememberSaveable { mutableStateOf(false) }
    var commentReply by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier
            .padding(vertical = 1.dp, horizontal = 16.dp)
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        shape = MaterialTheme.shapes.extraSmall
    ) {
        ConstraintLayout {
            val (commentText, replyText, replyBox, replyList, userName, time) = createRefs()
            Text(
                text = "${comment.username}: ",
                fontSize = TextUnit(12.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.constrainAs(userName) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start, margin = 4.dp)
                }
            )
            Text(
                text = comment.comment?: "",
                fontSize = TextUnit(12.0f, TextUnitType.Sp),
                modifier = Modifier
                    .constrainAs(commentText) {
                        start.linkTo(parent.start, margin = 8.dp)
                        top.linkTo(userName.bottom)
                    }
            )
            Text(
                text = LocalDateTime.parse(comment.dateOfComment).format(DateTimeFormatter.ofPattern("HH:mm")),
                fontSize = TextUnit(10.0f, TextUnitType.Sp),
                color = Color.Gray,
                modifier = Modifier.constrainAs(time) {
                    top.linkTo(parent.top, margin = 4.dp)
                    end.linkTo(parent.end, margin = 4.dp)
                }
            )
            if (comment.commentReplies?.isNotEmpty() == true) {
                Surface(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .constrainAs(replyList) {
                            top.linkTo(commentText.bottom)
                            end.linkTo(parent.end, margin = 4.dp)
                        },
                    color = MaterialTheme.colorScheme.background
                ) {
                    CommentReplyList(comment.commentReplies)
                }
            }

            Text(
                text = stringResource(id = R.string.reply),
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .clickable { showCommentReplyBox = true }
                    .constrainAs(replyText) {
                        end.linkTo(parent.end, margin = 4.dp)
                        bottom.linkTo(parent.bottom, margin = 2.dp)
                    }
            )
            if (showCommentReplyBox) {
                Row(
                    modifier = Modifier.constrainAs(replyBox) {
                        top.linkTo(commentText.bottom, margin = 2.dp)
                        start.linkTo(parent.start, margin = 4.dp)
                    }
                ) {
                    Surface(
                        modifier = Modifier.width((screenWidth - 84).dp),
                        shape = MaterialTheme.shapes.medium,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        TextField(
                            value = commentReply,
                            onValueChange = { commentReply = it },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                focusedContainerColor = MaterialTheme.colorScheme.background,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            placeholder = { Text(text = stringResource(id = R.string.reply)) }
                        )
                    }

                    IconButton(onClick = {
                        showCommentReplyBox = false
                        coroutineScope.launch {
                            activityViewModel.postCommentReply(
                                commentReply,
                                comment.commentId!!
                            )
//                            if (newReplyItem != null) {
//                                val newCommentReplyList =
//                                    commentReplyList.toMutableList() // creates a mutable list of the reply list defined above
//                                newCommentReplyList.add(newReplyItem) //adds the new item to the list
//                                commentReplyList =
//                                    newCommentReplyList //reassign the reply list with the updated list
//                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send reply",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CommentReplyList(commentReplyList: List<CommentReply>) {
    val screenWidth = LocalConfiguration.current.screenWidthDp - 64
    Column(
        modifier = Modifier
            .width(screenWidth.dp)
    ) {
        commentReplyList.forEach { reply ->
            ReplyItem(reply)
        }
    }
}

@Composable
fun ReplyItem(reply: CommentReply) {
    ConstraintLayout {
        val (userName, comment, time) = createRefs()

        Text(
            text = "${reply.username}: ",
            fontSize = TextUnit(12.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.constrainAs(userName) {
                top.linkTo(parent.top)
                start.linkTo(parent.start, margin = 4.dp)
            }
        )
        Text(
            text = reply.replyText?: "",
            fontSize = TextUnit(12.0f, TextUnitType.Sp),
            modifier = Modifier
                .constrainAs(comment) {
                    start.linkTo(parent.start, margin = 8.dp)
                    top.linkTo(userName.bottom)
                }
        )
        Text(
            text = LocalDateTime.parse(reply.dateOfReply).format(DateTimeFormatter.ofPattern("HH:mm")),
            fontSize = TextUnit(10.0f, TextUnitType.Sp),
            color = Color.Gray,
            modifier = Modifier.constrainAs(time) {
                top.linkTo(comment.bottom)
                start.linkTo(parent.start, margin = 8.dp)
            }
        )
    }
}
