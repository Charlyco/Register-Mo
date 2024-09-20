package com.register.app.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.util.CircularIndicator
import com.register.app.util.DataStoreManager
import com.register.app.util.ONBOARDING
import com.register.app.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun Signup(
    authViewModel: AuthViewModel,
    navController: NavController,
    dataStoreManager: DataStoreManager
) {
    val scrollState = rememberScrollState(initial = 0)
    Surface(color = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            val (backBtn, topBg, header, inputSection, alternate) = createRefs()

            Image(painter = painterResource(
                id = R.drawable.auth_bg2),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .constrainAs(topBg) {
                        top.linkTo(parent.top, margin = 0.dp)
                        centerHorizontallyTo(parent)
                    },
                contentScale = ContentScale.FillBounds
            )

            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        navController.navigate(ONBOARDING) {
                            popUpTo("signup") { inclusive = true }
                        }
                    }
                    .constrainAs(backBtn) {
                        start.linkTo(parent.start, margin = 16.dp)
                        top.linkTo(parent.top, margin = 12.dp)
                    },
                shape = MaterialTheme.shapes.extraLarge,
                shadowElevation = dimensionResource(id = R.dimen.default_elevation),
                color = MaterialTheme.colorScheme.background
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = stringResource(id = R.string.get_started),
                fontWeight = FontWeight.Bold,
                fontSize = TextUnit(48.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.constrainAs(header) {
                    top.linkTo(backBtn.bottom, margin = 40.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                }
            )

            Surface(
                color = Color.Transparent,
                modifier = Modifier.constrainAs(inputSection) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(parent.bottom)
                }
            ) {
                TextInputSection(authViewModel, navController)
            }
        }
    }
}

@Composable
fun AlternateAction(navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ){
       Text(
           text = stringResource(id = R.string.account_exit),
           fontSize = TextUnit(14.0f, TextUnitType.Sp),
           modifier = Modifier.padding(end = 8.dp),
           color = MaterialTheme.colorScheme.onBackground
       )
        Text(
            text = stringResource(id = R.string.signin_instead),
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            modifier = Modifier
                .padding(end = 8.dp)
                .clickable { navController.navigate("signin") },
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun TextInputSection(authViewModel: AuthViewModel, navController: NavController) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    val screenWidth = LocalConfiguration.current.screenWidthDp - 32
    val error = authViewModel.errorLiveData.observeAsState().value
    val context = LocalContext.current
    val showIndicator = authViewModel.progressLiveData.observeAsState().value
    val coroutineScope = rememberCoroutineScope()

    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val (emailBox, phoneBox, firstNameBox, lastNameBox, signupBtn, alternate, indicator) = createRefs()

        Surface(
            modifier = Modifier
                .width(screenWidth.dp)
                .height(dimensionResource(id = R.dimen.text_field_height))
                .constrainAs(firstNameBox) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(lastNameBox.top, margin = 16.dp)
                },
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            TextField(
                value = firstName,
                onValueChange = {firstName = it},
                label = { Text(
                    text = stringResource(id = R.string.first_name),
                    color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "",
                    tint = Color.Gray)},
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        Surface(
            modifier = Modifier
                .width(screenWidth.dp)
                .height(dimensionResource(id = R.dimen.text_field_height))
                .constrainAs(lastNameBox) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(phoneBox.top, margin = 16.dp)
                },
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            TextField(
                value = lastName,
                onValueChange = {lastName = it},
                label = { Text(
                    text = stringResource(id = R.string.last_name),
                    color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "",
                    tint = Color.Gray)},
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        Surface(
            modifier = Modifier
                .width(screenWidth.dp)
                .height(dimensionResource(id = R.dimen.text_field_height))
                .constrainAs(phoneBox) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(emailBox.top, margin = 16.dp)
                },
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            TextField(
                value = phone,
                onValueChange = {phone = it},
                label = { Text(
                    text = stringResource(id = R.string.phone),
                    color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(
                    imageVector = Icons.Default.PhoneAndroid,
                    contentDescription = "",
                    tint = Color.Gray)},
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        Surface(
            modifier = Modifier
                .width(screenWidth.dp)
                .height(dimensionResource(id = R.dimen.text_field_height))
                .constrainAs(emailBox) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(signupBtn.top, margin = 48.dp)
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
                    tint = Color.Gray)},
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
                    val response = authViewModel.sendOtp(firstName, lastName, email, phone)
                    if (error?.isNotBlank() == true) {
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        if (error == "Account with this email already exists") {
                            navController.navigate("login") {
                                popUpTo("splash") {inclusive = true}
                            }
                        }
                    }
                    if (response) {
                        navController.navigate("otp_verify/${email}") {
                            popUpTo("splash") {inclusive = true}
                        }
                    }
                }
                      },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background
            ),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .width(screenWidth.dp)
                .height(55.dp)
                .padding(bottom = 4.dp)
                .constrainAs(signupBtn) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(alternate.top, margin = 16.dp)
                }
            ) {
            Text(text = stringResource(id = R.string.proceed))
        }

        Surface(
            color = Color.Transparent,
            modifier = Modifier.constrainAs(alternate) {
                centerHorizontallyTo(parent)
                bottom.linkTo(parent.bottom, margin = 24.dp)
            }
        ) {
            AlternateAction(navController)
        }

        if (showIndicator == true) {
            Surface(
                color = Color.Transparent,
                modifier = Modifier.constrainAs(indicator) {
                    centerHorizontallyTo(parent)
                    centerVerticallyTo(parent)
                }
            ) {
                CircularIndicator()
            }
        }
    }
}
