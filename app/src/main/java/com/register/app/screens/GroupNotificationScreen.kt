package com.register.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.dto.GroupNotification
import com.register.app.enums.NotificationType
import com.register.app.util.CircularIndicator
import com.register.app.util.GenericTopBar
import com.register.app.viewmodel.GroupViewModel

@Composable
fun GroupNotificationScreen(groupViewModel: GroupViewModel, navController: NavController) {
    val isLoading = groupViewModel.loadingState.observeAsState().value
    Scaffold(
        topBar = { GenericTopBar(
            title = stringResource(id = R.string.group_notification),
            navController = navController
        )}
    ) {
        GroupNotificationList(Modifier.padding(it), groupViewModel)
        if (isLoading == true) {
            CircularIndicator()
        }
    }
}

@Composable
fun GroupNotificationList(
    modifier: Modifier,
    groupViewModel: GroupViewModel
) {
    val groupNotifications = groupViewModel.groupNotificationList.observeAsState().value
    if (!groupNotifications.isNullOrEmpty()) {
        LazyColumn(modifier = modifier) {
            items(groupNotifications) {notificationItem ->
                GroupNotificationItem(notificationItem)
            }
        }
    }
}

@Composable
fun GroupNotificationItem(
    notificationItem: GroupNotification

) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .height(72.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                Modifier.size(48.dp),
                color = when (notificationItem.type) {
                    NotificationType.ADMIN.name -> Color(color = context.getColor(R.color.light_ox))
                    NotificationType.EVENT.name -> Color(color = context.getColor(R.color.purple_500))
                    NotificationType.ELECTION.name -> Color(color = context.getColor(R.color.app_orange))
                    else -> Color(color = context.getColor(R.color.light_lemon))
                },
                shape = MaterialTheme.shapes.small
            ) {
                when (notificationItem.type) {
                    NotificationType.ADMIN.name -> {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_notification_bell),
                            contentDescription = "",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    NotificationType.ELECTION.name -> {
                        Icon(
                            painter = painterResource(id = R.drawable.reshot_icon_notification),
                            contentDescription = "",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    NotificationType.EVENT.name -> {
                        Icon(
                            painter = painterResource(id = R.drawable.events_icoc),
                            contentDescription = "",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    NotificationType.GENERAL.name -> {
                        Icon(
                            painter = painterResource(id = R.drawable.reshot_icon_notification),
                            contentDescription = "",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            Text(
                text = notificationItem.content?: "",
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                modifier = Modifier.padding(start = 8.dp)
            )

        }
    }
}
