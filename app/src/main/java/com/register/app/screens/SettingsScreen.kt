package com.register.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.util.GenericTopBar
import com.register.app.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingScreen(homeViewModel: HomeViewModel, navController: NavController) {
    Scaffold(
        topBar = { GenericTopBar(title = stringResource(id = R.string.settings), navController = navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) {
        SettingsScreenContent(it, homeViewModel, navController)
    }
}

@Composable
fun SettingsScreenContent(
    it: PaddingValues,
    homeViewModel: HomeViewModel,
    navController: NavController
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.Start
    ) {
        AuthenticationSettings(homeViewModel)
    }
}

@Composable
fun AuthenticationSettings(homeViewModel: HomeViewModel) {
    val authMode = homeViewModel.authModeLiveData.observeAsState().value
    val coroutineScope = rememberCoroutineScope()
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(id = R.string.auth_mode),
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.should_request_login),
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground)

            Switch(
                checked = authMode?: false,
                onCheckedChange = {
                    if (authMode == true) {
                        coroutineScope.launch {
                            homeViewModel.setAuthMode(false)
                        }
                    }else {
                        coroutineScope.launch {
                            homeViewModel.setAuthMode(true)
                        }
                    }
                }
            )
        }
    }
}
