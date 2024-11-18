package com.register.app.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.TextButton
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.dto.FormUserResponseDto
import com.register.app.dto.QuestionnaireData
import com.register.app.dto.QuestionnaireEntry
import com.register.app.dto.QuestionnaireResponse
import com.register.app.util.CircularIndicator
import com.register.app.util.GenericTopBar
import com.register.app.viewmodel.GroupViewModel
import com.register.app.viewmodel.QuestionnaireViewModel
import kotlinx.coroutines.launch

@Composable
fun SubmitQuestionnaireResponse(
    questionnaireViewModel: QuestionnaireViewModel,
    groupViewModel: GroupViewModel,
    navController: NavController) {
    Scaffold(
        topBar = { GenericTopBar(
            title = "Questionnaire Response",
            navController = navController
        ) },
        containerColor = MaterialTheme.colorScheme.background
    ) {
        ResponseScreenContent(Modifier.padding(it), questionnaireViewModel, groupViewModel, navController)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ResponseScreenContent(
    modifier: Modifier,
    questionnaireViewModel: QuestionnaireViewModel,
    groupViewModel: GroupViewModel,
    navController: NavController) {
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    val membershipId = groupViewModel.membershipId.observeAsState().value
    val questionnaires = questionnaireViewModel.groupQuestionnaires.observeAsState().value
    val isRefreshing by rememberSaveable { mutableStateOf(false) }
    val isLoading = questionnaireViewModel.loadingState.observeAsState().value
    val downloadProgress = questionnaireViewModel.downloadProgress.observeAsState().value
    val responseList = questionnaireViewModel.questionnaireResponseList.observeAsState().value
    val coroutineScope = rememberCoroutineScope()
    val refreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            coroutineScope.launch {
                questionnaireViewModel.getQuestionnaires(group?.groupId!!)
            }
        },
        refreshThreshold = 84.dp,
        refreshingOffset = 64.dp)

    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 64.dp, start = 16.dp, end = 16.dp)
            .verticalScroll(rememberScrollState(initial = 0))
            .pullRefresh(refreshState),
        horizontalAlignment = Alignment.Start
    ) {
        questionnaires?.forEach { questionnaire ->
            QuestionnaireItem(questionnaire, membershipId, questionnaireViewModel, groupViewModel, navController)
        }
        if (isLoading == true) {
            CircularIndicator()
        }

        if (downloadProgress != null && (downloadProgress > 0 && downloadProgress < responseList?.size!!)) {
            ShowDownloadProgress(downloadProgress, responseList)
        }
    }
}

@Composable
fun ShowDownloadProgress(downloadProgress: Int, responseList: MutableList<FormUserResponseDto>) {
    val progressFraction = if (responseList.isNotEmpty()) {
        downloadProgress.toFloat() / responseList.size
    } else 0f

    Dialog(onDismissRequest = { }) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Downloading Files...",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                )

                Text(
                    text = "$downloadProgress of ${responseList.size} files downloaded"
                )
            }
        }
    }
}

@Composable
fun QuestionnaireItem(
    questionnaire: QuestionnaireData,
    membershipId: String?,
    questionnaireViewModel: QuestionnaireViewModel,
    groupViewModel: GroupViewModel,
    navController: NavController
) {
    var showQuestionnaireDetail by rememberSaveable { mutableStateOf(false) }
    var showContextMenu by rememberSaveable { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        if (showContextMenu) {
            QuestionnaireItemContextMenu(questionnaire, questionnaireViewModel, navController) {
                showContextMenu = it
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .clickable { showQuestionnaireDetail = !showQuestionnaireDetail }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { showQuestionnaireDetail = !showQuestionnaireDetail },
                        onLongPress = { showContextMenu = true },
                    )
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = questionnaire.title!!,
                fontSize = TextUnit(18.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold
            )
            if (showQuestionnaireDetail) {
                Icon(
                    painter = painterResource(id = R.drawable.up_arrow_solid),
                    contentDescription = "",
                    Modifier
                        .size(16.dp)
                        .clickable { showQuestionnaireDetail = false }
                )
            }else {
                Icon(
                    painter = painterResource(id = R.drawable.forward_arrow_solid),
                    contentDescription = "",
                    Modifier
                        .size(16.dp)
                        .clickable { showQuestionnaireDetail = true }
                )
            }
        }

        if (showQuestionnaireDetail && questionnaire.responders.contains(membershipId)) {
            Text(
                text = stringResource(id = R.string.already_responded),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                textAlign = TextAlign.Center,
                )
        } else if(showQuestionnaireDetail && !questionnaire.responders.contains(membershipId)) {
            QuestionnaireDetail(questionnaire, membershipId, questionnaireViewModel, navController)
        }
    }

}

