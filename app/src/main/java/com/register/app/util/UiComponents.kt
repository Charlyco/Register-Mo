package com.register.app.util

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.register.app.R

@Composable
fun CircularIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.size(64.dp),
        color = MaterialTheme.colorScheme.primary,
        strokeCap = StrokeCap.Butt,
        strokeWidth = dimensionResource(id = R.dimen.progress_indicator_stroke),
        trackColor = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun BottomNavBar(navController: NavController) {
    val backStackEntry = navController.currentBackStackEntry
    val bottomBarItems = listOf(
        BottomBarItem("home", R.drawable.home),
        BottomBarItem("chats", R.drawable.messages),
        BottomBarItem("groups", R.drawable.notepad),
        BottomBarItem("profile", R.drawable.user)
    )
BottomAppBar(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        topEnd = 24.dp,
                        topStart = 24.dp
                    )
                ),
            containerColor = MaterialTheme.colorScheme.primary,
            tonalElevation = 4.dp,
            windowInsets = BottomAppBarDefaults.windowInsets
        ) {
            bottomBarItems.forEach { item ->
                val selected = item.route == backStackEntry?.destination?.route

                NavigationBarItem(
                    selected = selected,
                    onClick = { navController.navigate(item.route){
                        launchSingleTop = true
                        popUpTo("home") }
                    },
                    modifier = Modifier.size(32.dp),
                    //label = { Text(item.label, color = MaterialTheme.colorScheme.primary) },
                    icon = { Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = "") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        indicatorColor = Color.Transparent
                    )
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericTopBar(title: String, navController: NavController, navRoute: String) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        TopAppBar(
            title = { Text(text = title) },
            modifier = Modifier.fillMaxWidth(),
            navigationIcon = { Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onBackground
            )},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent),
            actions = {
                IconButton(
                    onClick = { isExpanded = !isExpanded }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = stringResource(id = R.string.menu))
                }
                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false },
                    modifier = Modifier.width(160.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.settings)) },
                        onClick = {
                            isExpanded = false
                        },
                        colors = MenuDefaults.itemColors(

                        ),
                        leadingIcon = { Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(id = R.string.settings)
                        ) }
                    )

                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.privacy)) },
                        onClick = {
                            isExpanded = false
                        },
                        leadingIcon = { Icon(
                            imageVector = Icons.Default.PrivacyTip,
                            contentDescription = stringResource(id = R.string.privacy)
                        ) }
                    )

                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.about)) },
                        onClick = {
                            isExpanded = false
                        },
                        leadingIcon = { Icon(
                            imageVector = Icons.Default.Details,
                            contentDescription = stringResource(id = R.string.about)
                        ) }
                    )

                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.sign_out)) },
                        onClick = {
                            isExpanded = false
                        },
                        leadingIcon = { Icon(
                            imageVector = Icons.Default.ArrowOutward,
                            contentDescription = stringResource(id = R.string.sign_out)
                        ) }
                    )
                }
            }
        )
    }
}

@Composable
fun ImageLoader(imageUrl: String, context: Context, height: Int, width: Int) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(context).data(data = imageUrl).apply(block = fun ImageRequest.Builder.() {
            transformations(CircleCropTransformation())
            placeholder(R.drawable.sample)
            error(R.drawable.sample)
        }).build()
    )
    Image(
        painter = painter,
        contentDescription = "",
        modifier = Modifier
            .width(width.dp)
            .height(height.dp),
        alignment = Alignment.Center,
        contentScale = ContentScale.FillBounds
    )
}