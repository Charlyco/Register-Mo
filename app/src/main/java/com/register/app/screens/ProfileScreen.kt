package com.register.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.util.BottomNavBar
import com.register.app.util.ImageLoader
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.GroupViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    groupViewModel: GroupViewModel,
    navController: NavController
) {
    Scaffold(
        topBar = {},
        bottomBar = { BottomNavBar(navController = navController)},
        containerColor = MaterialTheme.colorScheme.background
    ) {
        ProfileScreenUi(Modifier.padding(it), authViewModel, groupViewModel, navController)
    }
}

@Composable
fun ProfileScreenUi(
    modifier: Modifier,
    authViewModel: AuthViewModel,
    groupViewModel: GroupViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val user = authViewModel.userLideData.observeAsState().value
    val scrollState = rememberScrollState(initial = 0)
    ConstraintLayout(
        Modifier
            .verticalScroll(scrollState)
            .padding(bottom = 72.dp)
            .fillMaxWidth()
            .clickable {  }
    ) {
        val (bgImage, profilePic, name, phone, address, card) = createRefs()

        Surface(
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 42.dp, bottomEnd = 42.dp))
                .constrainAs(bgImage) {
                    top.linkTo(parent.top)
                    centerHorizontallyTo(parent)
                },
            color = MaterialTheme.colorScheme.primary
            ){}
        Surface(
            Modifier
                .size(160.dp)
                .clip(CircleShape)
                .constrainAs(profilePic) {
                    top.linkTo(bgImage.bottom, margin = (-70).dp)
                    centerHorizontallyTo(parent)
                },
            color = MaterialTheme.colorScheme.background,
        ) {
            ImageLoader(imageUrl = user?.imageUrl?: "", context = context, height = 158, width = 158, placeHolder = R.drawable.placeholder)
        }

        Text(
            text = user?.fullName!!,
            color = MaterialTheme.colorScheme.primary,
            fontSize = TextUnit(20.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.constrainAs(name) {
                top.linkTo(profilePic.bottom, margin = 16.dp)
                centerHorizontallyTo(parent)
            }
        )

        Text(
            text = user.phoneNumber,
            color = Color.DarkGray,
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            modifier = Modifier.constrainAs(phone) {
                top.linkTo(name.bottom, margin = 4.dp)
                centerHorizontallyTo(parent)
            }
        )

        user.address?.let {
            Text(
                text = it,
                color = Color.DarkGray,
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .constrainAs(address) {
                        top.linkTo(phone.bottom, margin = 4.dp)
                        centerHorizontallyTo(parent)
                    },
                textAlign = TextAlign.Center
            )
        }

        Surface(
            modifier = Modifier
                .padding(16.dp)
                .constrainAs(card) {
                    centerHorizontallyTo(parent)
                    top.linkTo(address.bottom)
                },
            shape = MaterialTheme.shapes.large,
            shadowElevation = dimensionResource(id = R.dimen.low_elevation),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                ConstraintLayout(
                    Modifier
                        .padding(top = 20.dp)
                        .height(48.dp)
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("edit_profile") {
                                launchSingleTop = true
                            }
                        }
                ) {
                    val (icon, label, arrow) = createRefs()
                    
                    Surface(
                        Modifier
                            .size(48.dp)
                            .constrainAs(icon) {
                                start.linkTo(parent.start, margin = 8.dp)
                                centerVerticallyTo(parent)
                            },
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.tertiary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "",
                            Modifier.padding(8.dp),
                            tint = MaterialTheme.colorScheme.onBackground)
                    }
                    
                    Text(
                        text = stringResource(id = R.string.edit_Profile),
                        modifier = Modifier
                            .constrainAs(label) {
                                start.linkTo(icon.end, margin = 20.dp)
                                centerVerticallyTo(icon)
                            },
                        fontSize = TextUnit(17.0f, TextUnitType.Sp)
                        )
                    
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "",
                        modifier = Modifier.constrainAs(arrow) {
                            centerVerticallyTo(icon)
                            end.linkTo(parent.end, margin = 16.dp)
                        })
                }
                ConstraintLayout(
                    Modifier
                        .padding(top = 20.dp)
                        .height(48.dp)
                        .fillMaxWidth()
                        .clickable {  }
                ) {
                    val (icon, label, arrow) = createRefs()

                    Surface(
                        Modifier
                            .size(48.dp)
                            .constrainAs(icon) {
                                start.linkTo(parent.start, margin = 8.dp)
                                centerVerticallyTo(parent)
                            },
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.tertiary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "",
                            Modifier.padding(8.dp),
                            tint = MaterialTheme.colorScheme.onBackground)
                    }

                    Text(
                        text = stringResource(id = R.string.settings),
                        modifier = Modifier
                            .constrainAs(label) {
                                start.linkTo(icon.end, margin = 20.dp)
                                centerVerticallyTo(icon)
                            },
                        fontSize = TextUnit(17.0f, TextUnitType.Sp)
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "",
                        modifier = Modifier.constrainAs(arrow) {
                            centerVerticallyTo(icon)
                            end.linkTo(parent.end, margin = 16.dp)
                        })
                }
                ConstraintLayout(
                    Modifier
                        .padding(top = 20.dp)
                        .height(48.dp)
                        .fillMaxWidth()
                        .clickable {  }
                ) {
                    val (icon, label, arrow) = createRefs()

                    Surface(
                        Modifier
                            .size(48.dp)
                            .constrainAs(icon) {
                                start.linkTo(parent.start, margin = 8.dp)
                                centerVerticallyTo(parent)
                            },
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.tertiary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "",
                            Modifier.padding(8.dp),
                            tint = MaterialTheme.colorScheme.onBackground)
                    }

                    Text(
                        text = stringResource(id = R.string.notifications),
                        modifier = Modifier
                            .constrainAs(label) {
                                start.linkTo(icon.end, margin = 20.dp)
                                centerVerticallyTo(icon)
                            },
                        fontSize = TextUnit(17.0f, TextUnitType.Sp)
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "",
                        modifier = Modifier.constrainAs(arrow) {
                            centerVerticallyTo(icon)
                            end.linkTo(parent.end, margin = 16.dp)
                        })
                }
                ConstraintLayout(
                    Modifier
                        .padding(top = 20.dp)
                        .height(48.dp)
                        .fillMaxWidth()
                        .clickable {  }
                ) {
                    val (icon, label, arrow) = createRefs()

                    Surface(
                        Modifier
                            .size(48.dp)
                            .constrainAs(icon) {
                                start.linkTo(parent.start, margin = 8.dp)
                                centerVerticallyTo(parent)
                            },
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.tertiary
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = "",
                            Modifier.padding(8.dp),
                            tint = MaterialTheme.colorScheme.onBackground)
                    }

                    Text(
                        text = stringResource(id = R.string.help),
                        modifier = Modifier
                            .constrainAs(label) {
                                start.linkTo(icon.end, margin = 20.dp)
                                centerVerticallyTo(icon)
                            },
                        fontSize = TextUnit(17.0f, TextUnitType.Sp)
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
                ConstraintLayout(
                    Modifier
                        .padding(top = 20.dp, bottom = 20.dp)
                        .height(48.dp)
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("signin") {
                                popUpTo("home") {
                                    inclusive = true
                                }
                            }
                        }
                ) {
                    val (icon, label, arrow) = createRefs()

                    Surface(
                        Modifier
                            .size(48.dp)
                            .constrainAs(icon) {
                                start.linkTo(parent.start, margin = 8.dp)
                                centerVerticallyTo(parent)
                            },
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.tertiary
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "",
                            Modifier.padding(8.dp),
                            tint = MaterialTheme.colorScheme.onBackground)
                    }

                    Text(
                        text = stringResource(id = R.string.sign_out),
                        modifier = Modifier
                            .constrainAs(label) {
                                start.linkTo(icon.end, margin = 20.dp)
                                centerVerticallyTo(icon)
                            },
                        fontSize = TextUnit(17.0f, TextUnitType.Sp)
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
}
