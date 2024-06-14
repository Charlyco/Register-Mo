package com.register.app.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.register.app.R
import com.register.app.util.SPLASH_SCREEN_KEY
import com.register.app.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(authViewModel: AuthViewModel, navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LaunchedEffect(key1 = SPLASH_SCREEN_KEY) {
            delay(3000)
            navController.navigate("auth") {
                popUpTo("splash") {inclusive = true}
            }
        }

        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
           val (logo, text) = createRefs()

           Image(
               painter = painterResource(id = R.drawable.register_logo),
               contentDescription = stringResource(id = R.string.app_logo),
               modifier = Modifier
                   .size(120.dp)
                   .constrainAs(logo) {
                   centerHorizontallyTo(parent)
                   centerVerticallyTo(parent)
               }
           )
        }
    }
}