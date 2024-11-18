package com.register.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.util.GenericTopBar
import com.register.app.viewmodel.GroupViewModel
import com.register.app.viewmodel.HomeViewModel

@Composable
fun DiscoverScreen(
    groupViewModel: GroupViewModel,
    homeViewModel: HomeViewModel,
    navController: NavController
) {
    Scaffold(
        topBar = { GenericTopBar(
            title = "Find Colleagues",
            navController = navController
        )}
    ) {
        DiscoverScreenUi(Modifier.padding(it), groupViewModel, homeViewModel, navController)
    }
}

@Composable
fun DiscoverScreenUi(
    modifier: Modifier,
    groupViewModel: GroupViewModel,
    homeViewModel: HomeViewModel,
    navController: NavController
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            border = BorderStroke(1.dp, Color.Gray),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            ConstraintLayout(
                Modifier.fillMaxWidth()
            )
            {
                val (text, image, icon) = createRefs()

                Column(
                    modifier = Modifier
                        .width(248.dp)
                        .padding(8.dp)
                        .constrainAs(text) {
                            start.linkTo(parent.start)
                            centerVerticallyTo(parent)
                        }
                ) {
                    Text(
                        text = stringResource(id = R.string.discover),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = TextUnit(16.0f, TextUnitType.Sp)
                    )
                    Text(
                        text = stringResource(id = R.string.discover_desc),
                        fontSize = TextUnit(14.0f, TextUnitType.Sp)
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.search_group1),
                    contentDescription = "",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .constrainAs(image) {
                            end.linkTo(parent.end)
                            centerVerticallyTo(parent)
                        },
                    contentScale = ContentScale.Fit
                )
                Icon(
                    painter = painterResource(id = R.drawable.caret_forward_circle_outline),
                    contentDescription = "",
                    Modifier
                        .size(48.dp)
                        .clickable { }
                        .constrainAs(icon) {
                            centerVerticallyTo(parent)
                            end.linkTo(parent.end, margin = 24.dp)
                        })
            }
        }
        Surface(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            border = BorderStroke(1.dp, Color.Gray),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            ConstraintLayout(
                Modifier.fillMaxWidth())
            {
                val (text, image, icon) = createRefs()
                Column(
                    modifier = Modifier
                        .width(248.dp)
                        .padding(8.dp)
                        .constrainAs(text) {
                            start.linkTo(parent.start)
                            centerVerticallyTo(parent)
                        }
                ) {
                    Text(
                        text = stringResource(id = R.string.discover_friends),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = TextUnit(16.0f, TextUnitType.Sp)
                    )
                    Text(
                        text = stringResource(id = R.string.find_friends_desc),
                        fontSize = TextUnit(14.0f, TextUnitType.Sp)
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.search_friends),
                    contentDescription = "",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .constrainAs(image) {
                            end.linkTo(parent.end)
                            centerVerticallyTo(parent)
                        },
                    contentScale = ContentScale.Fit)

                Icon(
                    painter = painterResource(id = R.drawable.caret_forward_circle_outline),
                    contentDescription = "",
                    Modifier
                        .size(48.dp)
                        .clickable { }
                        .constrainAs(icon) {
                            centerVerticallyTo(parent)
                            end.linkTo(parent.end, margin = 24.dp)
                        })
            }

        }
        Text(text = "UNDER DEVELOPMENT")
    }
}

