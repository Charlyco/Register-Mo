package com.register.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.dto.FormUserResponseDto
import com.register.app.util.GenericTopBar
import com.register.app.viewmodel.QuestionnaireViewModel

@Composable
fun QuestionnaireResponses(
    questionnaireViewModel: QuestionnaireViewModel,
    navController: NavController,
    formTitle: String?
) {
    Scaffold(
        topBar = { GenericTopBar(title = formTitle!!, navController =navController)}
    ) {
        QuestionnaireResponsesList(Modifier.padding(it), questionnaireViewModel, navController)
    }
}

@Composable
fun QuestionnaireResponsesList(
    modifier: Modifier,
    questionnaireViewModel: QuestionnaireViewModel,
    navController: NavController
) {
    val responseList = questionnaireViewModel.questionnaireResponseList.observeAsState().value

    if (!responseList.isNullOrEmpty()) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(top = 64.dp),
            horizontalAlignment = Alignment.Start,
            state = rememberLazyListState()
        ) {
            items(responseList) {response ->
                ResponseItem(response)
            }
        }
    }

}

@Composable
fun ResponseItem(response: FormUserResponseDto) {
    var showResponseDetail by rememberSaveable {  mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { showResponseDetail = !showResponseDetail },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = response.fullName!!,
                fontSize = TextUnit(18.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold
            )
            if (showResponseDetail) {
                Icon(
                    painter = painterResource(id = R.drawable.up_arrow_solid),
                    contentDescription = "",
                    Modifier
                        .size(16.dp)
                        .clickable { showResponseDetail = false }
                )
            }else {
                Icon(
                    painter = painterResource(id = R.drawable.forward_arrow_solid),
                    contentDescription = "",
                    Modifier
                        .size(16.dp)
                        .clickable { showResponseDetail = true }
                )
            }
        }
        if (showResponseDetail) {
            ResponseItemDetail(response)
        }
    }
}

@Composable
fun ResponseItemDetail(response: FormUserResponseDto) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        response.content?.forEach{ entry ->
            Text(
                text = "Question: ${entry?.question}",
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = "Response: ${entry?.response}",
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                modifier = Modifier.padding(bottom = 8.dp)
                )
        }
    }
}


