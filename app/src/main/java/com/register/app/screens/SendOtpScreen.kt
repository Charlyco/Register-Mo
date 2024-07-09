//package com.register.app.screens
//
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowDropDown
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.DropdownMenu
//import androidx.compose.material3.DropdownMenuItem
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.material3.TextFieldDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.res.dimensionResource
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.TextUnit
//import androidx.compose.ui.unit.TextUnitType
//import androidx.compose.ui.unit.dp
//import androidx.constraintlayout.compose.ConstraintLayout
//import androidx.navigation.NavController
//import com.register.app.R
//import com.register.app.model.CountryCode
//import com.register.app.util.CircularIndicator
//import com.register.app.util.CountryCodeSaver
//import com.register.app.viewmodel.AuthViewModel
//import kotlinx.coroutines.launch
//
//@Composable
//fun SendOtpScreen(authViewModel: AuthViewModel, navController: NavController) {
//    Surface(
//        modifier = Modifier.fillMaxSize(),
//        color = MaterialTheme.colorScheme.primary
//    ) {
//        ConstraintLayout(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            val (lowerSection, image, text) = createRefs()
//            Image(
//                painter = painterResource(id = R.drawable.otp),
//                contentDescription = "",
//                contentScale = ContentScale.FillBounds,
//                modifier = Modifier
//                    .width(140.dp)
//                    .height(180.dp)
//                    .constrainAs(image) {
//                        centerHorizontallyTo(parent)
//                        top.linkTo(parent.top, margin = 64.dp)
//                    }
//            )
//
//            Text(
//                text = stringResource(id = R.string.otp_header),
//                fontSize = TextUnit(18.0f, TextUnitType.Sp),
//                color = MaterialTheme.colorScheme.onPrimary,
//                textAlign = TextAlign.Center,
//                modifier = Modifier
//                    .padding(vertical = 2.dp, horizontal = 4.dp)
//                    .constrainAs(text) {
//                        centerHorizontallyTo(parent)
//                        bottom.linkTo(lowerSection.top, margin = 32.dp) },
//                )
//
//            Surface(
//                modifier = Modifier
//                    .constrainAs(lowerSection) {
//                        centerHorizontallyTo(parent)
//                        bottom.linkTo(parent.bottom)
//                        //top.linkTo(image.bottom, margin = (-112).dp)
//                    }
//                    //.height(600.dp)
//                    .clip(
//                        RoundedCornerShape(
//                            topStart = 48.dp,
//                            topEnd = 48.dp,
//                            bottomStart = 0.dp,
//                            bottomEnd = 0.dp
//                        )
//                    )
//                    .fillMaxWidth(),
//                color = MaterialTheme.colorScheme.background,
//                shadowElevation = dimensionResource(id = R.dimen.default_elevation)
//            ) {
//                LowerSection(authViewModel, navController)
//            }
//
//
//        }
//    }
//}
//
//@Composable
//fun LowerSection(authViewModel: AuthViewModel, navController: NavController) {
//    var phoneNumber by rememberSaveable { mutableStateOf("") }
//    val screenWidth = LocalConfiguration.current.screenWidthDp - 32
//    var showIndicator by rememberSaveable { mutableStateOf(false) }
//    val coroutineScope = rememberCoroutineScope()
//    var countryCode by rememberSaveable { mutableStateOf("") }
//    val countryList = listOf(
//        CountryCode("Afghanistan", "+93"),
//        CountryCode("Albania", "+355"),
//        CountryCode("Algeria", "+213"),
//        CountryCode("American Samoa", "+1-684")
//    )
//    ConstraintLayout {
//        val (spinner, phoneBox, otpBtn, indicator, loginInstead) = createRefs()
//
//        Surface(
//            modifier = Modifier
//                .width(screenWidth.dp)
//                .height(50.dp)
//                .constrainAs(spinner) {
//                    bottom.linkTo(phoneBox.top, margin = 32.dp)
//                    top.linkTo(parent.top, 48.dp)
//                    centerHorizontallyTo(parent)
//                },
//            color = MaterialTheme.colorScheme.background,
//            shape = MaterialTheme.shapes.large,
//            border = BorderStroke(1.dp, Color.Gray)
//        ) {
//            SelectCountry(countryList) {
//                countryCode = it.code
//            }
//        }
//
//        Surface(
//            modifier = Modifier
//                .width(screenWidth.dp)
//                .height(55.dp)
//                .constrainAs(phoneBox) {
//                    bottom.linkTo(otpBtn.top, margin = 72.dp)
//                    centerHorizontallyTo(parent)
//                },
//            color = MaterialTheme.colorScheme.background,
//            shape = MaterialTheme.shapes.large,
//            border = BorderStroke(1.dp, Color.Gray)
//        ) {
//            TextField(
//                value = phoneNumber,
//                onValueChange = { phoneNumber = it },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
//                label = { Text(
//                    text = stringResource(id = R.string.phone_number),
//                    color = Color.Gray)},
//                colors = TextFieldDefaults.colors(
//                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
//                    unfocusedIndicatorColor = Color.Transparent,
//                    focusedContainerColor = MaterialTheme.colorScheme.background,
//                    focusedIndicatorColor = Color.Transparent
//                )
//            )
//        }
//
//        Button(
//            onClick = {
//                coroutineScope.launch {
//                    showIndicator = true
//                    val isOtpSent = authViewModel.sendOtp(
//                        phoneNumber,
//                        lastName,
//                        email,
//                        phone,
//                        username
//                    )
//                    if (isOtpSent) {
//                        showIndicator = false
//                        navController.navigate("otp_verify") {
//                            launchSingleTop = true
//                        }
//                    }
//                }
//            },
//            modifier = Modifier
//                .width(screenWidth.dp)
//                .height(50.dp)
//                .constrainAs(otpBtn) {
//                    centerHorizontallyTo(parent)
//                    bottom.linkTo(parent.bottom, margin = 120.dp)
//                },
//            shape = MaterialTheme.shapes.large,
////            elevation = ButtonDefaults.buttonElevation(
////                defaultElevation = dimensionResource(id = R.dimen.low_elevation),
////                pressedElevation = dimensionResource(id = R.dimen.button_pressed_evelation)
////            ),
//            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = MaterialTheme.colorScheme.background,
//                contentColor = MaterialTheme.colorScheme.primary
//            )
//            ) {
//            Text(text = stringResource(id = R.string.get_otp))
//        }
//
//        if (showIndicator) {
//            Surface(
//                color = Color.Transparent,
//                modifier = Modifier.constrainAs(indicator) {
//                    centerHorizontallyTo(parent)
//                    centerVerticallyTo(parent)
//                }
//            ) {
//                CircularIndicator()
//            }
//        }
//
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.constrainAs(loginInstead) {
//                centerHorizontallyTo(parent)
//                bottom.linkTo(parent.bottom, margin = 32.dp)
//            }
//        ){
//            Text(
//                text = stringResource(id = R.string.account_exit),
//                fontSize = TextUnit(14.0f, TextUnitType.Sp),
//                modifier = Modifier.padding(end = 8.dp),
//                color = MaterialTheme.colorScheme.onBackground
//            )
//            Text(
//                text = stringResource(id = R.string.signin_instead),
//                fontSize = TextUnit(14.0f, TextUnitType.Sp),
//                modifier = Modifier
//                    .padding(end = 8.dp)
//                    .clickable { navController.navigate("signin") },
//                color = MaterialTheme.colorScheme.primary
//            )
//        }
//    }
//}
//
//@Composable
//fun SelectCountry(
//    countryList: List<CountryCode>,
//    countryCode: (CountryCode) -> Unit
//) {
//    var expanded by remember { mutableStateOf(false) }
//    var selectedOptionText by rememberSaveable (
//        stateSaver = CountryCodeSaver,
//        key = "name",
//        init = { mutableStateOf(countryList[0]) }
//    )
//    val screenWidth = LocalConfiguration.current.screenWidthDp
//
//    Box(
//        modifier = Modifier
//            .width((screenWidth - 32).dp)
//            .height(55.dp),
//        contentAlignment = Alignment.Center
//    ) {
//       ConstraintLayout(modifier = Modifier
//           .clickable { expanded = !expanded }
//           .fillMaxSize()
//           .padding(8.dp)
//           ) {
//           val (text, icon, menu) = createRefs()
//
//           Text(
//               text = "${selectedOptionText.name} ${selectedOptionText.code}",
//               fontSize = TextUnit(14.0f, TextUnitType.Sp),
//               color = MaterialTheme.colorScheme.onBackground,
//               modifier = Modifier
//                   .padding(8.dp)
//                   .constrainAs(text) {
//                       start.linkTo(parent.start, margin = 4.dp)
//                       centerVerticallyTo(parent)
//                   }
//           )
//
//           Icon(
//               imageVector = Icons.Default.ArrowDropDown,
//               contentDescription = "",
//               modifier = Modifier.constrainAs(icon) {
//                   end.linkTo(parent.end, margin = 2.dp)
//                   centerVerticallyTo(parent)
//               }
//           )
//
//           DropdownMenu(
//               expanded = expanded,
//               onDismissRequest = { expanded = false },
//               modifier = Modifier
//                   .constrainAs(menu) { end.linkTo(icon.start) }
//                   .width((screenWidth - 40).dp)
//               ) {
//               countryList.forEach {
//                   DropdownMenuItem(
//                       text = { Text(text = "${it.name} ${it.code}") },
//                       onClick = {
//                           expanded = false
//                           selectedOptionText = it
//                           countryCode(it)
//                       }
//                   )
//               }
//           }
//       }
//    }
//}
//