@Composable
fun QuestionnaireItemContextMenu(
    questionnaire: QuestionnaireData,
    questionnaireViewModel: QuestionnaireViewModel,
    navController: NavController,
    onDismiss: (Boolean) -> Unit
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var showCompleteDialog by rememberSaveable { mutableStateOf(false) }
    var showDownloadDialog by rememberSaveable { mutableStateOf(false) }
    var showDownloadSummeryDialog by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Dialog(onDismissRequest = { onDismiss(false) }) {
        Surface(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.small
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(id = R.string.view_responses),
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    modifier = Modifier
                        .clickable {
                            coroutineScope.launch {
                                questionnaireViewModel.getUserResponses(questionnaire)
                                navController.navigate("user_responses/${questionnaire.title}") {
                                    launchSingleTop = true
                                }
                            }
                            onDismiss(false)
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = stringResource(id = R.string.download_responses),
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    modifier = Modifier
                        .clickable {
                            showDownloadDialog = true
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = stringResource(id = R.string.download_responses_summery),
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    modifier = Modifier
                        .clickable {
                            showDownloadSummeryDialog = true
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = stringResource(id = R.string.delete_questionnaire),
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    modifier = Modifier
                        .clickable {
                            showDeleteDialog = true
                            //onDismiss(false)
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground
                    )

                Text(
                    text = stringResource(id = R.string.mark_completed),
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    modifier = Modifier
                        .clickable {
                            showCompleteDialog = true
                            //onDismiss(false)
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    coroutineScope.launch {
                                        val groupId = questionnaire.groupId // Create an instance of the groupId before deletion
                                        val response = questionnaireViewModel.deleteQuestionnaire(questionnaire)
                                        Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                                        questionnaireViewModel.getQuestionnaires(groupId!!)
                                        //showDeleteDialog = false
                                        navController.navigateUp()
                                    }
                                }
                            ) {
                                Text(
                                    stringResource(id = R.string.delete),
                                    color = MaterialTheme.colorScheme.error)
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showDeleteDialog = false }
                            ) {
                                Text(stringResource(id = R.string.cancel))
                            }
                        },
                        title = { Text(text = stringResource(id = R.string.delete_questionnaire)) },
                        text = { Text(text = stringResource(id = R.string.delete_questionnaire_confirmation)) }
                    )
                }

                if (showCompleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showCompleteDialog = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    coroutineScope.launch {
                                        questionnaireViewModel.endQuestionnaire(questionnaire)
                                        questionnaireViewModel.getQuestionnaires(questionnaire.groupId!!)
                                        showCompleteDialog = false
                                    }
                                }
                            ) {
                                Text(
                                    stringResource(id = R.string.confirm),
                                    color = MaterialTheme.colorScheme.error)
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showCompleteDialog = false }
                            ) {
                                Text(stringResource(id = R.string.cancel))
                            }
                        },
                        title = { Text(text = stringResource(id = R.string.mark_completed)) },
                        text = { Text(text = stringResource(id = R.string.end_questionnaire_confirmation)) }
                    )
                }

                if (showDownloadDialog) {
                    AlertDialog(
                        onDismissRequest = { showDownloadDialog = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    coroutineScope.launch {
                                        val response = questionnaireViewModel.getUserResponses(questionnaire)
                                        questionnaireViewModel.downloadAllResponses(response?.data, context)
                                    }
                                    showDownloadDialog = false
                                }
                            ) {
                                Text(
                                    stringResource(id = R.string.download),
                                    color = MaterialTheme.colorScheme.error)
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showDownloadDialog = false }
                            ) {
                                Text(stringResource(id = R.string.cancel))
                            }
                        },
                        title = { Text(text = stringResource(id = R.string.download_responses)) },
                        text = { Text(text = stringResource(id = R.string.download_responses_confirmation)) }
                    )
                }

                if (showDownloadSummeryDialog) {
                    AlertDialog(
                        onDismissRequest = { showDownloadSummeryDialog = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    coroutineScope.launch {
                                        questionnaireViewModel.getResponsesSummery(questionnaire.formId, context)
                                    }
                                    showDownloadDialog = false
                                }
                            ) {
                                Text(
                                    stringResource(id = R.string.download),
                                    color = MaterialTheme.colorScheme.error)
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showDownloadDialog = false }
                            ) {
                                Text(stringResource(id = R.string.cancel))
                            }
                        },
                        title = { Text(text = stringResource(id = R.string.download_responses_summery)) },
                        text = { Text(text = stringResource(id = R.string.download_summery_confirmation)) }
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionnaireDetail(
    questionnaireData: QuestionnaireData,
    membershipId: String?,
    questionnaireViewModel: QuestionnaireViewModel,
    navController: NavController
) {
    val userResponses = mutableListOf<QuestionnaireResponse>()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        questionnaireData.questionnaire?.forEach { questionEntry ->
            SingleQuestion(questionEntry) {
                val found = userResponses.find { response -> response.question == it?.question }
                    if (found != null) {
                        userResponses.remove(found)
                        userResponses.add(it?: QuestionnaireResponse(questionEntry.question, "null"))
                    }else {
                        userResponses.add(it?: QuestionnaireResponse(questionEntry.question, "null"))
                    }
            }
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    val response = questionnaireViewModel.submitQuestionnaireResponse(userResponses, questionnaireData, membershipId)
                    if (response?.status == true) {
                        questionnaireViewModel.getQuestionnaires(questionnaireData.groupId!!)
                    }else {
                        Toast.makeText(context, response?.message, Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(text = stringResource(id = R.string.submit))
        }
    }
}

@Composable
fun SingleQuestion(
    questionEntry: QuestionnaireEntry,
    onResponse: (QuestionnaireResponse?) -> Unit
) {
    var selection by rememberSaveable { mutableStateOf("") }
    var response by rememberSaveable { mutableStateOf("") }
    Column {
        Text(
            text = questionEntry.question,
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Start
        )
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalAlignment = Alignment.Start
        ) {
            questionEntry.options.forEach { option ->
                if (option == "user_input") {
                    Surface(
                        Modifier
                            .height(dimensionResource(id = R.dimen.text_field_height))
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.background,
                        shape = MaterialTheme.shapes.small,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary)
                    ) {
                        TextField(
                            value = response,
                            onValueChange = {
                                response = it
                                onResponse(QuestionnaireResponse(questionEntry.question, response))
                                            },
                            label = { Text(text = stringResource(id = R.string.type_answer)) },
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                focusedContainerColor = MaterialTheme.colorScheme.background
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selection == option,
                            onClick = {
                                selection = option
                                onResponse(QuestionnaireResponse(questionEntry.question, selection))
                                      },
                        )
                        Text(
                            text = option,
                            fontSize = TextUnit(14.0f, TextUnitType.Sp),
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    }
}
