package com.register.app

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.register.app.screens.AddRemoveMember
import com.register.app.screens.AdminChat
import com.register.app.screens.EventDetails
import com.register.app.screens.Events
import com.register.app.screens.AllGroups
import com.register.app.screens.AllUserActivities
import com.register.app.screens.AssignSpecialLevi
import com.register.app.screens.AuthScreen
import com.register.app.screens.BatchUploadIntroScreen
import com.register.app.screens.BatchUploadScreen
import com.register.app.screens.BulkPayment
import com.register.app.screens.CreateElectionScreen
import com.register.app.screens.CreateEvent
import com.register.app.screens.CreateQuestionnaireScreen
import com.register.app.screens.DeleteGroup
import com.register.app.screens.SavedChatList
import com.register.app.screens.DiscoverScreen
import com.register.app.screens.EditProfile
import com.register.app.screens.ElectionDetail
import com.register.app.screens.ElectionResults
import com.register.app.screens.Elections
import com.register.app.screens.EvidenceOfPayment
import com.register.app.screens.Faq
import com.register.app.screens.Forum
import com.register.app.screens.GroupDetail
import com.register.app.screens.GroupNotificationScreen
import com.register.app.screens.GroupUpdateScreen
import com.register.app.screens.HomeScreen
import com.register.app.screens.LiveChatSupport
import com.register.app.screens.LoginScreen
import com.register.app.screens.MemberDetails
import com.register.app.screens.MembershipRequests
import com.register.app.screens.ModifyAdmin
import com.register.app.screens.NewPasswordEntry
import com.register.app.screens.NotificationScreen
import com.register.app.screens.PaySpecialLevy
import com.register.app.screens.ProfileScreen
import com.register.app.screens.QuestionnaireResponses
import com.register.app.screens.ResetPassword
import com.register.app.screens.SettingScreen
import com.register.app.screens.SignUpCont
import com.register.app.screens.Signup
import com.register.app.screens.SpecialLevyDetail
import com.register.app.screens.SplashScreen
import com.register.app.screens.SubmitQuestionnaireResponse
import com.register.app.screens.GroupSearch
import com.register.app.screens.PrivacyStatementScreen
import com.register.app.screens.SupportScreen
import com.register.app.screens.VerifyOtpScreen
import com.register.app.util.ADMIN_CHAT
import com.register.app.util.ALL_USER_ACTIVITIES
import com.register.app.util.ASSIGN_LEVY
import com.register.app.util.BATCH_UPLOAD
import com.register.app.util.BATCH_UPLOAD_INTRO
import com.register.app.util.DIRECT_CHAT
import com.register.app.util.DataStoreManager
import com.register.app.util.EVENT_DETAIL
import com.register.app.util.GROUP_NOTIFICATIONS
import com.register.app.util.HOME
import com.register.app.util.NOTIFICATIONS
import com.register.app.util.ONBOARDING
import com.register.app.util.PAY_SPECIAL_LEVY
import com.register.app.util.PRIVACY
import com.register.app.util.SETTINGS
import com.register.app.util.SPECIAL_LEVY_DETAIL
import com.register.app.viewmodel.ActivityViewModel
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.ForumViewModel
import com.register.app.viewmodel.GroupViewModel
import com.register.app.viewmodel.HomeViewModel
import com.register.app.viewmodel.QuestionnaireViewModel

