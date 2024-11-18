package com.register.app.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
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
import com.register.app.util.GenericTopBar
import com.register.app.util.HOME
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch

@Composable
fun DeleteGroup(groupViewModel: GroupViewModel, navController: NavController) {
    val isLoading = groupViewModel.loadingState.observeAsState().value
    Scaffold(
        topBar = {GenericTopBar(title = stringResource(id = R.string.delete_group), navController = navController)}
    ) {
        DeleteGroupContents(it, groupViewModel, navController)
        if (isLoading == true) {
            CircularIndicator()
        }
    }
}

@Composable
fun DeleteGroupContents(
    paddingValues: PaddingValues,
    groupViewModel: GroupViewModel,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val group = groupViewModel.groupDetailLiveData.observeAsState().value

    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.delete_group_info),
            fontSize = TextUnit(16.0f, TextUnitType.Sp),
            textAlign = TextAlign.Center
            )

        Button(
            onClick = {
                coroutineScope.launch {
                    val response = groupViewModel.deleteGroup(group?.groupId!!)
                    Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                    if (response.status) {
                        navController.navigate(HOME) {
                            popUpTo("group_detail") {inclusive = true}
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 32.dp, end = 32.dp)
        ) {
            Text(text = stringResource(id = R.string.proceed))
        }
    }
}
