package com.register.app.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.register.app.R
import com.register.app.util.CircularIndicator
import com.register.app.util.OtpTextField
import com.register.app.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun VerifyOtpScreen(authViewModel: AuthViewModel, navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (backBtn, lowerSection, image, text, changeNUmber) = createRefs()

//            Image(
//                painter = painterResource(id = R.drawable.urban2),
//                contentDescription = "",
//                modifier = Modifier
//                    .constrainAs(image) {
//                        centerHorizontallyTo(parent)
//                        top.linkTo(parent.top)
//                    }
//                    .fillMaxWidth(),
//                contentScale = ContentScale.FillBounds
//            )

            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { navController.navigateUp() }
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
            Surface(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(
                        RoundedCornerShape(
                            bottomEnd = 32.dp,
                            topStart = 32.dp
                        )
                    )
                    .constrainAs(text) {
                        centerHorizontallyTo(parent)
                        bottom.linkTo(lowerSection.top, margin = 32.dp)
                    },
                color = MaterialTheme.colorScheme.onTertiary
            ) {
            Text(
                text = stringResource(id = R.string.verify_otp_header),
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
            )}

            Surface(
                modifier = Modifier
                    .constrainAs(lowerSection) {
                        centerHorizontallyTo(parent)
                        bottom.linkTo(parent.bottom)
                    }
                    //.height(600.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 48.dp,
                            topEnd = 48.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        )
                    )
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = dimensionResource(id = R.dimen.default_elevation)
            ) {
                LowerVerifySection(authViewModel, navController)
            }

            Text(
                text = stringResource(id = R.string.change_number),
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onError,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable {
                        navController.navigate("otp") {
                            launchSingleTop = true
                            popUpTo("verify_otp") { inclusive = true }
                        }
                    }
                    .constrainAs(changeNUmber) {
                        centerHorizontallyTo(parent)
                        top.linkTo(lowerSection.top, margin = 8.dp)
                    }
            )
        }
    }
}

@Composable
fun LowerVerifySection(authViewModel: AuthViewModel, navController: NavController) {
    val timer = authViewModel.otpTimer?.observeAsState()?.value
    val enableResendButton =authViewModel.shouldResendOtp.observeAsState().value
    val screenWidth = LocalConfiguration.current.screenWidthDp - 32
    var showIndicator by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val isOtPVerified = authViewModel.isOtpVerified.observeAsState().value

    ConstraintLayout(
        Modifier.height(512.dp)
    ) {
        val (otpBox, nextButton, indicator, timerText, checkMark, loginInstead) = createRefs()

        Surface(
                color = Color.Transparent,
                modifier = Modifier
                    .size(128.dp)
                    .constrainAs(checkMark) {
                        centerHorizontallyTo(parent)
                        top.linkTo(parent.top, margin = 32.dp)
                        bottom.linkTo(otpBox.top, margin = 16.dp)
                    }
            ) {
                if (isOtPVerified == true) {
                    LoadCheckMark()
                }
        }

        Surface(
            modifier = Modifier
                //.width(screenWidth.dp)
                .constrainAs(otpBox) {
                    top.linkTo(parent.top, margin = 160.dp)
                    centerHorizontallyTo(parent)
                },
            color = Color.Transparent
        ) {
            OtpTextField(authViewModel = authViewModel)
        }

        if (enableResendButton == true) {
            Text(
                text = stringResource(id = R.string.resend),
                modifier = Modifier.constrainAs(timerText) {
                    top.linkTo(otpBox.bottom, margin = 4.dp)
                    end.linkTo(otpBox.end)
                },
                color = MaterialTheme.colorScheme.primary,
                fontSize = TextUnit(14.0f, TextUnitType.Sp)
            )
        }

        if (timer != null) {
            Text(
                text = timer,
                modifier = Modifier.constrainAs(timerText) {
                    top.linkTo(otpBox.bottom, margin = 4.dp)
                    start.linkTo(otpBox.start)
                },
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = TextUnit(14.0f, TextUnitType.Sp)
            )
        }
        if (isOtPVerified == true) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        navController.navigate("signup") {
                            launchSingleTop = true
                        }
                    }
                },
                modifier = Modifier
                    .width(screenWidth.dp)
                    .height(50.dp)
                    .constrainAs(nextButton) {
                        centerHorizontallyTo(parent)
                        bottom.linkTo(parent.bottom, margin = 128.dp)
                    },
                shape = MaterialTheme.shapes.large,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = dimensionResource(id = R.dimen.low_elevation),
                    pressedElevation = dimensionResource(id = R.dimen.button_pressed_evelation)
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = stringResource(id = R.string.next))
            }
        }

        if (showIndicator) {
            Surface(
                modifier = Modifier.constrainAs(indicator) {
                    centerHorizontallyTo(parent)
                    centerVerticallyTo(parent)
                },
                color = Color.Transparent
            ) {
                CircularIndicator()
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.constrainAs(loginInstead) {
                centerHorizontallyTo(parent)
                bottom.linkTo(parent.bottom, margin = 32.dp)
            }
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

}

@Composable
fun LoadCheckMark() {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.check_mark_anim))
        val progress by animateLottieCompositionAsState(composition)
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(120.dp)
        )
}