@Composable
fun RegisterAppNavHost(
    mainActivity: MainActivity,
    dataStoreManager: DataStoreManager,
    takePicture: ActivityResultLauncher<Void?>
) {
    val authViewModel: AuthViewModel by mainActivity.viewModels()
    val homeViewModel: HomeViewModel by mainActivity.viewModels()
    val groupViewModel: GroupViewModel by mainActivity.viewModels()
    val forumViewModel: ForumViewModel by mainActivity.viewModels()
    val activityViewModel: ActivityViewModel by mainActivity.viewModels()
    val questionnaireViewModel: QuestionnaireViewModel by mainActivity.viewModels()

    val startDestination = homeViewModel.homeDestination.observeAsState().value

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination!!) {
        composable("splash") {
            SplashScreen(authViewModel, navController, dataStoreManager)
        }
        composable("signup") {
            Signup(authViewModel = authViewModel, navController = navController, dataStoreManager)
        }
        composable("signup_cont") {
            SignUpCont(authViewModel, navController)
        }
        composable("otp_verify/{email}") {
            VerifyOtpScreen(authViewModel = authViewModel, navController = navController, email = it.arguments?.getString("email"))
        }
        composable("signin") {
            LoginScreen(authViewModel = authViewModel, navController = navController, dataStoreManager)
        }
        composable(HOME) {
            HomeScreen(
                homeViewModel = homeViewModel,
                navController = navController,
                groupViewModel = groupViewModel,
                authViewModel = authViewModel,
                activityViewModel = activityViewModel,
                questionnaireViewModel = questionnaireViewModel,
                mainActivity = mainActivity)
        }
        composable("colleagues") {
            DiscoverScreen(groupViewModel = groupViewModel, homeViewModel = homeViewModel, navController = navController)
        }
        composable("events/{title}") { backStackEntry ->
            Events(navController = navController, activityViewModel = activityViewModel, groupViewModel = groupViewModel, authViewModel = authViewModel, title = backStackEntry.arguments?.getString("title"))
        }
        composable(EVENT_DETAIL) {
            EventDetails(dataStoreManager = dataStoreManager, navController = navController, activityViewModel = activityViewModel, groupViewModel = groupViewModel, authViewModel = authViewModel)
        }
        composable("groups") {
            AllGroups(
                navController = navController,
                dataStoreManager = dataStoreManager,
                groupViewModel = groupViewModel,
                questionnaireViewModel = questionnaireViewModel,
                activityViewModel = activityViewModel,
                authViewModel = authViewModel)
        }
        composable("payment") {
            EvidenceOfPayment(
                navController = navController,
                groupViewModel = groupViewModel,
                activityViewModel = activityViewModel,
                cameraActivityResult = takePicture
                )
        }
        composable("group_detail") {
            GroupDetail(navController = navController,
                groupViewModel = groupViewModel,
                authViewModel = authViewModel,
                forumViewModel = forumViewModel,
                homeViewModel = homeViewModel,
                activityViewModel = activityViewModel,
                questionnaireViewModel = questionnaireViewModel)
        }
        composable("profile") {
            ProfileScreen(authViewModel, groupViewModel, homeViewModel, navController)
        }
        composable("forum") {
            Forum(forumViewModel = forumViewModel, groupViewModel = groupViewModel, navController = navController)
        }
        composable(ONBOARDING) {
            AuthScreen(navController = navController, homeViewModel = homeViewModel)
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
            GroupSearch(
                groupViewModel = groupViewModel,
                navController = navController,
                homeViewModel = homeViewModel,
                activityViewModel = activityViewModel,
                authViewModel = authViewModel
                )
        }
        composable("membership_request") {
            MembershipRequests(
                navController = navController,
                groupViewModel = groupViewModel,
                authViewModel = authViewModel
            )
        }
        composable("bulk_payment/{totalAmount}") { backStackEntry ->
            BulkPayment(
                navController = navController,
                groupViewModel = groupViewModel,
                activityViewModel = activityViewModel,
                totalAmount = backStackEntry.arguments?.getString("totalAmount")?.toDouble() ?: 0.0,
                cameraActivityResult = takePicture
            )
        }
        composable("support") {
            SupportScreen(
                authViewModel = authViewModel,
                forumViewModel = forumViewModel,
                homeViewModel = homeViewModel,
                navController = navController)
        }
        composable("live_support") {
            LiveChatSupport(
                homeViewModel = homeViewModel,
                forumViewModel = forumViewModel,
                navController = navController,
               authViewModel = authViewModel
                )
        }
        composable("faq") {
            Faq(homeViewModel = homeViewModel, navController = navController)
        }
        composable("create_election") {
            CreateElectionScreen(
                groupViewModel = groupViewModel,
                authViewModel = authViewModel,
                navController = navController
            )
        }
        composable("elections") {
            Elections(groupViewModel = groupViewModel, navController = navController)
        }
        composable("election_detail") {
            ElectionDetail(groupViewModel = groupViewModel, navController = navController)
        }
        composable("election_result") {
            ElectionResults(groupViewModel = groupViewModel, navController = navController)
        }
        composable(NOTIFICATIONS) {
            NotificationScreen(
                authViewModel = authViewModel,
                homeViewModel = homeViewModel,
                groupViewModel = groupViewModel,
                activityViewModel = activityViewModel,
                navController = navController
            )
        }
        composable("reset_password") {
            ResetPassword(authViewModel = authViewModel, navController = navController)
        }
        composable("new_password") {
            NewPasswordEntry(authViewModel = authViewModel, navController = navController)
        }
        composable("questionnaire") {
            CreateQuestionnaireScreen(
                navController = navController,
                questionnaireViewModel = questionnaireViewModel,
                groupViewModel = groupViewModel
            )
        }
        composable("quest_response") {
            SubmitQuestionnaireResponse(
                questionnaireViewModel = questionnaireViewModel,
                groupViewModel = groupViewModel,
                navController = navController
            )
        }
        composable("user_responses/{form_title}") { backStackEntry ->
            QuestionnaireResponses(
                questionnaireViewModel = questionnaireViewModel,
                navController = navController,
                formTitle = backStackEntry.arguments?.getString("form_title")
            )
        }
        composable("delete_group") {
                DeleteGroup(
                    groupViewModel = groupViewModel,
                    navController = navController)
        }
        composable(ALL_USER_ACTIVITIES) {
            AllUserActivities(
                activityViewModel = activityViewModel,
                groupViewModel = groupViewModel,
                authViewModel = authViewModel,
                navController = navController
            )
        }
        composable(ADMIN_CHAT) { backStackEntry ->
            AdminChat(
                forumViewModel = forumViewModel,
                groupViewModel = groupViewModel,
                authViewModel = authViewModel,
                navController = navController,
                remoteUserEmail = backStackEntry.arguments?.getString("remoteUserEmail")
            )
        }
        composable(GROUP_NOTIFICATIONS) {
        GroupNotificationScreen(
            groupViewModel = groupViewModel,
            navController = navController)
        }
        composable(DIRECT_CHAT) {
            SavedChatList(
                forumViewModel = forumViewModel,
                navController = navController,
                groupViewModel = groupViewModel
            )
        }
        composable(ASSIGN_LEVY) {
            AssignSpecialLevi(
                activityViewModel = activityViewModel,
                groupViewModel = groupViewModel,
                navController = navController
            )
        }
        composable(PAY_SPECIAL_LEVY) {
            PaySpecialLevy(
                activityViewModel = activityViewModel,
                groupViewModel = groupViewModel,
                navController = navController,
                cameraActivityResult = takePicture
            )
        }
        composable(SPECIAL_LEVY_DETAIL) {
            SpecialLevyDetail(
                activityViewModel = activityViewModel,
                groupViewModel = groupViewModel,
                authViewModel = authViewModel,
                navController = navController
            )
        }
        composable(BATCH_UPLOAD_INTRO) {
            BatchUploadIntroScreen(navController = navController, activityViewModel)
        }
        composable(BATCH_UPLOAD) {
            BatchUploadScreen(
                activityViewModel = activityViewModel,
                groupViewModel = groupViewModel,
                navController = navController
            )
        }
        composable(SETTINGS) {
            SettingScreen(homeViewModel = homeViewModel, navController = navController)
        }
        composable(PRIVACY) {
            PrivacyStatementScreen(homeViewModel = homeViewModel, navController = navController)
        }
    }
}

