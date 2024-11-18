package com.register.app.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.model.Member
import com.register.app.util.GenericTopBar
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch

@Composable
fun AssignSpecialLevi(
    activityViewModel: ActivityViewModel,
    groupViewModel: GroupViewModel,
    navController: NavController) {
    val selectedMember = groupViewModel.groupMemberLiveData.observeAsState().value
    Scaffold(
        topBar = { GenericTopBar(title = selectedMember?.fullName!!, navController = navController) }
    ) {
        AssignLevyUi(Modifier.padding(it), activityViewModel, groupViewModel, selectedMember, navController)
    }
}

@Composable
fun AssignLevyUi(
    modifier: Modifier,
    activityViewModel: ActivityViewModel,
    groupViewModel: GroupViewModel,
    selectedMember: Member?,
    navController: NavController
) {
    var levyTitle by rememberSaveable { mutableStateOf("") }
    var levyDescription by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    Column(
        Modifier
            .padding(top = 64.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(id = R.string.assign_levy_header),
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(id = R.string.levy_title),
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 16.dp)
            )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary),
            color = MaterialTheme.colorScheme.background
        ) {
            TextField(
                value = levyTitle,
                onValueChange = { levyTitle = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.text_field_height)),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                )
            )
        }

        Text(
            text = stringResource(id = R.string.levy_description),
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 16.dp)
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary),
            color = MaterialTheme.colorScheme.background
        ) {
            TextField(
                value = levyDescription,
                onValueChange = { levyDescription = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.text_field_height)),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                )
            )
        }

        Text(
            text = stringResource(id = R.string.levy_amount),
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 16.dp)
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary),
            color = MaterialTheme.colorScheme.background
        ) {
            TextField(
                value = amount,
                onValueChange = { amount = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.text_field_height)),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                )
            )
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    val response = activityViewModel.assignSpecialLevy(
                        levyTitle,
                        levyDescription,
                        amount.toDouble(),
                        group,
                        selectedMember)
                    Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                    if (response.status) {
                        navController.navigateUp()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 32.dp)
                .height(dimensionResource(id = R.dimen.button_height))
            ) {
            Text(text = stringResource(id = R.string.submit))
        }
    }
}
