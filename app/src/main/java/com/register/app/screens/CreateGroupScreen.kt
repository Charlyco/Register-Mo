package com.register.app.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.util.CircularIndicator
import com.register.app.util.ImageLoader
import com.register.app.util.Utils
import com.register.app.viewmodel.GroupViewModel
import kotlinx.coroutines.launch
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(groupViewModel: GroupViewModel, navController: NavController, onDismiss: (showState: Boolean) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val loadingState = groupViewModel.loadingState.observeAsState().value
    val coroutineScope = rememberCoroutineScope()
    val showBottomSheet = groupViewModel.showCreateGroupSheet.observeAsState().value
    val screenWidth = LocalConfiguration.current.screenWidthDp - 16
    val screenHeight = LocalConfiguration.current.screenHeightDp - 16
    var groupName by rememberSaveable { mutableStateOf("") }
    var groupDescription by rememberSaveable { mutableStateOf("") }
    var groupType by rememberSaveable { mutableStateOf("CLOSED") }
    var isClosedChecked by rememberSaveable { mutableStateOf(true) }
    var isOpenChecked by rememberSaveable { mutableStateOf(false) }
    val officeList = listOf("Select Office", "PRESIDENT", "SECRETARY", "TREASURER", "FINANCIAL_SECRETARY")
    var memberOffice by rememberSaveable { mutableStateOf("") }
    val scrollState = rememberScrollState(initial = 0)
    val logoUrl = groupViewModel.groupLogoLivedata.observeAsState().value
    val context = LocalContext.current
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                coroutineScope.launch {
                    try {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        if (inputStream != null) {
                            val mimeType = context.contentResolver.getType(uri)
                            groupViewModel.uploadGroupLogo(inputStream, mimeType, Utils.getFileNameFromUri(context.contentResolver, uri))
                        } else {
                            // Handle error
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        // Handle error
                    }
                }
            }
        }
    )

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
               Modifier
                   .fillMaxWidth()
                   .verticalScroll(scrollState)
           ) {
               val (closeBtn, title, divider, name, nameBox, description, descriptionBox,
                   office, officeSelector, logo, uploadBtn, type, createBtn, indicator) = createRefs()

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
                       top.linkTo(divider.bottom, margin = 8.dp)
                       start.linkTo(parent.start, margin = 8.dp)
                   },
                   fontSize = TextUnit(14.0f, TextUnitType.Sp),
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
                       top.linkTo(nameBox.bottom, margin = 8.dp)
                       start.linkTo(parent.start, margin = 8.dp)
                   },
                   fontSize = TextUnit(14.0f, TextUnitType.Sp),
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

               Text(
                   text = stringResource(id = R.string.creator_office),
                   Modifier.constrainAs(office) {
                       top.linkTo(descriptionBox.bottom, margin = 8.dp)
                       start.linkTo(parent.start, margin = 8.dp)
                   },
                   fontSize = TextUnit(14.0f, TextUnitType.Sp),
                   color = MaterialTheme.colorScheme.onBackground
               )
               Surface(
                   modifier = Modifier
                       .width(screenWidth.dp)
                       .height(50.dp)
                       .padding(end = 8.dp)
                       .constrainAs(officeSelector) {
                           top.linkTo(office.bottom, margin = 8.dp)
                           start.linkTo(parent.start, margin = 8.dp)
                       },
                   color = MaterialTheme.colorScheme.background,
                   shape = MaterialTheme.shapes.large,
                   border = BorderStroke(1.dp, Color.Gray)
               ) {
                   SelectOffice(officeList) {
                       memberOffice = it
                   }
               }

               Column(
                   Modifier
                       .fillMaxWidth()
                       .padding(horizontal = 16.dp)
                       .constrainAs(type) {
                           top.linkTo(officeSelector.bottom, margin = 8.dp)
                       },
                   horizontalAlignment = Alignment.Start
               ) {
                   Text(
                       text = stringResource(id = R.string.group_type),
                       Modifier.padding(start = 8.dp),
                       fontSize = TextUnit(14.0f, TextUnitType.Sp),
                       color = MaterialTheme.colorScheme.onBackground
                   )

                   Row(
                       Modifier.fillMaxWidth(),
                       verticalAlignment = Alignment.CenterVertically,
                   ) {
                       Text(
                           text = stringResource(id = R.string.closed),
                           fontSize = TextUnit(14.0f, TextUnitType.Sp),
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
                       imageUrl = logoUrl?: "",
                       context = context,
                       height = 120,
                       width = 120,
                       placeHolder = R.drawable.placeholder
                   )
               }

               Button(
                   onClick = {
                       filePicker.launch("image/*")
                   },
                   Modifier
                       .fillMaxWidth()
                       .padding(horizontal = 64.dp)
                       .height(50.dp)
                       .constrainAs(uploadBtn) {
                           top.linkTo(logo.bottom, margin = 8.dp)
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
                       coroutineScope.launch {
                           val response = groupViewModel.createNewGroup(groupName, groupDescription, memberOffice, groupType)
                           if (response.groupName == groupName) {
                               Toast.makeText(context, "Group Created Successfully", Toast.LENGTH_SHORT).show()
                               groupViewModel.showCreateGroupSheet.postValue(false)
                               groupViewModel.isUserAdmin() //grants the user admin rights
                               navController.navigate("group_detail")
                               groupViewModel.setSelectedGroupDetail(response)
                           }
                       }
                   },
                   Modifier
                       .fillMaxWidth()
                       .padding(start = 32.dp, end = 32.dp)
                       .height(50.dp)
                       .constrainAs(createBtn) {
                           top.linkTo(uploadBtn.bottom, margin = 16.dp)
                           bottom.linkTo(parent.bottom, margin = 48.dp)
                           centerHorizontallyTo(parent)
                       }
               ) {
                   Text(text = stringResource(id = R.string.create_group_btn))
               }

               if (loadingState == true) {
                   Surface(
                       Modifier.constrainAs(indicator) {
                           centerHorizontallyTo(parent)
                           centerVerticallyTo(parent)
                       },
                       color = Color.Transparent
                   ){
                       CircularIndicator()
                   }
               }
           }
        }
    }
}

@Composable
fun SelectOffice(officeList: List<String>, content: (office: String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by rememberSaveable { mutableStateOf(officeList[0]) }
    val screenWidth = LocalConfiguration.current.screenWidthDp

    Box(
        modifier = Modifier
            .width((screenWidth - 32).dp)
            .height(55.dp),
        contentAlignment = Alignment.Center
    ) {
        ConstraintLayout(modifier = Modifier
            .clickable { expanded = !expanded }
            .fillMaxSize()
            .padding(8.dp)
        ) {
            val (text, icon, menu) = createRefs()

            Text(
                text = selectedOptionText,
                fontSize = TextUnit(14.0f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(text) {
                        start.linkTo(parent.start, margin = 4.dp)
                        centerVerticallyTo(parent)
                    }
            )

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "",
                modifier = Modifier.constrainAs(icon) {
                    end.linkTo(parent.end, margin = 2.dp)
                    centerVerticallyTo(parent)
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .constrainAs(menu) { end.linkTo(icon.start) }
                    .width((screenWidth - 40).dp)
            ) {
                officeList.forEach {
                    DropdownMenuItem(
                        text = { Text(text = it) },
                        onClick = {
                            expanded = false
                            selectedOptionText = it
                            content(it)
                        }
                    )
                }
            }
        }
    }
}
