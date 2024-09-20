package com.register.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.util.GenericTopBar
import com.register.app.viewmodel.HomeViewModel

@Composable
fun PrivacyStatementScreen(homeViewModel: HomeViewModel, navController: NavController) {
    Scaffold(
        topBar = { GenericTopBar(
            title = stringResource(id = R.string.privacy),
            navController = navController)}
    ) {
        PrivacyStatementContent(it, homeViewModel)
    }
}

@Composable
fun PrivacyStatementContent(
    paddingValues: PaddingValues,
    homeViewModel: HomeViewModel,
) {
    val privacyStatement = homeViewModel.privacyStatement.observeAsState().value
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = privacyStatement?.content?: "",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            fontSize = TextUnit(14.0f, TextUnitType.Sp)
        )
    }
}