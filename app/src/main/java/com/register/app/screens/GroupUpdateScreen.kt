package com.register.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.model.Group
import com.register.app.util.GenericTopBar
import com.register.app.util.ImageLoader
import com.register.app.viewmodel.GroupViewModel

@Composable
fun GroupUpdateScreen(navController: NavController, groupViewModel: GroupViewModel) {
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    Scaffold(
        topBar = { GenericTopBar(title = stringResource(id = R.string.update_group), navController = navController, navRoute = "group_detail")}
    ) {
        if (group != null) {
            GroupUpdateUi(Modifier.padding(it), group, navController, groupViewModel)
        }
    }
}

@Composable
fun GroupUpdateUi(
    modifier: Modifier,
    group: Group,
    navController: NavController,
    groupViewModel: GroupViewModel
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState(initial = 0)
    var groupName by rememberSaveable { mutableStateOf(group.groupName)}
    var description by rememberSaveable { mutableStateOf(group.groupDescription) }
    var address by rememberSaveable { mutableStateOf(group.address) }
    var phone by rememberSaveable { mutableStateOf(group.phone) }
    var email by rememberSaveable { mutableStateOf(group.email) }
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .padding(top = 64.dp)
            .verticalScroll(scrollState)
    ) {
        val (logo, details, saveBtn) = createRefs()

        Box(
            Modifier
                .size(200.dp)
                .clip(CircleShape)
                .constrainAs(logo) {
                    top.linkTo(parent.top, margin = 32.dp)
                    centerHorizontallyTo(parent)
                },
            contentAlignment = Alignment.CenterEnd
        ) {
            ImageLoader(
                imageUrl = group.logoUrl,
                context = context,
                height = 200,
                width = 200,
                placeHolder = R.drawable.download
            )

            Icon(
                imageVector = Icons.Default.CameraEnhance,
                contentDescription = "",
                modifier = Modifier
                    .clickable { }
                    .size(40.dp))
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .constrainAs(details) {
                    centerHorizontallyTo(parent)
                    top.linkTo(logo.bottom, margin = 24.dp)
                },
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(id = R.string.group_name),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold
            )
            Surface(
                Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                border = BorderStroke(1.dp, Color.Gray),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background
            ) {
            TextField(
                value = groupName,
                onValueChange = { groupName = it },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Text(
                text = stringResource(id = R.string.group_description),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Surface(
                Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                border = BorderStroke(1.dp, Color.Gray),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background
            ) {
                TextField(
                    value = description!!,
                    onValueChange = { description = it },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Text(
                text = stringResource(id = R.string.address),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Surface(
                Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                border = BorderStroke(1.dp, Color.Gray),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background
            ) {
                TextField(
                    value = address!!,
                    onValueChange = { address = it },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Text(
                text = stringResource(id = R.string.phone),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Surface(
                Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                border = BorderStroke(1.dp, Color.Gray),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background
            ) {
                TextField(
                    value = phone!!,
                    onValueChange = { phone = it },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Text(
                text = stringResource(id = R.string.email),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Surface(
                Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                border = BorderStroke(1.dp, Color.Gray),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background
            ) {
                TextField(
                    value = email!!,
                    onValueChange = { email = it },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }

        Button(
            onClick = { groupViewModel.saveGroupUpdate() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .constrainAs(saveBtn) {
                    top.linkTo(details.bottom, margin = 24.dp)
                    centerHorizontallyTo(parent)
                }
            ) {
            Text(text = stringResource(id = R.string.save))
        }
    }
}