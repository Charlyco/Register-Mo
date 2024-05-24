package com.register.app

import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.register.app.screens.Chatroom
import com.register.app.screens.EventDetails
import com.register.app.screens.Events
import com.register.app.screens.AllGroups
import com.register.app.screens.EvidenceOfPayment
import com.register.app.screens.GroupDetail
import com.register.app.screens.HomeScreen
import com.register.app.screens.LoginScreen
import com.register.app.screens.SendOtpScreen
import com.register.app.screens.Signup
import com.register.app.screens.SplashScreen
import com.register.app.screens.VerifyOtpScreen
import com.register.app.util.DataStoreManager
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.GroupViewModel
import com.register.app.viewmodel.HomeViewModel

@Composable
fun RegisterAppNavHost(mainActivity: MainActivity, dataStoreManager: DataStoreManager) {
    val authViewModel: AuthViewModel by mainActivity.viewModels()
    val homeViewModel: HomeViewModel by mainActivity.viewModels()
    val groupViewModel: GroupViewModel by mainActivity.viewModels()

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(authViewModel, navController)
        }
        composable("signup") {
            Signup(authViewModel = authViewModel, navController = navController, dataStoreManager)
        }
        composable("otp") {
            SendOtpScreen(authViewModel, navController)
        }
        composable("otp_verify") {
            VerifyOtpScreen(authViewModel = authViewModel, navController = navController)
        }
        composable("signin") {
            LoginScreen(authViewModel = authViewModel, navController = navController, dataStoreManager)
        }
        composable("home") {
            HomeScreen(homeViewModel = homeViewModel, navController = navController, dataStoreManager = dataStoreManager)
        }
        composable("chats") {
            Chatroom()
        }
        composable("events") {
            Events()
        }
        composable("event_detail") {
            EventDetails(dataStoreManager = dataStoreManager, navController = navController, homeViewModel = homeViewModel)
        }
        composable("groups") {
            AllGroups(navController = navController, dataStoreManager = dataStoreManager, groupViewModel = groupViewModel)
        }
        composable("payment") {
            EvidenceOfPayment(navController = navController, groupViewModel = groupViewModel)
        }
        composable("group_detail") {
            GroupDetail(navController = navController, groupViewModel = groupViewModel)
        }
    }
}