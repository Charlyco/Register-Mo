package com.register.app.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.dto.SpecialLevy
import com.register.app.enums.PaymentMethod
import com.register.app.util.CircularIndicator
import com.register.app.util.GenericTopBar
import com.register.app.util.ImageLoader
import com.register.app.util.Utils.ImageSourceChooserDialog
import com.register.app.util.Utils.getFileNameFromUri
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch
import java.io.IOException

@Composable
fun PaySpecialLevy(
    activityViewModel: ActivityViewModel,
    groupViewModel: GroupViewModel,
    navController: NavController,
    cameraActivityResult: ActivityResultLauncher<Void?>) {

    val specialLevy = activityViewModel.selectedSpecialLevy.observeAsState().value
    Scaffold(
        topBar = { GenericTopBar(title = specialLevy?.levyTitle!!, navController = navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) {
        PaySpecialLevyUi(Modifier.padding(it), specialLevy, activityViewModel, groupViewModel, cameraActivityResult)
    }
}

@Composable
fun PaySpecialLevyUi(
    modifier: Modifier,
    specialLevy: SpecialLevy?,
    activityViewModel: ActivityViewModel,
    groupViewModel: GroupViewModel,
    cameraActivityResult: ActivityResultLauncher<Void?>
) {
    val fileName = activityViewModel.fileName.observeAsState().value
    val imageUrl = activityViewModel.paymentEvidence.observeAsState().value
    var showBankDetails by rememberSaveable { mutableStateOf(false) }
    val isLoading = activityViewModel.loadingState.observeAsState().value
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var modeOfPayment by rememberSaveable { mutableStateOf("") }
    var amountPaid by rememberSaveable { mutableStateOf("") }
    var showImageSourceChooserDialog by rememberSaveable { mutableStateOf(false) }
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                coroutineScope.launch {
                    try {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        if (inputStream != null) {
                            val mimeType = context.contentResolver.getType(uri)
                            activityViewModel.uploadEvidenceOfPayment(inputStream, mimeType, getFileNameFromUri(context.contentResolver, uri))
                        } else {
                            // Handle error
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        // Handle error
                    }
                }
            }
        }
    )

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        Surface(
            Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        bottomStart = 32.dp,
                        bottomEnd = 32.dp
                    )
                ),
        ) {
            Column {
                Text(
                    text = specialLevy?.levyDescription?: "",
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    textAlign = TextAlign.Justify
                )

                Text(
                    text = stringResource(id = R.string.payment_hint),
                    Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                    textAlign = TextAlign.Justify,
                    fontSize = TextUnit(14.0f, TextUnitType.Sp)
                )
            }
        }

        Text(
            text = stringResource(id = R.string.view_bank_detail),
            Modifier
                .clickable {
                    groupViewModel.getBankDetails()
                    showBankDetails = true
                }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.secondary
        )
        if (isLoading == true) {
            CircularIndicator()
        }

        Column(
            Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = modeOfPayment == PaymentMethod.CASH.name,
                    onCheckedChange = { modeOfPayment = PaymentMethod.CASH.name }
                )
                Text(
                    text = stringResource(id = R.string.cash_payment),
                    Modifier.padding(start = 8.dp),
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Row(
                Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = modeOfPayment == PaymentMethod.CARD_PAYMENT.name,
                    onCheckedChange = { modeOfPayment = PaymentMethod.CARD_PAYMENT.name }
                )
                Text(
                    text = stringResource(id = R.string.card_payment),
                    Modifier.padding(start = 8.dp),
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Row(
                Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = modeOfPayment == PaymentMethod.BANK_TRANSFER.name,
                    onCheckedChange = { modeOfPayment = PaymentMethod.BANK_TRANSFER.name }
                )
                Text(
                    text = stringResource(id = R.string.bank_transfer),
                    Modifier.padding(start = 8.dp),
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Row(
                Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = modeOfPayment == PaymentMethod.POS.name,
                    onCheckedChange = { modeOfPayment = PaymentMethod.POS.name }
                )
                Text(
                    text = stringResource(id = R.string.pos_payment),
                    Modifier.padding(start = 8.dp),
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary)
        ) {
            TextField(
                value = amountPaid,
                onValueChange = { amountPaid = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.text_field_height)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = { Text(text = stringResource(id = R.string.amount_paid)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            if (imageUrl != null) {
                Box(
                    contentAlignment = Alignment.TopEnd
                ) {
                    ImageLoader(
                        imageUrl = imageUrl?: "",
                        context = context,
                        height = 120,
                        width = 120,
                        placeHolder = R.drawable.placeholder
                    )
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                activityViewModel.deleteEvidence()
                            },
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Button(
                onClick = {
                    showImageSourceChooserDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
            ) {
                Text(
                    text = stringResource(id = R.string.browse),
                    Modifier.padding(2.dp)
                )
            }
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    if (modeOfPayment != PaymentMethod.CASH.name && fileName == null) {
                        Toast.makeText(context, "Please select a payment method", Toast.LENGTH_SHORT).show()
                    }else if(modeOfPayment == PaymentMethod.CASH.name && amountPaid.isEmpty()) {
                        Toast.makeText(context, "Please enter amount paid", Toast.LENGTH_SHORT).show()
                    }else {
                        val response = activityViewModel.paySpecialLevy(specialLevy, modeOfPayment, amountPaid)
                        if (response.status) {
                            Toast.makeText(context, "Payment submitted", Toast.LENGTH_SHORT).show()
                        }else {
                            Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                      },

            Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp, horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = dimensionResource(id = R.dimen.low_elevation)
            )
        ) {
            Text(text = stringResource(id = R.string.submit))
        }

        if (showBankDetails) {
            BankDetailDialog(
                onDismiss = { showBankDetails = false},
                onDoneClick = { showBankDetails = false },
                groupViewModel = groupViewModel
            )
        }

        if (showImageSourceChooserDialog) {
            ImageSourceChooserDialog(filePicker, cameraActivityResult) { showImageSourceChooserDialog = it }
        }
    }
}
