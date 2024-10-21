package com.register.app.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.util.CircularIndicator
import com.register.app.util.ImageLoader
import com.register.app.util.Utils.ImageSourceChooserDialog
import com.register.app.util.Utils.getFileNameFromUri
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch
import java.io.IOException

@Composable
fun BulkPayment(
    navController: NavController,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    totalAmount: Double,
    cameraActivityResult: ActivityResultLauncher<Void?>
) {
    val fileName = activityViewModel.fileName.observeAsState().value
    val imageUrl = activityViewModel.paymentEvidence.observeAsState().value
    val loadingState = activityViewModel.loadingState.observeAsState().value
    var showBankDetails by rememberSaveable { mutableStateOf(false) }
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    val membershipId = groupViewModel.membershipId.observeAsState().value
    var showImageSourceChooserDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
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
    Surface(
        Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BulkPaymentTopBarDesign(navController)

            Text(
                text = stringResource(id = R.string.payment_hint),
                Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp),
                textAlign = TextAlign.Start,
                fontSize = TextUnit(14.0f, TextUnitType.Sp)
            )

            Text(
                text = "Total amount to pay: $totalAmount",
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                textAlign = TextAlign.Start,
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.secondary
                )

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
            if (loadingState == true) {
                CircularIndicator()
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (imageUrl != null) {
                    ImageLoader(
                        imageUrl = imageUrl?: "",
                        context = context,
                        height = 120,
                        width = 120,
                        placeHolder = R.drawable.placeholder
                    )
                }

                Button(
                    onClick = {
                        showImageSourceChooserDialog = true},
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
                        val response = activityViewModel.submitBulkPaymentEvidence(group?.groupName!!, group.groupId, membershipId!!, totalAmount)
                        if (response.status) {
                            Toast.makeText(context, "Payment submitted", Toast.LENGTH_SHORT).show()
                            navController.navigateUp()
                        } else {
                            Toast.makeText(context, "Error submitting payment", Toast.LENGTH_SHORT).show()
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
}

@Composable
fun BulkPaymentTopBarDesign(navController: NavController) {
    Surface(
        Modifier
            //.padding(top = 2.dp, start = 2.dp, end = 2.dp)
            .fillMaxWidth()
            .height(64.dp)
            .clip(
                RoundedCornerShape(
                    bottomEnd = 32.dp,
                    bottomStart = 32.dp
                )
            ),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = dimensionResource(id = R.dimen.default_elevation),
        tonalElevation = dimensionResource(id = R.dimen.default_elevation)
    ) {
        ConstraintLayout {
            val (navBtn, title) = createRefs()

            Surface(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable {
                        navController.navigateUp()
                    }
                    .constrainAs(navBtn) {
                        start.linkTo(parent.start, margin = 8.dp)
                        centerVerticallyTo(parent)
                    },
                color = MaterialTheme.colorScheme.background,
                shadowElevation = dimensionResource(id = R.dimen.default_elevation)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = stringResource(id = R.string.upload_payment),
                Modifier.constrainAs(title) {
                    centerVerticallyTo(parent)
                    centerHorizontallyTo(parent)
                },
                fontSize = TextUnit(16.0f, TextUnitType.Sp)
            )
        }
    }
}
