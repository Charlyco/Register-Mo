package com.register.app.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.dto.BankDetail
import com.register.app.enums.GroupType
import com.register.app.model.Group
import com.register.app.util.CircularIndicator
import com.register.app.util.GenericTopBar
import com.register.app.util.ImageLoader
import com.register.app.util.Utils
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch
import java.io.IOException

@Composable
fun GroupUpdateScreen(navController: NavController, groupViewModel: GroupViewModel) {
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    Scaffold(
        topBar = { GenericTopBar(
            title = stringResource(id = R.string.update_group),
            navController = navController
        )}
    ) {
        if (group != null) {
            GroupUpdateUi(Modifier.padding(it), group, navController, groupViewModel)
        }
    }
}

@Composable
fun GroupUpdateUi(
    modifier: Modifier,
    group: Group,
    navController: NavController,
    groupViewModel: GroupViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val loadingState = groupViewModel.loadingState.observeAsState().value
    val scrollState = rememberScrollState(initial = 0)
    var groupName by rememberSaveable { mutableStateOf(group.groupName)}
    var description by rememberSaveable { mutableStateOf(group.groupDescription) }
    var address by rememberSaveable { mutableStateOf(group.address) }
    var phone by rememberSaveable { mutableStateOf(group.phoneNumber) }
    var email by rememberSaveable { mutableStateOf(group.groupEmail) }
    var logoUrl by rememberSaveable { mutableStateOf(group.logoUrl) }
    var groupType by rememberSaveable { mutableStateOf(group.groupType) }
    var showBankUpdateDialog by rememberSaveable { mutableStateOf(false) }
    val imageMimeTypes = listOf("image/jpeg", "image/png")
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                coroutineScope.launch {
                    try {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        if (inputStream != null) {
                            val mimeType = context.contentResolver.getType(uri)
                            logoUrl = groupViewModel.uploadGroupLogo(inputStream, mimeType, Utils.getFileNameFromUri(context.contentResolver, uri))
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

    if (showBankUpdateDialog) {
        BandDetailUpdate(group, groupViewModel) {showBankUpdateDialog = it}
    }
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .padding(top = 64.dp)
            .verticalScroll(scrollState)
    ) {
        val (logo, details, saveBtn, bankDetails, progress) = createRefs()
        
        Surface(
            Modifier
                .clickable {
                    coroutineScope.launch { groupViewModel.fetchBankDetails(group.groupId) }
                    showBankUpdateDialog = true
                }
                .constrainAs(bankDetails) {
                    top.linkTo(parent.top, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                },
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary)
        ) {
            Text(
                text = stringResource(id = R.string.update_bank_details),
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(4.dp)
                )
        }
        Box(
            Modifier
                .size(200.dp)
                .constrainAs(logo) {
                    top.linkTo(parent.top, margin = 32.dp)
                    centerHorizontallyTo(parent)
                },
            contentAlignment = Alignment.BottomEnd
        ) {
            Surface(
                Modifier.clip(CircleShape)
            ) {
                ImageLoader(
                    imageUrl = group.logoUrl?: "",
                    context = context,
                    height = 200,
                    width = 200,
                    placeHolder = R.drawable.download
                )
            }
            Icon(
                imageVector = Icons.Default.CameraEnhance,
                contentDescription = "",
                modifier = Modifier
                    .clickable {
                        val mimeType = imageMimeTypes.joinToString("image/jpg,image/png")
                        filePicker.launch("image/*")
                    }
                    .size(40.dp))
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .constrainAs(details) {
                    centerHorizontallyTo(parent)
                    top.linkTo(logo.bottom, margin = 24.dp)
                },
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(id = R.string.group_name),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold
            )
            Surface(
                Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                border = BorderStroke(1.dp, Color.Gray),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background
            ) {
                TextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Text(
                text = stringResource(id = R.string.group_description),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Surface(
                Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                border = BorderStroke(1.dp, Color.Gray),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background
            ) {
                TextField(
                    value = description ?: "",
                    onValueChange = { description = it },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Text(
                text = stringResource(id = R.string.address),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Surface(
                Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                border = BorderStroke(1.dp, Color.Gray),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background
            ) {
                TextField(
                    value = address ?: "",
                    onValueChange = { address = it },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Text(
                text = stringResource(id = R.string.phone),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Surface(
                Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                border = BorderStroke(1.dp, Color.Gray),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background
            ) {
                TextField(
                    value = phone ?: "",
                    onValueChange = { phone = it },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Text(
                text = stringResource(id = R.string.email),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Surface(
                Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                border = BorderStroke(1.dp, Color.Gray),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background
            ) {
                TextField(
                    value = email ?: "",
                    onValueChange = { email = it },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = groupType == GroupType.OPEN.name,
                    onCheckedChange = { groupType = GroupType.OPEN.name}
                )
                Text(text = stringResource(id = R.string.open))

                Checkbox(
                    checked = groupType == GroupType.CLOSED.name,
                    onCheckedChange = { groupType = GroupType.CLOSED.name}
                )
                Text(text = stringResource(id = R.string.closed))
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        val response = groupViewModel.saveGroupUpdate(
                            group.groupId,
                            groupName,
                            description,
                            address,
                            phone,
                            email,
                            logoUrl,
                            groupType
                        )
                        if (response?.status == true) {
                            Toast.makeText(
                                context,
                                "Group updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(context, "Failed to update group", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color(context.getColor(R.color.background_color))
                )
            ) {
                Text(text = stringResource(id = R.string.save))
            }
        }

        if (loadingState == true) {
            Surface(
                Modifier.constrainAs(progress) {
                    centerHorizontallyTo(parent)
                    centerVerticallyTo(parent)
                },
                color = Color.Transparent
            ) {
                CircularIndicator()
            }
        }
    }
}

@Composable
fun BandDetailUpdate(
    group: Group,
    groupViewModel: GroupViewModel,
    onDismiss: (Boolean) -> Unit
) {
    val bankDetails = groupViewModel.bankDetails.observeAsState().value
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp
    var accountName by rememberSaveable { mutableStateOf(bankDetails?.accountName?: "") }
    var bankName by rememberSaveable { mutableStateOf(bankDetails?.bankName?: "") }
    var accountNumber by rememberSaveable { mutableStateOf(bankDetails?.accountNumber?: "") }

    Dialog(
        onDismissRequest = { onDismiss(false) }) {
        Surface(
            Modifier
                .width(screenWidth.dp)
                .padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(id = R.string.bank_details),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = TextUnit(16.0f, TextUnitType.Sp),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )

                Surface(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 2.dp, end = 2.dp, bottom = 4.dp)
                        .height(55.dp),
                    border = BorderStroke(1.dp, Color.Gray),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.background
                ) {
                    TextField(
                        value = bankName,
                        onValueChange = { bankName = it },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        label = { Text(text = stringResource(id = R.string.bank_name)) }
                    )
                }

                Surface(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 2.dp, end = 2.dp, bottom = 4.dp)
                        .height(55.dp),
                    border = BorderStroke(1.dp, Color.Gray),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.background
                ) {
                    TextField(
                        value = accountNumber,
                        onValueChange = { accountNumber = it },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        label = { Text(text = stringResource(id = R.string.account_number))},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Surface(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 2.dp, end = 2.dp, bottom = 4.dp)
                        .height(55.dp),
                    border = BorderStroke(1.dp, Color.Gray),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.background
                ) {
                    TextField(
                        value = accountName,
                        onValueChange = { accountName = it },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        label = { Text(text = stringResource(id = R.string.account_holder))}
                    )
                }

                Button(
                    onClick = {
                        if (accountNumber.isEmpty()) {
                            Toast.makeText(context, "Account number cannot be blank", Toast.LENGTH_SHORT).show()
                        }else if (accountName.isEmpty()) {
                            Toast.makeText(context, "Account name cannot be blank", Toast.LENGTH_SHORT).show()
                        }else if (bankName.isEmpty()) {
                            Toast.makeText(context, "Bank name cannot be blank", Toast.LENGTH_SHORT).show()
                        }else {
                            coroutineScope.launch {
                                val bankDetail = BankDetail(accountName, accountNumber, bankName)
                                val response = groupViewModel.updateBankDetail(bankDetail, group.groupId)
                                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                                if  (response.status) { onDismiss(false) }
                            }
                        }
                    },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color(context.getColor(R.color.background_color))
                    )
                ) {
                    Text(text = stringResource(id = R.string.save))
                }
            }
        }
    }
}
