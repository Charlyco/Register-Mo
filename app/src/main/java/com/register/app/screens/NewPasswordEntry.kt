package com.register.app.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.util.CircularIndicator
import com.register.app.util.GenericTopBar
import com.register.app.util.PasswordTextBox
import com.register.app.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun NewPasswordEntry(authViewModel: AuthViewModel, navController: NavController) {
    Scaffold(
        topBar = { GenericTopBar(
            title = stringResource(id = R.string.enter_new_password),
            navController = navController
        )}
    ) {
        NewPasswordScreen(Modifier.padding(it), authViewModel, navController)
    }
}

@Composable
fun NewPasswordScreen(
    modifier: Modifier,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    var password by rememberSaveable { mutableStateOf("") }
    var rePassword by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val isLoading = authViewModel.progressLiveData.observeAsState().value

    Surface(
        Modifier
            .padding(top = 64.dp)
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(id = R.string.new_password_header),
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp)
                )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(dimensionResource(id = R.dimen.text_field_height)),
                color = MaterialTheme.colorScheme.background,
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                PasswordTextBox(stringResource(id = R.string.password)) {password = it}
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(dimensionResource(id = R.dimen.text_field_height)),
                color = MaterialTheme.colorScheme.background,
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                PasswordTextBox(stringResource(id = R.string.confirm_password)) {rePassword = it}
            }

            if (isLoading == true) {
                CircularIndicator()
            }

            Button(onClick = {
                coroutineScope.launch {
                    if (password == rePassword) {
                        if (password.length >= 6) {
                            val response = authViewModel.resetPassword(password)
                            if (response.status) {
                                navController.navigate("signin") {
                                    launchSingleTop = true
                                }
                            } else {
                                Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_LONG).show()
                        }
                    }else {
                        Toast.makeText(context, "Password mismatch", Toast.LENGTH_LONG).show()
                    }
                } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                    .height(dimensionResource(id = R.dimen.button_height)),
                shape = MaterialTheme.shapes.small
                ) {
                Text(text = stringResource(id = R.string.submit))
            }
        }
    }
}
