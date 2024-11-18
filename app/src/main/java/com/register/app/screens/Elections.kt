package com.register.app.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.dto.Election
import com.register.app.util.GenericTopBar
import com.register.app.viewmodel.GroupViewModel

@Composable
fun Elections(groupViewModel: GroupViewModel, navController: NavController) {
    val isAdmin = groupViewModel.isUserAdminLiveData.observeAsState().value
    Scaffold(
        topBar = { GenericTopBar(title = "Elections", navController = navController) },
        floatingActionButton = { if (isAdmin == true) {
            CreateElectionFab(navController)
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        ElectionsScreen(Modifier.padding(it), groupViewModel, navController)
    }
}

@Composable
fun CreateElectionFab(navController: NavController) {
    FloatingActionButton(
        onClick = { navController.navigate("create_election") },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = CircleShape
        ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "")
    }
}

@Composable
fun ElectionsScreen(
    modifier: Modifier,
    groupViewModel: GroupViewModel,
    navController: NavController
) {
    val elections = groupViewModel.electionsLiveData.observeAsState().value
    val isSuspended = groupViewModel.isUserSuspended.observeAsState().value
    if (isSuspended == true) {
        Text(
            text = stringResource(id = R.string.suspended),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.error)
    } else {
        if (elections.isNullOrEmpty()) {
            NullElectionsScreen()
        } else  {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(top = 64.dp),
                state = rememberLazyListState(),
            ) {
                items(elections) { election ->
                    ElectionItem(election, groupViewModel, navController)
                }
            }
        }
    }
}

@Composable
fun NullElectionsScreen() {
    Surface(
        Modifier.fillMaxSize()
    ) {
       ConstraintLayout(
           Modifier.fillMaxSize()
       ) {
           val (image, text) = createRefs()

           Image(
               painter = painterResource(id = R.drawable.vote),
               contentDescription = "",
               contentScale = ContentScale.Fit,
               modifier = Modifier
                   .size(120.dp)
                   .constrainAs(image) {
                       centerHorizontallyTo(parent)
                       centerVerticallyTo(parent)
                   }
               )
           Text(
               text = stringResource(id = R.string.no_elections),
               fontSize = TextUnit(20.0f, TextUnitType.Sp),
               fontWeight = FontWeight.SemiBold,
               color = MaterialTheme.colorScheme.onBackground,
               textAlign = TextAlign.Center,
               modifier = Modifier.constrainAs(text) {
                   top.linkTo(image.bottom, margin = 16.dp)
                   centerHorizontallyTo(parent)
               }
           )
       }
    }
}

@Composable
fun ElectionItem(election: Election, groupViewModel: GroupViewModel, navController: NavController) {
    var voteCount = 0
    election.contestantList.forEach { contestant ->
        voteCount += contestant.voteCount?: 0
    }
    Surface(
        Modifier
            .fillMaxWidth()
            .clickable {
                groupViewModel.setSelectedElection(election)
                navController.navigate("election_detail") {
                    launchSingleTop = true
                }
            }
            .padding(horizontal = 16.dp, vertical = 2.dp),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.onTertiary
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
           Text(text = election.electionTitle,
               fontSize = TextUnit(16.0f, TextUnitType.Sp),
               fontWeight = FontWeight.SemiBold,
               color = MaterialTheme.colorScheme.onBackground,
               modifier = Modifier.padding(start = 8.dp, top = 8.dp)
           )

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "$voteCount votes",
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                )

                Text(text = "Date: ${election.electionDate}",
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(end = 8.dp, top = 8.dp)
                )
            }
        }
    }
}
