package com.register.app.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.dto.Payment
import com.register.app.dto.SpecialLevy
import com.register.app.model.Member
import com.register.app.util.GenericTopBar
import com.register.app.util.ImageLoader
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch

@Composable
fun SpecialLevyDetail(
    activityViewModel: ActivityViewModel,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val specialLevy = activityViewModel.selectedSpecialLevy.observeAsState().value
    Scaffold(
        topBar = { GenericTopBar(title = specialLevy?.levyTitle!!, navController = navController) }
    ) {
        SpecialLevyDetailScreen(it, specialLevy, groupViewModel, activityViewModel, authViewModel, navController)
    }
}

@Composable
fun SpecialLevyDetailScreen(
    paddingValues: PaddingValues,
    specialLevy: SpecialLevy?,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    var payerDetails by remember{ mutableStateOf<Member?>( null ) }
    LaunchedEffect(key1 = specialLevy) {
       payerDetails = authViewModel.getMemberDetails(specialLevy?.payeeEmail!!)
    }
    var paymentToConfirm by remember { mutableStateOf<Payment?>(null) }
    var showRespondDialog by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = payerDetails?.fullName?: "",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 16.dp),
            textAlign = TextAlign.Start,
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = stringResource(id = R.string.levy_description),
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp),
            fontSize = TextUnit(14.0f, TextUnitType.Sp)
        )
        Text(
            text = specialLevy?.levyDescription?:"",
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp),
            fontSize = TextUnit(14.0f, TextUnitType.Sp)
        )

        if (specialLevy?.pendingPaymentEvidence?.isNotEmpty() == true) {
            PendingPaymentEvidence(specialLevy) { shouldShow, payment ->
                showRespondDialog = shouldShow
                paymentToConfirm = payment
            }
        }

        if (showRespondDialog) {
            ConfirmSpecialLevyPaymentDialog(paymentToConfirm, activityViewModel, groupViewModel, navController) {showRespondDialog = it}
        }
    }
}

@Composable
fun ConfirmSpecialLevyPaymentDialog(
    paymentToConfirm: Payment?,
    activityViewModel: ActivityViewModel,
    groupViewModel: GroupViewModel,
    navController: NavController,
    onDismiss: (Boolean) -> Unit
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
        onDismissRequest = { onDismiss(false)}) {
        Surface(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState(initial = 0)),
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.small
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
                            val (desc, input, btn) = createRefs()
                            Text(
                                text = stringResource(id = R.string.decline_reason),
                                modifier = Modifier.constrainAs(desc) {
                                    top.linkTo(parent.top, margin = 8.dp)
                                    centerHorizontallyTo(parent)
                                })
                            Surface(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp)
                                    .constrainAs(input) {
                                        top.linkTo(desc.bottom, margin = 8.dp)
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
                                                    activityViewModel.rejectSpecialLevyPayment(
                                                        paymentToConfirm,
                                                        reason
                                                    )
                                                if (response.status) {
                                                    navController.navigateUp()
                                                    showReasonInputDialog = false
                                                    onDismiss(false)
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
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                if (!paymentToConfirm?.imageUrl.isNullOrEmpty()) {
                    ImageLoader(
                        paymentToConfirm?.imageUrl ?: "",
                        LocalContext.current,
                        screenHeight - 180,
                        screenWidth - 16,
                        R.drawable.placeholder_doc
                    )
                }
                Text(
                    text = paymentToConfirm?.groupName!!,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = TextUnit(16.0f, TextUnitType.Sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, top = 16.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Title of activity: ${paymentToConfirm.eventTitle}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = TextUnit(16.0f, TextUnitType.Sp),
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                )

                Text(
                    text = "Payer: ${paymentToConfirm.payerFullName}",
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    modifier = Modifier.padding(start = 8.dp)
                )

                Text(
                    text = "Payment Method: ${paymentToConfirm.modeOfPayment}",
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    modifier = Modifier.padding(start = 8.dp)
                )

                Text(
                    text = "Levy amount: ${paymentToConfirm.amountPaid}",
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    modifier = Modifier.padding(start = 8.dp)
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
                        placeholder = { androidx.compose.material3.Text(text = stringResource(id = R.string.amount_paid)) },
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
                                val response = activityViewModel.confirmSpecialLevyPayment(paymentToConfirm, amountPaid.toDouble(),
                                    0.0, group?.groupId!!)
                                if (response.status) {
                                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                                    navController.navigateUp()
                                    onDismiss(false)
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
fun PendingPaymentEvidence(
    specialLevy: SpecialLevy,
    onRespondButtonClick: (Boolean, Payment) -> Unit
) {
    val context = LocalContext.current
    Column(
        Modifier.fillMaxWidth()
    ) {
        specialLevy.pendingPaymentEvidence?.forEach { payment ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                shadowElevation = dimensionResource(id = R.dimen.low_elevation),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ImageLoader(
                        imageUrl = payment.imageUrl?: "",
                        context = context,
                        height = 200,
                        width = 140,
                        placeHolder = R.drawable.placeholder)
                    //VerticalDivider(Modifier.heightIn(200.dp))
                    Column(
                        Modifier.padding(start = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Amount paid: ${payment.amountPaid}",
                            fontSize = TextUnit(14.0f, TextUnitType.Sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        )
                        Text(
                            text = "Method of payment: ${payment.modeOfPayment}",
                            fontSize = TextUnit(14.0f, TextUnitType.Sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                            )
                        Button(
                            onClick = {
                                onRespondButtonClick(
                                    true,
                                    payment
                                )
                            },
                            modifier = Modifier
                                .width(120.dp)
                                .padding(top = 24.dp),

                            ) {
                            Text(text = stringResource(id = R.string.respond))
                        }
                    }
                }
            }
        }
    }
}
