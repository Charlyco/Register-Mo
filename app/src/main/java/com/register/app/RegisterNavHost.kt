package com.register.app

import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.register.app.screens.SendOtpScreen
import com.register.app.screens.Signup
import com.register.app.screens.SplashScreen
import com.register.app.screens.VerifyOtpScreen
import com.register.app.viewmodel.AuthViewModel

@Composable
fun RegisterAppNavHost(mainActivity: MainActivity) {
    val authViewModel: AuthViewModel by mainActivity.viewModels()

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(authViewModel, navController)
        }
        composable("signup") {
            Signup(authViewModel = authViewModel, navController = navController)
        }
        composable("otp") {
            SendOtpScreen(authViewModel, navController)
        }
        composable("otp_verify") {
            VerifyOtpScreen(authViewModel = authViewModel, navController = navController)
        }
    }

}