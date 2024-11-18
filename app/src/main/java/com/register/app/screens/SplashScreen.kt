package com.register.app.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.util.DataStoreManager
import com.register.app.util.ONBOARDING
import com.register.app.util.SPLASH_SCREEN_KEY
import com.register.app.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
    dataStoreManager: DataStoreManager
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LaunchedEffect(key1 = SPLASH_SCREEN_KEY) {
            delay(2000)
            if (dataStoreManager.readUserData()?.userId != null) {
                if (dataStoreManager.readLoginType() == true) {
                    navController.navigate("signin") {
                        popUpTo("splash") {inclusive = true}
                    }
                } else {
                    Log.d("USER:", dataStoreManager.readUserData().toString())
                    if (authViewModel.shouldLogin()) {
                        navController.navigate("signin") {
                            popUpTo("splash") {inclusive = true}
                        }
                    }else {
                        if(authViewModel.shouldRefreshToken()) {
                            authViewModel.getUserDetails()
                            authViewModel.refreshToken()
                            navController.navigate("home") {
                                popUpTo("splash") { inclusive = true }
                            }
                        } else {
                            authViewModel.getUserDetails()
                            navController.navigate("home") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                    }
                }
            } else {
                navController.navigate(ONBOARDING) {
                    popUpTo("splash") {inclusive = true}
                }
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