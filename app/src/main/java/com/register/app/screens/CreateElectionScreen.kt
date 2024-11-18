package com.register.app.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.model.Member
import com.register.app.util.CircularIndicator
import com.register.app.util.GenericTopBar
import com.register.app.util.ImageLoader
import com.register.app.util.Utils.formatToYYYYMMDD
import com.register.app.util.Utils.toLocalDateTime
import com.register.app.util.Utils.toMills
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun CreateElectionScreen(groupViewModel: GroupViewModel, authViewModel: AuthViewModel, navController: NavController) {
    Scaffold(
        topBar = { GenericTopBar(
            title = stringResource(id = R.string.create_election),
            navController = navController
        ) },
        containerColor = MaterialTheme.colorScheme.background
    ) {
        CreateElectionUi(Modifier.padding(it), groupViewModel, authViewModel, navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateElectionUi(
    modifier: Modifier,
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    var electionTitle by rememberSaveable { mutableStateOf("") }
    var electionDescription by rememberSaveable { mutableStateOf("") }
    var electionDate by rememberSaveable { mutableStateOf("") }
    var office by rememberSaveable { mutableStateOf("") }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val isLoading = groupViewModel.loadingState.observeAsState().value
    val officeList = listOf("Select Office",
        "PRESIDENT", "SECRETARY", "TREASURER", "FINANCIAL_SECRETARY", "ADMIN", "LEADER",
        "ORGANIZER", "CHIEF", "EZE", "IGWE", "EMIR")
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

    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .padding(top = 64.dp)
    ) {
        val (selectDate, date, title, description, contestants, officeBox, progressIndicator) = createRefs()

        Text(
            text = stringResource(id = R.string.select_date),
            modifier = Modifier
                .clickable { showDatePicker = true }
                .constrainAs(selectDate) {
                    top.linkTo(parent.top, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                },
            color = MaterialTheme.colorScheme.primary,
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = electionDate,
            modifier = Modifier
                .clickable { showDatePicker = true }
                .constrainAs(date) {
                    top.linkTo(selectDate.bottom, margin = 8.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                },
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = TextUnit(16.0f, TextUnitType.Sp)
        )

        Surface(
            Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.text_field_height))
                .padding(horizontal = 16.dp)
                .constrainAs(title) {
                    top.linkTo(date.bottom, margin = 16.dp)
                    centerHorizontallyTo(parent)
                },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary),
            shape = MaterialTheme.shapes.small,
            color = Color.White
        ) {
            TextField(value = electionTitle,
                onValueChange = { electionTitle = it },
                placeholder = { Text(text = stringResource(id = R.string.election_title), color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                )
            )
        }

        Surface(
            Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.text_field_height))
                .padding(horizontal = 16.dp)
                .constrainAs(description) {
                    top.linkTo(title.bottom, margin = 16.dp)
                    centerHorizontallyTo(parent)
                },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary),
            shape = MaterialTheme.shapes.small,
            color = Color.White
        ) {
            TextField(value = electionDescription,
                onValueChange = { electionDescription = it },
                placeholder = { Text(text = stringResource(id = R.string.description), color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                )
            )
        }

        Surface(
            Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.text_field_height))
                .padding(horizontal = 16.dp)
                .constrainAs(officeBox) {
                    top.linkTo(description.bottom, margin = 16.dp)
                    centerHorizontallyTo(parent)
                },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary),
            shape = MaterialTheme.shapes.small,
            color = Color.White
        ) {
            SelectOffice(officeList = officeList ) {
                office = it
            }
        }

        Surface(
            Modifier
                .fillMaxWidth()
                .constrainAs(contestants) {
                    top.linkTo(officeBox.bottom, margin = 16.dp)
                    centerHorizontallyTo(parent)
                },
            color = MaterialTheme.colorScheme.background,
        ) {
            AddContestants(groupViewModel, authViewModel, navController) {
                coroutineScope.launch {
                    if (electionDate.isEmpty()) {
                        Toast.makeText(context, "Please select a date", Toast.LENGTH_LONG).show()
                        return@launch
                    } else if (electionTitle.isEmpty()) {
                        Toast.makeText(context, "Specify title of election", Toast.LENGTH_LONG).show()
                        return@launch
                    }else {
                        val response = groupViewModel.createElection(electionTitle, electionDescription, electionDate, office)
                        if (response.status) {
                            Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                            groupViewModel.clearContestants()
                        }else {
                            Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    IconButton(onClick = {
                        val electionDateMillis = datePickerState.selectedDateMillis
                        electionDate = electionDateMillis?.toLocalDateTime()?.toLocalDate()?.formatToYYYYMMDD()!!
                        showDatePicker = false
                    }) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "")
                    }
                }) {
                DatePicker(
                    state = datePickerState
                )
            }
        }

        if (isLoading == true) {
            CircularIndicator()
        }
    }
}

