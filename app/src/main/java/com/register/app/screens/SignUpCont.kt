package com.register.app.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.util.CircularIndicator
import com.register.app.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun SignUpCont(authViewModel: AuthViewModel,
               navController: NavController) {
    Surface(color = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
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
                val (backBtn,topBg, header, inputSection, alternate) = createRefs()

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
                            navController.navigate("onboard") {
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
                    text = stringResource(id = R.string.complete_info),
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
                    InputSection(authViewModel, navController)
                }
            }
        }
    }
}

@Composable
fun InputSection(authViewModel: AuthViewModel, navController: NavController) {
    var username by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var rePassword by rememberSaveable { mutableStateOf("") }
    val screenWidth = LocalConfiguration.current.screenWidthDp - 32
    val error = authViewModel.errorLiveData.observeAsState().value
    val context = LocalContext.current
    val showIndicator = authViewModel.progressLiveData.observeAsState().value
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var showRePassword by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val (usernameBox, passwordBox, rePasswordBox, addressBox, signupBtn, alternate, indicator) = createRefs()

        Surface(
            modifier = Modifier
                .width(screenWidth.dp)
                .height(dimensionResource(id = R.dimen.text_field_height))
                .constrainAs(usernameBox) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(addressBox.top, margin = 16.dp)
                },
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            TextField(
                value = username,
                onValueChange = {username = it},
                label = { Text(
                    text = stringResource(id = R.string.username),
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
                .constrainAs(addressBox) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(passwordBox.top, margin = 16.dp)
                },
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            TextField(
                value = address,
                onValueChange = {address = it},
                label = { Text(
                    text = stringResource(id = R.string.address),
                    color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(
                    imageVector = Icons.Default.LocationOn,
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
                .constrainAs(passwordBox) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(rePasswordBox.top, margin = 16.dp)
                },
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            TextField(
                value = password,
                onValueChange = {password = it},
                label = { Text(
                    text = stringResource(id = R.string.password),
                    color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "",
                    tint = Color.Gray)},
                trailingIcon = {
                    if (showPassword) {
                        IconButton(onClick = { showPassword = false }) {
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = "hide_password",
                                tint = Color.Gray
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { showPassword = true }) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "hide_password",
                                tint = Color.Gray
                            )
                        }
                    }
                },
                visualTransformation = if (showPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
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
                .constrainAs(rePasswordBox) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(signupBtn.top, margin = 32.dp)
                },
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            TextField(
                value = rePassword,
                onValueChange = {rePassword = it},
                label = { Text(
                    text = stringResource(id = R.string.confirm_password),
                    color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "",
                    tint = Color.Gray)},
                trailingIcon = {
                    if (showRePassword) {
                        IconButton(onClick = { showRePassword = false }) {
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = "hide_password",
                                tint = Color.Gray
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { showRePassword = true }) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "hide_password",
                                tint = Color.Gray
                            )
                        }
                    }
                },
                visualTransformation = if (showRePassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
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
                    val response = authViewModel.signUp(username, password, rePassword, address)
                    if (error?.isNotBlank() == true) {
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    }
                    if (response) {
                        navController.navigate("signin") {
                            popUpTo("onboard") {inclusive = true}
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
            Text(text = stringResource(id = R.string.signup))
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