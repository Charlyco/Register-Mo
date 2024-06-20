package com.register.app.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.util.GenericTopBar
import com.register.app.viewmodel.AuthViewModel
import com.register.app.viewmodel.GroupViewModel

@Composable
fun AddMember(authViewModel: AuthViewModel, groupViewModel: GroupViewModel, navController: NavController) {
    Scaffold(
        topBar = { GenericTopBar(
            title = stringResource(id = R.string.add_member),
            navController = navController,
            navRoute = "group_detail")}
    ) {
        AddMemberUi(Modifier.padding(it), authViewModel, groupViewModel, navController)
    }
}

@Composable
fun AddMemberUi(
    modifier: Modifier,
    authViewModel: AuthViewModel,
    groupViewModel: GroupViewModel,
    navController: NavController
) {

}
