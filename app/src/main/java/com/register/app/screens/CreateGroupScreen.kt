package com.register.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.register.app.R
import com.register.app.util.DataStoreManager
import com.register.app.util.ImageLoader
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(groupViewModel: GroupViewModel, onDismiss: (showState: Boolean) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val showBottomSheet = groupViewModel.showCreateGroupSheet.observeAsState().value
    val screenWidth = LocalConfiguration.current.screenWidthDp - 16
    val screenHeight = LocalConfiguration.current.screenHeightDp - 72
    var groupName by rememberSaveable { mutableStateOf("") }
    var groupDescription by rememberSaveable { mutableStateOf("") }
    var groupType by rememberSaveable { mutableStateOf("CLOSED") }
    var isClosedChecked by rememberSaveable { mutableStateOf(true) }
    var isOpenChecked by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    if (showBottomSheet == true) {
        ModalBottomSheet(
            onDismissRequest = { onDismiss(false) },
            sheetState = sheetState,
            sheetMaxWidth = screenWidth.dp,
            containerColor = MaterialTheme.colorScheme.background,
            tonalElevation = dimensionResource(id = R.dimen.low_elevation),
            modifier = Modifier.height(screenHeight.dp)
            ) {
           ConstraintLayout(
               Modifier.fillMaxWidth()
           ) {
               val (closeBtn, title, divider, name, nameBox, description, descriptionBox, logo, uploadBtn, type, createBtn) = createRefs()

               Surface(
                   Modifier
                       .clip(CircleShape)
                       .clickable {
                           coroutineScope
                               .launch { sheetState.hide() }
                               .invokeOnCompletion {
                                   if (!sheetState.isVisible) {
                                       onDismiss(false)
                                   }
                               }
                       }
                       .constrainAs(closeBtn) {
                           start.linkTo(parent.start, margin = 16.dp)
                           top.linkTo(parent.top, margin = 16.dp)
                       },
                   color = MaterialTheme.colorScheme.background,
                   shadowElevation = dimensionResource(id = R.dimen.low_elevation)
               ) {
                   Icon(
                       imageVector = Icons.Default.KeyboardArrowDown,
                       contentDescription = "close"
                   )
               }

               Text(
                   text = stringResource(id = R.string.create_group),
                   Modifier.constrainAs(title) {
                       top.linkTo(parent.top, margin = 12.dp)
                       centerHorizontallyTo(parent)
                   },
                   fontSize = TextUnit(20.0f, TextUnitType.Sp),
                   fontWeight = FontWeight.SemiBold,
                   color = MaterialTheme.colorScheme.onBackground
                   )

               HorizontalDivider(
                   Modifier.constrainAs(divider) {
                       top.linkTo(title.bottom, margin = 16.dp)
                       centerHorizontallyTo(parent)
                   }
               )

               Text(
                   text = stringResource(id = R.string.group_name),
                   Modifier.constrainAs(name) {
                       top.linkTo(divider.bottom, margin = 16.dp)
                       start.linkTo(parent.start, margin = 8.dp)
                   },
                   fontSize = TextUnit(16.0f, TextUnitType.Sp),
                   color = MaterialTheme.colorScheme.onBackground
               )

               Surface(
                   Modifier
                       .fillMaxWidth()
                       .padding(horizontal = 8.dp)
                       .height(50.dp)
                       .constrainAs(nameBox) {
                           top.linkTo(name.bottom, margin = 4.dp)
                       },
                   shape = MaterialTheme.shapes.small,
                   color = MaterialTheme.colorScheme.background,
                   border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
               ) {
                   TextField(
                       value = groupName,
                       onValueChange = { groupName = it },
                       Modifier.fillMaxSize(),
                       colors = TextFieldDefaults.colors(
                           focusedContainerColor = MaterialTheme.colorScheme.background,
                           unfocusedContainerColor = MaterialTheme.colorScheme.background,
                           unfocusedIndicatorColor = Color.Transparent,
                           focusedIndicatorColor = MaterialTheme.colorScheme.primary
                       )
                   )
               }

               Text(
                   text = stringResource(id = R.string.group_description),
                   Modifier.constrainAs(description) {
                       top.linkTo(nameBox.bottom, margin = 16.dp)
                       start.linkTo(parent.start, margin = 8.dp)
                   },
                   fontSize = TextUnit(16.0f, TextUnitType.Sp),
                   color = MaterialTheme.colorScheme.onBackground
               )
               Surface(
                   Modifier
                       .fillMaxWidth()
                       .padding(horizontal = 8.dp)
                       .height(50.dp)
                       .constrainAs(descriptionBox) {
                           top.linkTo(description.bottom, margin = 4.dp)
                       },
                   shape = MaterialTheme.shapes.small,
                   color = MaterialTheme.colorScheme.background,
                   border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
               ) {
                   TextField(
                       value = groupDescription,
                       onValueChange = { groupDescription = it },
                       Modifier.fillMaxSize(),
                       colors = TextFieldDefaults.colors(
                           focusedContainerColor = MaterialTheme.colorScheme.background,
                           unfocusedContainerColor = MaterialTheme.colorScheme.background,
                           unfocusedIndicatorColor = Color.Transparent,
                           focusedIndicatorColor = MaterialTheme.colorScheme.primary
                       )
                   )
               }

               Column(
                   Modifier
                       .fillMaxWidth()
                       .padding(horizontal = 16.dp)
                       .constrainAs(type) {
                           top.linkTo(descriptionBox.bottom, margin = 16.dp)
                       },
                   horizontalAlignment = Alignment.Start
               ) {
                   Text(
                       text = stringResource(id = R.string.group_type),
                       Modifier.padding(start = 8.dp),
                       fontSize = TextUnit(16.0f, TextUnitType.Sp),
                       color = MaterialTheme.colorScheme.onBackground
                   )

                   Row(
                       Modifier.fillMaxWidth(),
                       verticalAlignment = Alignment.CenterVertically,
                   ) {
                       Text(
                           text = stringResource(id = R.string.closed),
                           fontSize = TextUnit(16.0f, TextUnitType.Sp),
                           color = MaterialTheme.colorScheme.onBackground
                       )

                       Checkbox(
                           checked = isClosedChecked,
                           onCheckedChange = {
                               isClosedChecked = !isClosedChecked
                               if (isClosedChecked) {
                                   isOpenChecked = false
                                   groupType = "CLOSED"
                               }
                           },
                           Modifier.padding(horizontal = 8.dp)
                       )

                       Text(
                           text = stringResource(id = R.string.open),
                           fontSize = TextUnit(16.0f, TextUnitType.Sp),
                           color = MaterialTheme.colorScheme.onBackground
                       )
                       Checkbox(
                           checked = isOpenChecked,
                           onCheckedChange = {
                               isOpenChecked = !isOpenChecked
                               if (isOpenChecked) {
                                   isClosedChecked = false
                                   groupType = "OPEN"
                               }
                           },
                           Modifier.padding(horizontal = 8.dp)
                       )
                   }
               }
               Surface(
                   Modifier
                       .size(120.dp)
                       .constrainAs(logo) {
                           centerHorizontallyTo(parent)
                           top.linkTo(type.bottom, margin = 16.dp)
                       },
                   shape = MaterialTheme.shapes.small,
                   color = MaterialTheme.colorScheme.background,
                   border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
                   ) {
                   ImageLoader(
                       imageUrl = "",
                       context = context,
                       height = 120,
                       width = 120,
                       placeHolder = R.drawable.app_icon
                   )
               }

               Button(
                   onClick = {
                             // pick image from local file and upload
                   },
                   Modifier
                       .fillMaxWidth()
                       .padding(horizontal = 64.dp)
                       .height(50.dp)
                       .constrainAs(uploadBtn) {
                           top.linkTo(logo.bottom, margin = 16.dp)
                           centerHorizontallyTo(parent)
                       },
                   colors = ButtonDefaults.buttonColors(
                       containerColor = MaterialTheme.colorScheme.secondary
                   )
                   ) {
                   Text(text = stringResource(id = R.string.upload))
               }

               Button(
                   onClick = {
                       // pick image from local file and upload
                   },
                   Modifier
                       .fillMaxWidth()
                       .padding(horizontal = 32.dp)
                       .height(50.dp)
                       .constrainAs(createBtn) {
                           top.linkTo(uploadBtn.bottom, margin = 24.dp)
                           centerHorizontallyTo(parent)
                       }
               ) {
                   Text(text = stringResource(id = R.string.create_group_btn))
               }
           }
        }
    }
}

@Preview
@Composable
fun PreviewSheet(){
    CreateGroupScreen(
        groupViewModel = GroupViewModel(DataStoreManager(LocalContext.current))) {
    }
}