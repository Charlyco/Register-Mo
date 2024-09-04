package com.register.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.util.CircularIndicator
import com.register.app.util.GenericTopBar
import com.register.app.util.OtpTextField
import com.register.app.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun ResetPassword(authViewModel: AuthViewModel, navController: NavController) {
    Scaffold(
        topBar = { GenericTopBar(
            title = stringResource(id = R.string.reset_password),
            navController = navController
        )},
        containerColor = MaterialTheme.colorScheme.background
    ) {
        PasswordResetScreen(Modifier.padding(it), authViewModel, navController)
    }
}

@Composable
fun PasswordResetScreen(
    modifier: Modifier,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    var email by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val countDownTimer = authViewModel.otpTimer.observeAsState().value
    var isOtpSent by rememberSaveable { mutableStateOf(false) }
    val enableResendButton = authViewModel.shouldResendOtp.observeAsState().value
    val isOtPVerified = authViewModel.isOtpVerified.observeAsState().value
    val isLoading = authViewModel.progressLiveData.observeAsState().value

    LaunchedEffect(isOtPVerified) {
        if (isOtPVerified == true) {
            navController.navigate("new_password") {
                launchSingleTop = true
            }
            authViewModel.clearOtpVerificationLiveData(false)
        }
    }

    ConstraintLayout(
        Modifier.padding(top = 64.dp)
    ) {
        val (header, emailBox, sendOtpBtn, otpBox, progress) = createRefs()

        Text(
            text = stringResource(id = R.string.password_reset_header),
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.constrainAs(header) {
                top.linkTo(parent.top, margin = 32.dp)
                centerHorizontallyTo(parent)
            }
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(dimensionResource(id = R.dimen.text_field_height))
                .constrainAs(emailBox) {
                    centerHorizontallyTo(parent)
                    top.linkTo(header.bottom, margin = 32.dp)
                },
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, Color.Gray)

        ) {
            TextField(
                value = email,
                onValueChange = {email = it},
                label = { Text(
                    text = stringResource(id = R.string.email),
                    color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "",
                    tint = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    val response = authViewModel.sendOtp(email)
                    isOtpSent = response?.status!!
                }
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(55.dp)
                .padding(bottom = 4.dp)
                .constrainAs(sendOtpBtn) {
                    centerHorizontallyTo(parent)
                    top.linkTo(emailBox.bottom, margin = 16.dp)
                },
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.background,
                containerColor = MaterialTheme.colorScheme.primary
            )
            ) {
            Text(text = stringResource(id = R.string.send_otp))
        }

        if (isOtpSent) {
            Surface(
                Modifier
                    .fillMaxWidth()
                    .constrainAs(otpBox) {
                        top.linkTo(sendOtpBtn.bottom, margin = 32.dp)
                        centerHorizontallyTo(parent)
                    },
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OtpTextField(authViewModel = authViewModel, email = email)

                    if (countDownTimer != null) {
                        Text(
                            text = countDownTimer,
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = TextUnit(14.0f, TextUnitType.Sp)
                        )
                    }
                    if (enableResendButton == true) {
                        Text(
                            text = stringResource(id = R.string.resend),
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .clickable {
                                    coroutineScope.launch {
                                        authViewModel.resendOtp(email)}},
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = TextUnit(14.0f, TextUnitType.Sp)
                        )
                    }
                }
            }
        }
        if (isLoading == true) {
            CircularIndicator()
        }
    }
}
