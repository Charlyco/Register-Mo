package com.register.app.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalConfiguration
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
import com.register.app.dto.QuestionnaireEntry
import com.register.app.util.CircularIndicator
import com.register.app.util.GenericTopBar
import com.register.app.viewmodel.GroupViewModel
import com.register.app.viewmodel.QuestionnaireViewModel
import kotlinx.coroutines.launch

@Composable
fun CreateQuestionnaireScreen(
    navController: NavController,
    questionnaireViewModel: QuestionnaireViewModel,
    groupViewModel: GroupViewModel
) {
    val isLoading = questionnaireViewModel.loadingState.observeAsState().value
    Scaffold(
        topBar = { GenericTopBar(
            title = stringResource(id = R.string.questionnaire),
            navController = navController
        )},
        containerColor = MaterialTheme.colorScheme.background
    ) {
        CreateQuestionnaireScreenContent(Modifier.padding(it), questionnaireViewModel, groupViewModel, navController)
        if (isLoading ==  true) {
            CircularIndicator()
        }
    }
}

@Composable
fun CreateQuestionnaireScreenContent(
    modifier: Modifier,
    questionnaireViewModel: QuestionnaireViewModel,
    groupViewModel: GroupViewModel,
    navController: NavController
) {
    var title by rememberSaveable { mutableStateOf("") }

    Column(
        Modifier
            .padding(top = 64.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState(initial = 0)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TitleInputBox() { title = it }
        QuestionnairePreview(questionnaireViewModel)
        HorizontalDivider(Modifier.padding(horizontal = 8.dp))
        QuestionsInputSection(questionnaireViewModel)
        SubmitButton(questionnaireViewModel, navController, groupViewModel, title)
    }
}

@Composable
fun QuestionnairePreview(
    questionnaireViewModel: QuestionnaireViewModel
) {
    val questions = questionnaireViewModel.questionnaireEntries.observeAsState().value
    var selection by rememberSaveable { mutableStateOf("") }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        questions?.forEach { questionEntry ->
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = questionEntry.question,
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Start
                )

                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .clickable {
                            questionnaireViewModel.deleteQuestionnaireEntry(questionEntry)
                        }
                        .size(20.dp))
            }

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
                                value = "",
                                onValueChange = {},
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
                    }else {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selection == option,
                                onClick = { selection = option },
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
}

@Composable
fun QuestionsInputSection(questionnaireViewModel: QuestionnaireViewModel) {
    var question by rememberSaveable { mutableStateOf("") }
    var options by rememberSaveable { mutableStateOf<List<String>?>(mutableListOf()) }
    var currentOption by rememberSaveable { mutableStateOf("") }
    val screenWidth = LocalConfiguration.current.screenWidthDp
    var requireUserInput by rememberSaveable { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(id = R.string.enter_question),
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Surface(
            Modifier
                .height(dimensionResource(id = R.dimen.text_field_height))
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary)

        ) {
            TextField(
                value = question,
                onValueChange = {
                    question = it
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background
                ),
                modifier = Modifier.fillMaxSize()
            )
        }

        options?.forEach { option ->
            Row(
                Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = option,
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .clickable {
                            val tempList = options!!.toMutableList()
                            tempList.remove(option)
                            options = tempList
                        }
                        .size(20.dp))
            }
        }

        if (question.isNotEmpty() && !requireUserInput) {
            Row(
                Modifier
                    .padding(top = 8.dp, start = 16.dp)
                    .width((screenWidth - 42).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    Modifier
                        .height(dimensionResource(id = R.dimen.text_field_height)),
                    color = MaterialTheme.colorScheme.background,
                    shape = MaterialTheme.shapes.small,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary)

                ){
                    TextField(
                        value = currentOption,
                        onValueChange = {
                            currentOption = it
                        },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedContainerColor = MaterialTheme.colorScheme.background
                        )
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable {
                            val tempOptionsList = options?.toMutableList()
                            tempOptionsList?.add(currentOption)
                            options = tempOptionsList
                            currentOption = ""
                        }
                )
            }
        }

        if (question.isNotEmpty()) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = requireUserInput,
                    onCheckedChange = { requireUserInput = !requireUserInput },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.secondary,
                        uncheckedColor = MaterialTheme.colorScheme.tertiary,
                        checkmarkColor = MaterialTheme.colorScheme.onBackground
                    )
                )
                Text(
                    text = stringResource(id = R.string.require_user_input),
                    fontSize = TextUnit(14.0f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Button(
                onClick = {
                    if (requireUserInput) {
                        if (options.isNullOrEmpty()) {
                            options = listOf("user_input")
                            questionnaireViewModel.addQuestion(QuestionnaireEntry(question, options!!))
                            question = ""
                            options = listOf() // reinitialize options with empty list
                        }
                    }else {
                        questionnaireViewModel.addQuestion(QuestionnaireEntry(question, options!!))
                        question = ""
                        if (!options.isNullOrEmpty()) {
                            //val tempList = options!!.toMutableList()
                            options = listOf() // reinitialize options with empty list
                        }
                    }
                },
                modifier = Modifier
                    .width(120.dp)
                    .padding(top = 16.dp)
            ) {
                Text(text = stringResource(id = R.string.add))
            }
        }
    }
}

@Composable
fun TitleInputBox(callback: (String) -> Unit) {
    var title by rememberSaveable { mutableStateOf("") }
    Column(
        Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(id = R.string.enter_title),
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
            )

        Surface(
            Modifier
                .height(dimensionResource(id = R.dimen.text_field_height))
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary)

        ) {
            TextField(
                value = title,
                onValueChange = {
                    title = it
                    callback(title)
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background
                ),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun SubmitButton(
    questionnaireViewModel: QuestionnaireViewModel,
    navController: NavController,
    groupViewModel: GroupViewModel,
    title: String
) {
    val group = groupViewModel.groupDetailLiveData.observeAsState().value
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    Button(
        onClick = {
            coroutineScope.launch {
                val response = questionnaireViewModel.postQuestionnaire(title, group)
                Toast.makeText(context, response?.message, Toast.LENGTH_LONG).show()
                if (response?.status ==  true) {
                    navController.navigateUp()
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
            .height(dimensionResource(id = R.dimen.button_height)),
        shape = MaterialTheme.shapes.small
    ) {
        Text(text = stringResource(id = R.string.publish))
    }
}