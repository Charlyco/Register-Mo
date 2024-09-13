package com.register.app.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.util.CircularIndicator
import com.register.app.util.EVENT_DETAIL
import com.register.app.util.GenericTopBar
import com.register.app.util.Utils.getFileNameFromUri
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream

@Composable
fun BatchUploadScreen(
    activityViewModel: ActivityViewModel,
    groupViewModel: GroupViewModel,
    navController: NavController
) {
    Scaffold(
        topBar = { GenericTopBar(title = stringResource(id = R.string.batch_upload), navController = navController) }
    ) {
        BatchUploadUi(Modifier.padding(it), activityViewModel, groupViewModel, navController)
    }
}

@Composable
fun BatchUploadUi(
    modifier: Modifier,
    activityViewModel: ActivityViewModel,
    groupViewModel: GroupViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    val isLoading = activityViewModel.loadingState.observeAsState().value
    var fileName by rememberSaveable { mutableStateOf<String?>("") }
    var inputStream by remember { mutableStateOf<InputStream?>(null) }
    var mimeType by remember { mutableStateOf<String?>("") }
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                coroutineScope.launch {
                    try {
                        inputStream = context.contentResolver.openInputStream(uri)
                        if (inputStream != null) {
                            mimeType = context.contentResolver.getType(uri)
                            fileName = getFileNameFromUri(context.contentResolver, uri)
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
        Modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.batch_upload_desc),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            textAlign = TextAlign.Center
        )

        Row(
            Modifier.padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = fileName?: "",
                Modifier.padding(start = 16.dp, end = 16.dp),
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground
                )

            Button(
                onClick = {
                    filePicker.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                },
                modifier = Modifier
                    .width(120.dp)
                    .padding(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = stringResource(id = R.string.browse),
                    color = MaterialTheme.colorScheme.onBackground)
            }
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    val response = activityViewModel.uploadBatchPaymentRecord(inputStream, mimeType, fileName, group?.groupId!!)
                    Toast.makeText(context, response?.message, Toast.LENGTH_LONG).show()
                    if (response?.status == true) {
                        navController.navigate(EVENT_DETAIL)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
            shape = MaterialTheme.shapes.medium
            ) {
            Text(
                text = stringResource(id = R.string.upload),
                color = MaterialTheme.colorScheme.onBackground
                )
        }

        if (isLoading == true) {
            CircularIndicator()
        }
    }
}
