package com.register.app.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.register.app.util.GenericTopBar
import com.register.app.viewmodel.QuestionnaireViewModel

@Composable
fun QuestionnaireResponses(
    questionnaireViewModel: QuestionnaireViewModel,
    navController: NavController,
    formTitle: String?
) {
    Scaffold(
        topBar = { GenericTopBar(title = formTitle!!, navController =navController, navRoute = "quest_response")}
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
}
