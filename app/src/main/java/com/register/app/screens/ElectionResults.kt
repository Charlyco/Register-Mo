package com.register.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
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
import com.register.app.dto.Contestant
import com.register.app.dto.Election
import com.register.app.util.GenericTopBar
import com.register.app.util.ImageLoader
import com.register.app.viewmodel.GroupViewModel

@Composable
fun ElectionResults(groupViewModel: GroupViewModel, navController: NavController) {
    Scaffold(
        topBar = { GenericTopBar(title = "Election Results", navController = navController, navRoute = "election_detail") },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        ElectionResultsScreen(Modifier.padding(it), groupViewModel, navController)
    }
}

@Composable
fun ElectionResultsScreen(
    modifier: Modifier,
    groupViewModel: GroupViewModel,
    navController: NavController
) {
    val election = groupViewModel.electionDetail.observeAsState().value
    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .padding(top = 64.dp)
    ) {
        val (title, description, contestants, winner) = createRefs()

        Text(text = election?.electionTitle!!,
            fontSize = TextUnit(20.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(parent.top, margin = 32.dp)
                    centerHorizontallyTo(parent)
                }
        )

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

        Surface(
            Modifier
                .fillMaxWidth()
                .constrainAs(contestants) {
                    top.linkTo(description.bottom, margin = 16.dp)
                    centerHorizontallyTo(parent)
                },
            color = Color.Transparent
        ) {
            ContestantsList(election)
        }
        if (election.winnerId != null) {
            Surface(
                Modifier
                    .fillMaxWidth()
                    .constrainAs(winner) {
                        top.linkTo(contestants.bottom, margin = 32.dp)
                        centerHorizontallyTo(parent)
                    },
                color = Color.Transparent
            ) {
                ElectionWinner(election)
            }
        }
    }
}

@Composable
fun ElectionWinner(election: Election) {
    val winner = election.contestantList.find { contestant -> contestant.id == election.winnerId }
    Column(
        Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.winner),
            fontSize = TextUnit(28.0f, TextUnitType.Sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
            )
        if (winner != null) {
            ContestantResultItem(contestant = winner )
        }
    }
}

@Composable
fun ContestantsList(election: Election) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)

    ) {

        election.contestantList.forEach { contestant ->
            ContestantResultItem(contestant)
        }
    }
}

@Composable
fun ContestantResultItem(contestant: Contestant) {
    val context = LocalContext.current
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
                .padding(vertical = 4.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
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
                    text = contestant.contestantName!!,
                    fontSize = TextUnit(16.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = "${contestant.voteCount} votes",
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
