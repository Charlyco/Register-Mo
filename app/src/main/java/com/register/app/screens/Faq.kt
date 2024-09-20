package com.register.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.model.Faq
import com.register.app.util.GenericTopBar
import com.register.app.viewmodel.HomeViewModel

@Composable
fun Faq(homeViewModel: HomeViewModel, navController: NavController) {
    Scaffold(
        topBar = { GenericTopBar(title = "FAQ", navController = navController) },
    ) {
        FaqScreen(Modifier.padding(it), homeViewModel, navController)
    }
}

@Composable
fun FaqScreen(modifier: Modifier, homeViewModel: HomeViewModel, navController: NavController) {
    val faqList = homeViewModel.faqListLiveData.observeAsState().value
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState(initial = 0)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            faqList?.forEach { faq ->
                FaqItemHeader(faq)
            }
        }
    }
}

@Composable
fun FaqItemHeader(faq: Faq) {
    val context = LocalContext.current
    var showAnswer by rememberSaveable { mutableStateOf(false) }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clickable { showAnswer != showAnswer },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = faq.question,
            fontSize = TextUnit(14.0f, TextUnitType.Sp),
            color = Color(context.getColor(R.color.purple_500))
        )
        if (showAnswer) {
            Icon(
                painter = painterResource(id = R.drawable.up_arrow_solid),
                contentDescription = "",
                Modifier
                    .size(16.dp)
                    .clickable { showAnswer = false },
                tint = Color(context.getColor(R.color.purple_500))
            )
            Text(
                text = faq.answer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start
            )
        }else {
            Icon(
                painter = painterResource(id = R.drawable.forward_arrow_solid),
                contentDescription = "",
                Modifier
                    .size(16.dp)
                    .clickable { showAnswer = true },
                tint = Color(context.getColor(R.color.purple_500))
            )
        }
    }
}
