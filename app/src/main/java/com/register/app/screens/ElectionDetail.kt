package com.register.app.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
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
import com.register.app.dto.Contestant
import com.register.app.dto.Election
import com.register.app.enums.ElectionStatus
import com.register.app.util.CircularIndicator
import com.register.app.util.GenericTopBar
import com.register.app.util.ImageLoader
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch
import kotlin.math.truncate

@Composable
fun ElectionDetail(groupViewModel: GroupViewModel, navController: NavController) {
    var showAddContestantDialog by rememberSaveable { mutableStateOf(false) }
    val contestant = groupViewModel.contestantList.observeAsState().value
    val election = groupViewModel.electionDetail.observeAsState().value
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = { GenericTopBar(title = "Elections", navController = navController, navRoute = "elections") },
        floatingActionButton = { AddContestantFab(groupViewModel) {showAddContestantDialog = it} },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        ElectionDetailScreen(Modifier.padding(it), groupViewModel, navController)
        if (showAddContestantDialog) {
            SelectContestantDialog(groupViewModel ) { dismiss ->
                showAddContestantDialog = dismiss
                if (contestant?.isNotEmpty() == true) {
                    coroutineScope.launch {
                        groupViewModel.addContestant(contestant[0], election!!)
                    }
                }
            }
        }
    }
}

@Composable
fun AddContestantFab(groupViewModel: GroupViewModel, showAddContestantDialog: (Boolean) -> Unit) {
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    val coroutineScope = rememberCoroutineScope()
    FloatingActionButton(
        onClick = {
            coroutineScope.launch {
                groupViewModel.populateGroupMembers(group)
                showAddContestantDialog(true)
            }
        },
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.background) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "")
    }
}

@Composable
fun ElectionDetailScreen(
    modifier: Modifier,
    groupViewModel: GroupViewModel,
    navController: NavController
) {
    val election = groupViewModel.electionDetail.observeAsState().value
    var selectedContestant by remember { mutableStateOf<Contestant?>(null) }
    val isLoading = groupViewModel.loadingState.observeAsState().value
    val isAdmin = groupViewModel.isUserAdminLiveData.observeAsState().value
    var showContextDialog by rememberSaveable { mutableStateOf(false) }
    //var contestantToRemove: Contestant? = null
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var hasVoted by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit) {
        hasVoted = groupViewModel.checkIfUserHasVoted(election!!)
    }
    ConstraintLayout(
        Modifier
            .padding(top = 64.dp)
            .fillMaxSize()
    ) {
        val (title, description, contestants, endBtn, startBtn, voteBtn, result) = createRefs()

        Text(text = election?.electionTitle!!,
            fontSize = TextUnit(20.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(parent.top, margin = 40.dp)
                    centerHorizontallyTo(parent)
                }
        )

        if (isAdmin == true) {
            Text(
                text = stringResource(id = R.string. start_election),
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = if (election.electionStatus != ElectionStatus.ACTIVE.name) {
                    Color(context.getColor(R.color.teal_200))
                }else {Color.Gray},
                modifier = Modifier
                    .constrainAs(startBtn) {
                        top.linkTo(parent.top, margin = 4.dp)
                        end.linkTo(parent.end, margin = 16.dp)
                    }
                    .clickable {
                        coroutineScope.launch {
                            groupViewModel.startElection(election)
                        }
                    }
            )

            Text(
                text = stringResource(id = R.string.end_election),
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = if (election.electionStatus == ElectionStatus.ACTIVE.name) {
                    MaterialTheme.colorScheme.primary
                }else {Color.Gray},
                modifier = Modifier
                    .constrainAs(endBtn) {
                        top.linkTo(startBtn.bottom, margin = 8.dp)
                        end.linkTo(parent.end, margin = 16.dp)
                    }
                    .clickable {
                        coroutineScope.launch {
                            groupViewModel.endElection(election)
                        }
                    }
                )
        }

        Text(text = election.description,
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .constrainAs(description) {
                    top.linkTo(title.bottom, margin = 32.dp)
                    start.linkTo(parent.start)
                }
        )

        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .constrainAs(contestants) {
                    top.linkTo(description.bottom, margin = 16.dp)
                    centerHorizontallyTo(parent)
                }
        ) {
            Text(text = stringResource(id = R.string.select_contestant_to_vote),
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier)

            election.contestantList.forEach { contestant ->
                Surface(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.background,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            //Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = contestant == selectedContestant,
                                onClick = { selectedContestant = contestant })
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
                                text = contestant.contestantName!!,
                                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        if (isAdmin == true) {
                            Icon(
                                painter = painterResource(id = R.drawable.delete),
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(42.dp)
                                    .padding(end = 16.dp)
                                    .clickable {
                                        showContextDialog = true
                                        selectedContestant = contestant
                                    }
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = stringResource(id = R.string.view_result),
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable {
                    navController.navigate("election_result") {
                        launchSingleTop = true
                    }
                }
                .constrainAs(result) {
                    end.linkTo(parent.end, margin = 16.dp)
                    top.linkTo(contestants.bottom, margin = 16.dp)
                }
            )

        if (election.electionStatus == ElectionStatus.ACTIVE.name) {
            if (!hasVoted) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val response = groupViewModel.castVote(election, selectedContestant)
                            if (response.status) {
                                Toast.makeText(context, response.data.toString(), Toast.LENGTH_SHORT).show()
                                //navController.navigateUp()
                            }else {
                                Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .constrainAs(voteBtn) {
                            top.linkTo(contestants.bottom, margin = 42.dp)
                            centerHorizontallyTo(parent)
                        },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(text = stringResource(id = R.string.cast_vote))
                }
            }else {
                Text(
                    text = stringResource(id = R.string.you_have_voted),
                    fontSize = TextUnit(16.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .constrainAs(voteBtn) {
                            top.linkTo(contestants.bottom, margin = 42.dp)
                            centerHorizontallyTo(parent)
                        }
                )
            }
        }else {
            Text(
                text = stringResource(id = R.string.election_not_started),
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .constrainAs(voteBtn) {
                        top.linkTo(contestants.bottom, margin = 42.dp)
                        centerHorizontallyTo(parent)
                    }
            )
        }

        if (isLoading == true) {
            CircularIndicator()
        }

        if (showContextDialog) {
            RemoveContestantDialog(groupViewModel, selectedContestant, election) {showContextDialog = it}
        }
    }
}

@Composable
fun RemoveContestantDialog(
    groupViewModel: GroupViewModel,
    contestantToRemove: Contestant?,
    election: Election,
    showContextDialog: (Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    Dialog(
        onDismissRequest = {
            showContextDialog(false)
        }) {
        Surface(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(120.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(id = R.string.remove_contestant),
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
                    )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.clickable {
                            showContextDialog(false)
                        }
                    )
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "",
                        tint = Color(context.getColor(R.color.teal_200)),
                        modifier = Modifier
                            .clickable{ coroutineScope.launch {
                                val response = groupViewModel.removeContestant(
                                    contestantToRemove?.id,
                                    election.electionId
                                )
                                if (response.status) {
                                    Toast.makeText(context, response.data.toString(), Toast.LENGTH_SHORT)
                                        .show()
                                    groupViewModel.getElectionDetails(election.electionId!!)
                                }else {
                                    Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}


