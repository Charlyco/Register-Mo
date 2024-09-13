package com.register.app.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.LiveHelp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.dto.SupportMessageDto
import com.register.app.enums.MessageType
import com.register.app.util.GenericTopBar
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.ForumViewModel
import com.register.app.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun SupportScreen(
    authViewModel: AuthViewModel,
    forumViewModel: ForumViewModel,
    homeViewModel: HomeViewModel,
    navController: NavController
) {
    Scaffold(
        topBar = {GenericTopBar(title = "Support", navController = navController)},
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        SupportScreenContent(Modifier.padding(it), forumViewModel, authViewModel, homeViewModel, navController)
    }
}

@Composable
fun SupportScreenContent(
    modifier: Modifier,
    forumViewModel: ForumViewModel,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    navController: NavController
) {
    val userData = authViewModel.userLideData.observeAsState().value
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var showSendEmailDialog by rememberSaveable { mutableStateOf(false) }
    Column(
        Modifier
            .padding(top = 64.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showSendEmailDialog) {
            SendEmailDialog {
                showSendEmailDialog = it
            }
        }
        Image(
            painter = painterResource(id = R.drawable.support),
            contentDescription = "",
            modifier = Modifier
                .padding(top = 32.dp)
                .size(120.dp),
            contentScale = ContentScale.FillBounds
        )
        Text(
            text = stringResource(id = R.string.support_desc),
            fontWeight = FontWeight.SemiBold,
            fontSize = TextUnit(24.0f, TextUnitType.Sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
            )

        //Spacer(modifier = Modifier.height(24.dp))
        Surface(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 32.dp),
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.medium
        ) {
            ConstraintLayout(
                Modifier
                    .height(48.dp)
                    .fillMaxWidth()
                    .clickable {
                        coroutineScope.launch {
                            forumViewModel.subscribeToSupport()
                        }
                        navController.navigate("live_support") {
                            launchSingleTop = true
                        }
                    }
            ) {
                val (icon, label, arrow) = createRefs()
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.LiveHelp,
                    contentDescription = "",
                    Modifier
                        .size(32.dp)
                        .constrainAs(icon) {
                            start.linkTo(parent.start, margin = 8.dp)
                            centerVerticallyTo(parent)
                        },
                    tint = MaterialTheme.colorScheme.onBackground)

                Text(
                    text = stringResource(id = R.string.live_chat),
                    modifier = Modifier
                        .constrainAs(label) {
                            start.linkTo(icon.end, margin = 20.dp)
                            centerVerticallyTo(icon)
                        },
                    fontSize = TextUnit(16.0f, TextUnitType.Sp)
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "",
                    modifier = Modifier.constrainAs(arrow) {
                        centerVerticallyTo(icon)
                        end.linkTo(parent.end, margin = 16.dp)
                    }
                )
            }
        }

        //Spacer(modifier = Modifier.height(24.dp))
        Surface(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 24.dp),
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.medium
        ) {
            ConstraintLayout(
                Modifier
                    .height(48.dp)
                    .fillMaxWidth()
                    .clickable {
                        showSendEmailDialog = true
                    }
            ) {
                val (icon, label, arrow) = createRefs()
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "",
                    Modifier
                        .size(32.dp)
                        .constrainAs(icon) {
                            start.linkTo(parent.start, margin = 8.dp)
                            centerVerticallyTo(parent)
                        },
                    tint = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = stringResource(id = R.string.send_email),
                    modifier = Modifier
                        .constrainAs(label) {
                            start.linkTo(icon.end, margin = 20.dp)
                            centerVerticallyTo(icon) },
                    fontSize = TextUnit(16.0f, TextUnitType.Sp)
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "",
                    modifier = Modifier.constrainAs(arrow) {
                        centerVerticallyTo(icon)
                        end.linkTo(parent.end, margin = 16.dp)
                    }
                )
            }
        }

        Surface(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 24.dp),
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.medium
        ) {
            ConstraintLayout(
                Modifier
                    .height(48.dp)
                    .fillMaxWidth()
                    .clickable {
                        coroutineScope.launch {
                            homeViewModel.getFaqList()
                        }
                        navController.navigate("faq") {
                            launchSingleTop = true
                        }
                    }
            ) {
                val (icon, label, arrow) = createRefs()
                Icon(
                    imageVector = Icons.Default.QuestionMark,
                    contentDescription = "",
                    Modifier
                        .size(32.dp)
                        .constrainAs(icon) {
                            start.linkTo(parent.start, margin = 8.dp)
                            centerVerticallyTo(parent)
                        },
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(id = R.string.faq),
                        modifier = Modifier
                            .constrainAs(label) {
                                start.linkTo(icon.end, margin = 20.dp)
                                centerVerticallyTo(icon)
                            },
                        fontSize = TextUnit(16.0f, TextUnitType.Sp)
                    )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "",
                    modifier = Modifier.constrainAs(arrow) {
                        centerVerticallyTo(icon)
                        end.linkTo(parent.end, margin = 16.dp)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendEmailDialog(
    onDisplayChange: (show: Boolean) -> Unit
) {
    val sheetWidth = LocalConfiguration.current.screenWidthDp - 16
    val screenHeight = LocalConfiguration.current.screenHeightDp - 64
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var subject by rememberSaveable { mutableStateOf("") }
    var body by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    ModalBottomSheet(
        onDismissRequest = { onDisplayChange(false) },
        containerColor = MaterialTheme.colorScheme.background,
        sheetMaxWidth = sheetWidth.dp,
        sheetState = sheetState,
        tonalElevation = dimensionResource(id = R.dimen.low_elevation),
        modifier = Modifier.height(screenHeight.dp)
        ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text(text = "Subject", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 4.dp, end = 4.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = MaterialTheme.shapes.medium
            )

            TextField(
                value = body,
                onValueChange = { body = it },
                label = { Text(text = stringResource(id = R.string.body), color = Color.Gray) },
                modifier = Modifier
                    .height(320.dp)
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 4.dp, end = 4.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = MaterialTheme.shapes.medium
            )

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "message/rfc822"
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("megamentalityenterprise@gmail.com"))
                        putExtra(Intent.EXTRA_SUBJECT, subject)
                        putExtra(Intent.EXTRA_TEXT, body)
                    }
                    val chooserIntent = Intent.createChooser(intent, "Choose an app")
                    startActivity(context, chooserIntent, null)
                },
                modifier = Modifier
                    .width(200.dp)
                    .padding(top = 16.dp)) {
                Text(text = "Send")
            }
        }
    }
}
