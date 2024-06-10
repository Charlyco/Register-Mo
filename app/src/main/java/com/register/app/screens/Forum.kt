package com.register.app.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.register.app.util.BottomNavBar
import com.register.app.util.GenericTopBar
import com.register.app.viewmodel.ForumViewModel
import com.register.app.viewmodel.GroupViewModel

@Composable
fun Forum(forumViewModel: ForumViewModel, groupViewModel: GroupViewModel, navController: NavController){
    Scaffold(
        topBar = { GenericTopBar(title = "", navController = navController, navRoute = "home") },
        bottomBar = { BottomNavBar(navController = navController) }
    ) {
        ForumScreen(Modifier.padding(it), forumViewModel, groupViewModel)
    }
}

@Composable
fun ForumScreen(
    modifier: Modifier,
    forumViewModel: ForumViewModel,
    groupViewModel: GroupViewModel) {

}
