package com.register.app.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.util.CircularIndicator
import com.register.app.util.GenericTopBar
import com.register.app.util.GetCustomFiles
import com.register.app.util.ImageLoader
import com.register.app.util.Utils
import com.register.app.util.Utils.formatToYYYYMMDD
import com.register.app.util.Utils.toLocalDateTime
import com.register.app.util.Utils.toMills
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.time.LocalDateTime

@Composable
fun CreateEvent(groupViewModel: GroupViewModel, activityViewModel: ActivityViewModel, navController: NavController) {
    val isLoading = activityViewModel.loadingState.observeAsState().value
    Scaffold(
        topBar = { GenericTopBar(
            title = "Create new Activity",
            navController = navController,
            navRoute = "group_detail"
        )}
    ) {
        ConstraintLayout(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState(initial = 0))) {
            val (content, progress) = createRefs()
            Surface(
                Modifier
                    .fillMaxSize()
                    .constrainAs(content) {
                        centerHorizontallyTo(parent)
                        centerVerticallyTo(parent)
                    },
                color = Color.Transparent) {
                CreateEventScreen(Modifier.padding(it), groupViewModel,activityViewModel, navController)
            }
            if (isLoading == true) {
                Surface(
                    Modifier
                        .constrainAs(progress) {
                            centerHorizontallyTo(parent)
                            centerVerticallyTo(parent)
                        },
                    color = Color.Transparent) {
                    CircularIndicator()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    modifier: Modifier,
    groupViewModel: GroupViewModel,
    activityViewModel: ActivityViewModel,
    navController: NavController
) {
    var activityTitle by rememberSaveable { mutableStateOf("")}
    var activityDescription by rememberSaveable { mutableStateOf("")}
    var levyAmount by rememberSaveable { mutableStateOf("") }
    var eventDate by rememberSaveable { mutableStateOf("") }
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    var showCalender by rememberSaveable { mutableStateOf(false) }
    val imageList = activityViewModel.activityImageList.observeAsState().value
    val coroutineScope = rememberCoroutineScope()
    val dateTime = LocalDateTime.now()
    val context = LocalContext.current
    val datePickerState = remember {
        DatePickerState(
            locale = CalendarLocale("en_EU"),
            initialSelectedDateMillis = dateTime.toMills(),
            initialDisplayedMonthMillis = null,
            2024..3099,
            initialDisplayMode = DisplayMode.Picker
        )
    }

Surface(
    Modifier
        .fillMaxSize()
        .padding(top = 64.dp),
    color = MaterialTheme.colorScheme.background
) {
    Column(
        Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(id = R.string.create_event_title),
            fontWeight = FontWeight.SemiBold,
            fontSize = TextUnit(18.0f, TextUnitType.Sp),
            modifier = Modifier.padding(bottom = 16.dp)
            )

        Text(
            text = stringResource(id = R.string.activity_title),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold
        )
        Surface(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(55.dp),
            border = BorderStroke(1.dp, Color.Gray),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            TextField(
                value = activityTitle,
                onValueChange = { activityTitle = it },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        Text(
            text = stringResource(id = R.string.activity_description),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold
        )
        Surface(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(55.dp),
            border = BorderStroke(1.dp, Color.Gray),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            TextField(
                value = activityDescription,
                onValueChange = { activityDescription = it },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
        Text(
            text = stringResource(id = R.string.levy_amount),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold
        )
        Surface(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(55.dp),
            border = BorderStroke(1.dp, Color.Gray),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            TextField(
                value = levyAmount,
                onValueChange = { levyAmount = it },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        Text(
            text = stringResource(id = R.string.event_date),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            if (showCalender) {
                DatePickerDialog(
                    onDismissRequest = { showCalender = false },
                    confirmButton = {
                        IconButton(onClick = {
                            val eventDateMillis = datePickerState.selectedDateMillis
                            eventDate = eventDateMillis?.toLocalDateTime()?.toLocalDate()?.formatToYYYYMMDD()!!
                            showCalender = false
                        }) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "")
                        }
                    }) {
                    DatePicker(
                        state = datePickerState
                    )
                }
            }
            Surface(
                Modifier
                    .fillMaxWidth()
                    .height(55.dp),
            border = BorderStroke(1.dp, Color.Gray),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
            ) {
                TextField(
                    value = eventDate,
                    onValueChange = { eventDate = it },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    leadingIcon = { Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "",
                        Modifier.clickable { showCalender = true })
                    }
                )
            }
        }
        
        Text(
            text = stringResource(id = R.string.upload_images),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
            )
        if (!imageList.isNullOrEmpty()) {
            Column(
                Modifier.padding(start = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                imageList.forEach { image ->
                    ImageLoader(
                        imageUrl = image,
                        context = context,
                        height = 72,
                        width = 72,
                        placeHolder = R.drawable.placeholder
                    )
                }
                UploadButton(activityViewModel)
            }
        }else {UploadButton(activityViewModel)}

        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { coroutineScope.launch {
                    val response = activityViewModel.createNewActivity(activityTitle,
                        activityDescription, levyAmount.toDouble(),eventDate, group?.groupName!!, group.groupId  )
                    if (response.status) {
                        Toast.makeText(context, "Activity Created with ID ${response.data}", Toast.LENGTH_SHORT).show()
                        navController.navigateUp()
                    }
                    else {
                        Toast.makeText(context, "Failed to create activity", Toast.LENGTH_SHORT).show()
                    }
                }
                          },
                Modifier
                    .height(50.dp)
                    .width(200.dp)
            ) {
                Text(text = stringResource(id = R.string.create_activity))
            }
        }
    }
    }
}

@Composable
fun UploadButton(activityViewModel: ActivityViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                coroutineScope.launch {
                    try {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        if (inputStream != null) {
                            val mimeType = context.contentResolver.getType(uri)
                            activityViewModel.uploadActivityImages(inputStream, mimeType, Utils.getFileNameFromUri(context.contentResolver, uri))
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
        Modifier
            .size(48.dp)
            .clickable {
                filePicker.launch("image/*")
            }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "")
    }
}
