package com.register.app

import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.register.app.screens.AddRemoveMember
import com.register.app.screens.EventDetails
import com.register.app.screens.Events
import com.register.app.screens.AllGroups
import com.register.app.screens.AuthScreen
import com.register.app.screens.CreateEvent
import com.register.app.screens.DiscoverScreen
import com.register.app.screens.EditProfile
import com.register.app.screens.EvidenceOfPayment
import com.register.app.screens.Forum
import com.register.app.screens.GroupDetail
import com.register.app.screens.GroupUpdateScreen
import com.register.app.screens.HomeScreen
import com.register.app.screens.LoginScreen
import com.register.app.screens.MemberDetails
import com.register.app.screens.MembershipRequests
import com.register.app.screens.ModifyAdmin
import com.register.app.screens.ProfileScreen
import com.register.app.screens.SignUpCont
import com.register.app.screens.Signup
import com.register.app.screens.SplashScreen
import com.register.app.screens.SuggestedGroups
import com.register.app.screens.VerifyOtpScreen
import com.register.app.util.DataStoreManager
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.ForumViewModel
import com.register.app.viewmodel.GroupViewModel
import com.register.app.viewmodel.HomeViewModel

@Composable
fun RegisterAppNavHost(mainActivity: MainActivity, dataStoreManager: DataStoreManager) {
    val authViewModel: AuthViewModel by mainActivity.viewModels()
    val homeViewModel: HomeViewModel by mainActivity.viewModels()
    val groupViewModel: GroupViewModel by mainActivity.viewModels()
    val forumViewModel: ForumViewModel by mainActivity.viewModels()
    val activityViewModel: ActivityViewModel by mainActivity.viewModels()

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(authViewModel, navController, dataStoreManager)
        }
        composable("signup") {
            Signup(authViewModel = authViewModel, navController = navController, dataStoreManager)
        }
        composable("sinup_cont") {
            SignUpCont(authViewModel, navController)
        }
        composable("otp_verify/{email}") {
            VerifyOtpScreen(authViewModel = authViewModel, navController = navController, email = it.arguments?.getString("email"))
        }
        composable("signin") {
            LoginScreen(authViewModel = authViewModel, navController = navController, dataStoreManager)
        }
        composable("home") {
            HomeScreen(homeViewModel = homeViewModel, navController = navController, groupViewModel = groupViewModel, authViewModel = authViewModel, activityViewModel = activityViewModel)
        }
        composable("colleagues") {
            DiscoverScreen(groupViewModel = groupViewModel, homeViewModel = homeViewModel, navController = navController)
        }
        composable("events/{title}") { backStackEntry ->
            Events(navController = navController, activityViewModel = activityViewModel, groupViewModel = groupViewModel, authViewModel = authViewModel, title = backStackEntry.arguments?.getString("title"))
        }
        composable("event_detail") {
            EventDetails(dataStoreManager = dataStoreManager, navController = navController, activityViewModel = activityViewModel, groupViewModel = groupViewModel, authViewModel = authViewModel)
        }
        composable("groups") {
            AllGroups(navController = navController, dataStoreManager = dataStoreManager, groupViewModel = groupViewModel)
        }
        composable("payment") {
            EvidenceOfPayment(navController = navController, groupViewModel = groupViewModel, activityViewModel = activityViewModel)
        }
        composable("group_detail") {
            GroupDetail(navController = navController,
                groupViewModel = groupViewModel,
                authViewModel = authViewModel,
                forumViewModel = forumViewModel,
                homeViewModel = homeViewModel,
                activityViewModel = activityViewModel)
        }
        composable("profile") {
            ProfileScreen(authViewModel, groupViewModel, navController)
        }
        composable("forum") {
            Forum(forumViewModel = forumViewModel, groupViewModel = groupViewModel, navController = navController)
        }
        composable("onboard") {
            AuthScreen(navController = navController, dataStoreManager = dataStoreManager)
        }
        composable("update_group") {
            GroupUpdateScreen(navController = navController, groupViewModel = groupViewModel)
        }
        composable("create_event") {
            CreateEvent(groupViewModel = groupViewModel, activityViewModel = activityViewModel, navController = navController)
        }
        composable("member_detail") {
            MemberDetails(groupViewModel = groupViewModel, authViewModel = authViewModel, activityViewModel = activityViewModel, navController = navController)
        }
        composable("add_member") {
            AddRemoveMember(authViewModel = authViewModel, groupViewModel = groupViewModel, navController = navController)
        }
        composable("edit_profile") {
            EditProfile(authViewModel, navController)
        }
        composable("modify_admin") {
            ModifyAdmin(authViewModel = authViewModel, groupViewModel = groupViewModel, navController = navController)
        }
        composable("suggested_groups") {
            SuggestedGroups(groupViewModel = groupViewModel, navController = navController)
        }
        composable("membership_request") {
            MembershipRequests(
                navController = navController,
                groupViewModel = groupViewModel,
                authViewModel = authViewModel
            )
        }
    }
}