@Composable
fun AddContestants(
    groupViewModel: GroupViewModel,
    authViewModel: AuthViewModel,
    navController: NavController,
    addContestant: () -> Unit
) {
    val contestantList = groupViewModel.contestantList.observeAsState().value
    var showSelectContestantDialog by rememberSaveable { mutableStateOf(false) }
    val group = groupViewModel.groupDetailLiveData.value
    val coroutineScope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.contestants),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold
            )

            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        groupViewModel.populateGroupMembers(group)
                        showSelectContestantDialog = true
                    }
                }
            ) {
                Text(
                    text = stringResource(id = R.string.add),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = TextUnit(16.0f, TextUnitType.Sp),
                    modifier = Modifier
                        .padding(end = 16.dp)
                )

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(18.dp)
                )
            }
        }

        if (showSelectContestantDialog) {
            SelectContestantDialog(groupViewModel) {status ->
                showSelectContestantDialog = status
            }
        }

        if (contestantList?.isNotEmpty() == true) {
            contestantList.forEach { contestant ->
                ContestantItem(contestant, groupViewModel)
            }

            Button(
                onClick = {
                    addContestant()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(text = stringResource(id = R.string.submit))
            }
        }
    }
}

@Composable
fun SelectContestantDialog(
    groupViewModel: GroupViewModel,
    onDismiss: (Boolean) -> Unit
) {
    val dialogHeight = LocalConfiguration.current.screenHeightDp - 120
    var searchTag by rememberSaveable { mutableStateOf("") }
    val memberList = groupViewModel.memberDetailsList.observeAsState().value
    Dialog(
        onDismissRequest = {
            onDismiss(false)
        }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .height(dialogHeight.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onPrimary,
                shadowElevation = dimensionResource(id = R.dimen.low_elevation),
                shape = MaterialTheme.shapes.large
            ) {
                TextField(
                    value = searchTag,
                    onValueChange = { searchTag = it },
                    modifier = Modifier
                        .height(55.dp),
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.search_members),
                            color = Color.Gray
                        )
                    },
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
                            tint = Color.Gray
                        )
                    }
                )
            }
            LazyColumn(
                contentPadding = PaddingValues(vertical = 4.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(memberList!!.filter { member ->
                    member.fullName.contains(
                        searchTag,
                        ignoreCase = true
                    )
                }) { member ->
                    MemberListItem(member, groupViewModel) {status ->
                        onDismiss(status)
                    }
                }
            }
        }
    }
}

@Composable
fun MemberListItem(member: Member, groupViewModel: GroupViewModel, onSelected: (Boolean) -> Unit) {
    val context = LocalContext.current
    Surface(
        Modifier
            .clickable {
                groupViewModel.addToContestants(member)
                onSelected(false)
            }
            .fillMaxWidth(),
    ) {
        Column(
            Modifier.fillMaxWidth()
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    Modifier.padding(end = 16.dp),
                    color = MaterialTheme.colorScheme.background,
                    border = BorderStroke(1.dp, Color.Gray),
                    shape = MaterialTheme.shapes.small
                ) {
                    ImageLoader(
                        imageUrl = member.imageUrl ?: "",
                        context = context,
                        height = 42,
                        width = 42,
                        placeHolder = R.drawable.placeholder
                    )
                }

                Text(
                    text = member.fullName,
                    fontSize = TextUnit(16.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            HorizontalDivider(Modifier.padding(2.dp))
        }
    }
}

@Composable
fun ContestantItem(contestant: Member, groupViewModel: GroupViewModel) {
    val context = LocalContext.current

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            //Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                Modifier.padding(end = 16.dp),
                color = MaterialTheme.colorScheme.background,
                border = BorderStroke(1.dp, Color.Gray),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                ImageLoader(
                    imageUrl = contestant.imageUrl ?: "",
                    context = context,
                    height = 42,
                    width = 42,
                    placeHolder = R.drawable.placeholder
                )
            }

            Text(
                text = contestant.fullName,
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.delete),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(42.dp)
                .padding(end = 16.dp)
                .clickable { groupViewModel.removeFromContestants(contestant) })
    }
}
