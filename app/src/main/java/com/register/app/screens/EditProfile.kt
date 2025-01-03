package com.register.app.screens

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.register.app.R
import com.register.app.model.Member
import com.register.app.util.CircularIndicator
import com.register.app.util.GenericTopBar
import com.register.app.util.ImageLoader
import com.register.app.util.Utils
import com.register.app.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import java.io.IOException

@Composable
fun EditProfile(authViewModel: AuthViewModel, navController: NavController) {
    val user = authViewModel.userLideData.observeAsState().value
    Scaffold(
        topBar = { GenericTopBar(title = "Edit Profile", navController = navController)},
        containerColor = MaterialTheme.colorScheme.background
    ) {
        EditProfileUi(Modifier.padding(it), user, authViewModel, navController)
    }
}

@Composable
fun EditProfileUi(
    modifier: Modifier,
    user: Member?,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    //val context = LocalContext.current
    var fullName by rememberSaveable { mutableStateOf(user?.fullName) }
    var username by rememberSaveable { mutableStateOf(user?.userName) }
    var address by rememberSaveable { mutableStateOf(user?.address) }
    var phone by rememberSaveable { mutableStateOf(user?.phoneNumber) }
    var imageUrl by rememberSaveable { mutableStateOf(user?.imageUrl) }
    val isLoading by authViewModel.progressLiveData.observeAsState(false)
    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val context = LocalContext.current as Activity
    val imageCropLauncher =
        rememberLauncherForActivityResult(contract = CropImageContract()) { result ->
            if (result.isSuccessful) {
                result.uriContent?.let {
                    //getBitmap method is deprecated in Android SDK 29 or above so we need to do this check here
                    bitmap = if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images
                            .Media.getBitmap(context.contentResolver, it)
                    } else {
                        val source = ImageDecoder
                            .createSource(context.contentResolver, it)
                        ImageDecoder.decodeBitmap(source)
                    }

                    coroutineScope.launch {
                        try {
                            if (bitmap != null) {
                                val mimeType = "image/jpeg"
                                val response = authViewModel.uploadCroppedProfilePic(
                                    bitmap!!, mimeType, "profile_pic")
                                if (response.status) {
                                    imageUrl = response.data?.secureUrl
                                    user?.imageUrl = imageUrl
                                    val updateResponse = authViewModel.updateUserProfilePic(user)
                                    imageUrl = updateResponse.data?.imageUrl
                                } else {
                                    // Handle error
                                }
                            } else {
                                // Handle error
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            // Handle error
                        }
                    }
                }

            } else {
                //If something went wrong you can handle the error here
                println("ImageCropping error: ${result.error}")
            }
        }

    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .padding(top = 64.dp)
            .verticalScroll(rememberScrollState(initial = 0))
    ) {
        val (profilePic, details, updateBtn, progress, uploadBtn) = createRefs()

        Box(
            Modifier
                .size(160.dp)
                .clip(CircleShape)
                .constrainAs(profilePic) {
                    centerHorizontallyTo(parent)
                    top.linkTo(parent.top, margin = 16.dp)
                },
            contentAlignment = Alignment.BottomEnd
        ) {
            if (imageUrl == null) {
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }else {
                ImageLoader(
                    imageUrl = imageUrl?: "",
                    context = LocalContext.current,
                    height = 158,
                    width = 158,
                    placeHolder = R.drawable.placeholder)
                }

            Surface(
                modifier = Modifier
                    .padding(end = 16.dp, bottom = 8.dp)
                    .clickable {
                        //filePicker.launch("image/*")
                        val cropOptions = CropImageContractOptions(
                            null,
                            CropImageOptions(imageSourceIncludeCamera = false)
                        )
                        imageCropLauncher.launch(cropOptions)
                    }
                    .size(40.dp),
                color = MaterialTheme.colorScheme.tertiary,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "",
                    modifier = Modifier.padding(4.dp))
            }
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .constrainAs(details) {
                    top.linkTo(profilePic.bottom, margin = 16.dp)
                    centerHorizontallyTo(parent)
                }
        ) {
            Text(
                text = stringResource(id = R.string.full_name),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )
            Surface(
                Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                border = BorderStroke(1.dp, Color.Gray),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background
            ) {
                TextField(
                    value = fullName?: "",
                    onValueChange = { fullName = it },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Text(
                text = stringResource(id = R.string.username),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )
            Surface(
                Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                border = BorderStroke(1.dp, Color.Gray),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background
            ) {
                TextField(
                    value = username?: "",
                    onValueChange = { username = it },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Text(
                text = stringResource(id = R.string.address),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )
            Surface(
                Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                border = BorderStroke(1.dp, Color.Gray),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background
            ) {
                TextField(
                    value = address?: "",
                    onValueChange = { address = it },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Text(
                text = stringResource(id = R.string.phone),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = TextUnit(16.0f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )
            Surface(
                Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                border = BorderStroke(1.dp, Color.Gray),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background
            ) {
                TextField(
                    value = phone?: "",
                    onValueChange = { phone = it },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    user?.fullName = fullName!!
                    user?.userName = username
                    user?.address = address
                    user?.phoneNumber = phone!!
                    val response = authViewModel.updateUserData(user)
                    if (response.status) {
                        Toast.makeText(
                            context,
                            "User info updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .padding(horizontal = 16.dp)
                .constrainAs(updateBtn) {
                    top.linkTo(details.bottom, margin = 24.dp)
                    bottom.linkTo(parent.bottom, margin = 24.dp)
                    centerHorizontallyTo(parent)
                },
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color(context.getColor(R.color.background_color))
            )
            ) {
            Text(text = stringResource(id = R.string.submit))
        }

        if (isLoading) {
            Surface(
                Modifier.constrainAs(progress) {
                    centerHorizontallyTo(parent)
                    centerVerticallyTo(parent)
                },
                color = Color.Transparent
            ) {
                CircularIndicator()
            }
        }
    }
}
