package com.register.app.util

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.register.app.R
import com.register.app.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun OtpTextField(
    otpCount: Int = 6,
    authViewModel: AuthViewModel?,
    email: String
) {
    var otp by rememberSaveable { mutableStateOf("") }
    var otpLength by rememberSaveable { mutableIntStateOf(0) }
    var isDone by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var isError by rememberSaveable { mutableStateOf<Boolean?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicTextField(
            value = otp,
            onValueChange = {
                isError = null
                if (it.length <= otpCount) {
                    otp = it
                    otpLength++
                }
                if (otpLength == otpCount) {
                    coroutineScope.launch {
                        val response = authViewModel?.verifyOtp(otp.toInt(), email)
                        isError = response?.status!!
                    }
                    isDone = true
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            decorationBox = {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(otpCount) { index ->
                        CharView(
                            index = index,
                            text = otp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        )

        if (isError != null && isError == false) {
            Text(
                text = stringResource(id = R.string.otp_error),
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp)
            )
            otp = ""
            otpLength = 0
        }
    }
}

@Composable
private fun CharView(
    index: Int,
    text: String
) {
    val isFocused = text.length == index
    val char = when {
        index == text.length -> " "
        index > text.length -> ""
        else -> text[index].toString()
    }
    Text(text = char,
        modifier = Modifier
            .paddingFromBaseline(top = 16.dp)
            .size(48.dp)
            .border(
                1.dp, when {
                    isFocused -> Color.DarkGray
                    else -> Color.LightGray
                }, RoundedCornerShape(8.dp)
            )
            .padding(2.dp),

        style = MaterialTheme.typography.bodyMedium,
        color = if (isFocused) {
            Color.LightGray
        } else {
            Color.DarkGray
        },
        textAlign = TextAlign.Center,
        fontSize = TextUnit(32.0f, TextUnitType.Sp)
    )
}