package com.register.app.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.register.app.R
import com.register.app.util.CircularIndicator
import com.register.app.util.DataStoreManager
import com.register.app.viewmodel.AuthViewModel

@Composable
fun Signup(
    authViewModel: AuthViewModel,
    navController: NavController,
    dataStoreManager: DataStoreManager
) {
    val signUpBrush = Brush.linearGradient(
        listOf(MaterialTheme.colorScheme.primary,MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.background))
    Surface(color = Color.Transparent,
        modifier = Modifier
            .background(signUpBrush)
            .fillMaxSize()
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize(),
        ) {
            val (backBtn, header, inputSection, alternate) = createRefs()

            Surface(
                modifier = Modifier
                    .size(40.dp)
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

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.constrainAs(header) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(inputSection.top, margin = 42.dp)
                }
            ) {
                Surface(Modifier
                    .padding(vertical = 16.dp)
                    .clip(
                        RoundedCornerShape(
                            topEnd = 8.dp,
                            topStart = 8.dp,
                            bottomEnd = 8.dp,
                            bottomStart = 8.dp
                        )
                    )
                    .size(80.dp),
                    color = Color.White
                ) {
                    Image(painter = painterResource(
                        id = R.drawable.app_icon),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxSize(),
                        contentScale = ContentScale.FillBounds,
                    )
                }
                Text(text = stringResource(
                    id = R.string.welcome),
                    fontFamily = FontFamily.SansSerif,
                    fontSize = TextUnit(22.0f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(id = R.string.fill_info),
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    modifier = Modifier.padding(end = 8.dp, top = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

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
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var rePassword by rememberSaveable { mutableStateOf("") }

    var showPassword by rememberSaveable { mutableStateOf(false) }
    var showRePassword by rememberSaveable { mutableStateOf(false) }
    val screenWidth = LocalConfiguration.current.screenWidthDp - 32
    val error = authViewModel.errorLiveData.observeAsState().value
    val context = LocalContext.current
    var showIndicator by rememberSaveable { mutableStateOf(false) }

    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val (emailBox, firstNameBox, lastNameBox, passwordBox, rePasswordBox, signupBtn, alternate, indicator) = createRefs()



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
            shadowElevation = dimensionResource(id = R.dimen.default_elevation)

        ) {
            TextField(
                value = firstName,
                onValueChange = {firstName = it},
                label = { Text(text = stringResource(id = R.string.first_name)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "")},
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
                    bottom.linkTo(emailBox.top, margin = 16.dp)
                },
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.small,
            shadowElevation = dimensionResource(id = R.dimen.default_elevation)

        ) {
            TextField(
                value = lastName,
                onValueChange = {lastName = it},
                label = { Text(text = stringResource(id = R.string.last_name)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "")},
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
                    bottom.linkTo(passwordBox.top, margin = 16.dp)
                },
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.small,
            shadowElevation = dimensionResource(id = R.dimen.default_elevation)

        ) {
            TextField(
                value = email,
                onValueChange = {email = it},
                label = { Text(text = stringResource(id = R.string.email)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "")},
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
            shadowElevation = dimensionResource(id = R.dimen.default_elevation)

        ) {
            TextField(
                value = password,
                onValueChange = {password = it},
                label = { Text(text = stringResource(id = R.string.password)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "")},
                trailingIcon = {
                    if (showPassword) {
                        IconButton(onClick = { showPassword = false }) {
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = "hide_password"
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { showPassword = true }) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "hide_password"
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
            shadowElevation = dimensionResource(id = R.dimen.default_elevation)

        ) {
            TextField(
                value = rePassword,
                onValueChange = {rePassword = it},
                label = { Text(text = stringResource(id = R.string.confirm_password)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "")},
                trailingIcon = {
                    if (showRePassword) {
                        IconButton(onClick = { showRePassword = false }) {
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = "hide_password"
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { showRePassword = true }) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "hide_password"
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
                val response = authViewModel.signUp(firstName, lastName, email, password, rePassword)
                      if (error?.isNotBlank() == true) {
                          Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                      } },
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = dimensionResource(id = R.dimen.default_elevation),
                pressedElevation = dimensionResource(id = R.dimen.button_pressed_evelation)
            ),
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .width(screenWidth.dp)
                .height(50.dp)
                .padding(bottom = 4.dp)
                .constrainAs(signupBtn) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(alternate.top, margin = 50.dp)
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

        if (showIndicator) {
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
