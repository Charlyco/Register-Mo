package com.register.app.screens

import android.widget.Toast
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.register.app.dto.Payment
import com.register.app.enums.PaymentMethod
import com.register.app.model.Member
import com.register.app.util.DateFormatter
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.GroupViewModel

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
        onDismissRequest = { /*TODO*/ },
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
    var amountPaid by rememberSaveable { mutableStateOf("0.0") }
    var outstanding by rememberSaveable { mutableStateOf("0.0") }
    val coroutineScope = rememberCoroutineScope()
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    val context = LocalContext.current
    Dialog(
        onDismissRequest = { callback(false)}) {
        Surface(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState(initial = 0)),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImageLoader(
                    selectedPayment?.imageUrl ?: "",
                    LocalContext.current,
                    screenHeight - 164,
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
                            )
                        )
                }
                Surface(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                    shape = MaterialTheme.shapes.small
                ) {
                    TextField(
                        value = outstanding,
                        onValueChange = { outstanding = it},
                        placeholder = { Text(text = stringResource(id = R.string.amount_paid))},
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val response = activityViewModel.confirmPayment(selectedPayment, amountPaid,
                                outstanding, group?.groupId!!, PaymentMethod.BANK_TRANSFER.name)
                            if (response.status) {
                                callback(false)
                                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                              },
                    Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .width(screenWidth.dp - 32.dp),
                    shape = MaterialTheme.shapes.small,
                    ) {
                    Text(text = stringResource(id = R.string.confirm))
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
    val context = LocalContext.current
    Surface(
        Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
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
                text = selectedEvent?.eventTitle!!,
                Modifier.constrainAs(eventTitle) {
                    centerHorizontallyTo(parent)
                    centerVerticallyTo(parent)
                }
            )
            
            if (groupViewModel.isUserAdmin()) {
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
                     if (!selectedEvent.pendingEvidenceOfPayment.isNullOrEmpty()) {
                         Text(
                             text = selectedEvent.pendingEvidenceOfPayment.size.toString(),
                             fontSize = TextUnit(10.0f, TextUnitType.Sp),
                             color = MaterialTheme.colorScheme.onBackground)
                     }
                 }
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
    val commentList = groupViewModel.eventCommentLiveData.observeAsState().value
    val pageState = rememberPagerState(pageCount = { event?.imageUrlList?.size?: 0} )
    var showDetails by rememberSaveable { mutableStateOf(true)}
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp - 64
    val scrollState = rememberScrollState(initial = 0)
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
                commentTab, react) = createRefs()

            HorizontalPager(
                state = pageState,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .constrainAs(eventImages) {
                        top.linkTo(parent.top)
                        centerHorizontallyTo(parent)
                    }
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
                Modifier.constrainAs(react) {
                    end.linkTo(eventImages.end, margin = 16.dp)
                    top.linkTo(eventImages.top, margin = 16.dp)
                },
                color = Color.Transparent
            ) {
                ReactToEvent(likeList, loveList)
            }

            Surface(
                Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .padding(horizontal = 16.dp)
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
                    ViewEventDetails(event, dataStoreManager, authViewModel, groupViewModel, activityViewModel, navController)
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
                          border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                          shape = MaterialTheme.shapes.medium
                      ) {
                          CommentBox(groupViewModel, activityViewModel, event)
                      }

                      if (commentList?.isNotEmpty() == true) {
                          LazyColumn(
                              modifier = Modifier
                                  .padding(vertical = 1.dp)
                                  .height(screenHeight.dp),
                              verticalArrangement = Arrangement.Top
                          ) {
                              items(commentList) { comment ->
                                  CommentItem(comment, groupViewModel, activityViewModel)
                              }
                          }
                      }
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
    dataStoreManager: DataStoreManager,
    authViewModel: AuthViewModel,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    val hasUserPaid = groupViewModel.hasPaid.observeAsState().value
    var showPaidList by rememberSaveable { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize()
    ) {
        Column(
            Modifier
                .padding(horizontal = 16.dp)
        ) {
            HorizontalDivider(
                Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = stringResource(id = R.string.description),
                Modifier.paddingFromBaseline(bottom = 16.dp),
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                text = event?.eventDescription!!,
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground
            )

            HorizontalDivider(
                Modifier.padding(vertical = 8.dp)
            )
        }

        Text(
            text = stringResource(id = R.string.other_details),
            Modifier
                .padding(start = 16.dp)
                .paddingFromBaseline(bottom = 16.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = TextUnit(16.0f, TextUnitType.Sp))

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Group: ",
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
            Text(text = event?.groupName!!,
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Activity created by:",
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
            Text(event?.eventCreator!!,
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Created at:",
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
            Text(text = DateFormatter.formatDateTime(event?.dateCreated!!),
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Levy amount:",
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
            Text(text = event?.levyAmount.toString(),
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "No. that have paid:",
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
            Text(text = event?.contributions?.size.toString(),
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Amount realized so far:",
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
            Text(text = event?.amountRealized.toString(),
                fontSize = TextUnit(14.0f, TextUnitType.Sp))
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
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
        HorizontalDivider(
            Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
        )

        ComplianceRate(event, groupViewModel)
        PaidListHeader(groupViewModel, event, showPaidList) {
            showPaidList = it
        }
        if (showPaidList) {
            if (!event?.contributions.isNullOrEmpty()) {
                event?.contributions?.forEach {
                    val member: Member? = authViewModel.fetchMemberDetailsByEmail(it.memberEmail)
                    if (member != null) {
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
                }
            }else {
                Text(
                    text = stringResource(id = R.string.no_payment),
                    Modifier.padding(start = 16.dp),
                    color = Color.DarkGray,
                    fontSize = TextUnit(14.0f, TextUnitType.Sp))
            }
        }
        HorizontalDivider(Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
        if (groupViewModel.isUserAdmin()) {
            AdminActions(event, groupViewModel, activityViewModel, navController)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ComplianceRate(event: Event?, groupViewModel: GroupViewModel) {
    val context = LocalContext.current
    val complianceRate = groupViewModel.getComplianceRate(event?.contributions?.size, event?.groupId)
    val pieChatData = PieChartData(slices = listOf(
        PieChartData.Slice("Complied", complianceRate.contributionSize.toFloat(), Color(context.getColor(R.color.teal_200))),
        PieChartData.Slice("Not Complies", (complianceRate.groupSize - complianceRate.contributionSize).toFloat(), Color(context.getColor(R.color.app_orange)))
    ), plotType = PlotType.Donut
    )
    val pieChartConfig = PieChartConfig(
        sliceLabelTextColor = MaterialTheme.colorScheme.onBackground,
        showSliceLabels = true,
        labelFontSize = TextUnit(24.0f, TextUnitType.Sp),
        labelColor = MaterialTheme.colorScheme.onBackground,
        strokeWidth = 42f,
        activeSliceAlpha = .9f,
        labelVisible = true,
        isAnimationEnable = true,
        chartPadding = 16,
        backgroundColor = MaterialTheme.colorScheme.background
        )
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.compliance),
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(start = 16.dp)
                .fillMaxWidth()
        )
        Text(text = "${complianceRate.contributionSize} payments out of ${complianceRate.groupSize} members")
        DonutPieChart(modifier = Modifier.size(200.dp), pieChartData = pieChatData, pieChartConfig = pieChartConfig)
    }
}

@Composable
fun AdminActions(event: Event?, groupViewModel: GroupViewModel, activityViewModel: ActivityViewModel, navController: NavController) {
    Surface(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
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
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                    Modifier.clickable {  },
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
                    Modifier.clickable {  },
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
                    Modifier.clickable {  },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete_activity),
                        contentDescription = "",
                        Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.secondary)
                    Text(text = stringResource(id = R.string.delete_activity),
                        fontSize = TextUnit(14.0f, TextUnitType.Sp),
                        color = MaterialTheme.colorScheme.onBackground)
                }
                Column(
                    Modifier.clickable {  },
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
fun PaidListHeader(
    groupViewModel: GroupViewModel,
    event: Event?,
    showPaidList: Boolean,
    callback: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clickable { callback(!showPaidList) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.paid_list),
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.tertiary
        )
        if (showPaidList) {
            Icon(
                painter = painterResource(id = R.drawable.up_arrow_solid),
                contentDescription = "",
                Modifier
                    .size(16.dp)
                    .clickable { callback(false) },
                tint = MaterialTheme.colorScheme.tertiary
            )
        }else {
            Icon(
                painter = painterResource(id = R.drawable.forward_arrow_solid),
                contentDescription = "",
                Modifier
                    .size(16.dp)
                    .clickable { callback(true) },
                tint = MaterialTheme.colorScheme.tertiary
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
            val newComment = activityViewModel.postComment(commentText, event?.eventId) }
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
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp - 8
    var showCommentReplyBox by rememberSaveable { mutableStateOf(false) }
    var commentReply by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var commentReplyList by rememberSaveable { mutableStateOf(listOf<CommentReply>()) }
    Surface(
        modifier = Modifier
            .padding(vertical = 1.dp)
            .width(screenWidth.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.extraSmall
    ) {
        ConstraintLayout {
            val (commentText, replyText, replyBox, replyList, userName, time) = createRefs()
            Text(
                text = "${comment.username}: ",
                fontSize = TextUnit(10.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.constrainAs(userName) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start, margin = 4.dp)
                }
            )
            Text(
                text = comment.comment,
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                modifier = Modifier
                    .constrainAs(commentText) {
                        start.linkTo(parent.start, margin = 8.dp)
                        top.linkTo(userName.bottom, margin = 2.dp)
                    }
            )
            Text(
                text = comment.dateOfComment,
                fontSize = TextUnit(10.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.constrainAs(time) {
                    top.linkTo(parent.top, margin = 4.dp)
                    end.linkTo(parent.end, margin = 4.dp)
                }
            )
            if (commentReplyList.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .constrainAs(replyList) {
                            top.linkTo(userName.bottom)
                            end.linkTo(parent.end, margin = 4.dp)
                        }
                ) {
                    CommentReplyList(commentReplyList)
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
                    TextField(
                        value = commentReply,
                        onValueChange = { commentReply = it },
                        modifier = Modifier.width((screenWidth - 84).dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedContainerColor = MaterialTheme.colorScheme.background
                        ),
                        placeholder = { Text(text = stringResource(id = R.string.reply)) }
                    )
                    IconButton(onClick = {
                        coroutineScope.launch {
                            val newReplyItem = activityViewModel.postCommentReply(
                                commentReply,
                                comment.commentId
                            )
                            if (newReplyItem != null) {
                                val newCommentReplyList =
                                    commentReplyList.toMutableList() // creates a mutable list of the reply list defined above
                                newCommentReplyList.add(newReplyItem) //adds the new item to the list
                                commentReplyList =
                                    newCommentReplyList //reassign the reply list with the updated list
                            }
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
    val screenHeight = LocalConfiguration.current.screenHeightDp - 64
    LazyColumn(
        modifier = Modifier.height(240.dp)
    ) {
        items(commentReplyList) { reply ->
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
            fontSize = TextUnit(10.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.constrainAs(userName) {
                top.linkTo(parent.top)
                start.linkTo(parent.start, margin = 4.dp)
            }
        )
        Text(
            text = reply.reply,
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            modifier = Modifier
                .constrainAs(comment) {
                    start.linkTo(parent.start, margin = 8.dp)
                    top.linkTo(userName.bottom, margin = 2.dp)
                }
        )
        Text(
            text = reply.dateOfComment,
            fontSize = TextUnit(10.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.constrainAs(time) {
                top.linkTo(comment.bottom)
                end.linkTo(parent.end, margin = 4.dp)
            }
        )
    }
}
