package com.register.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.util.DataStoreManager

@Composable
fun AuthScreen(navController: NavController, dataStoreManager: DataStoreManager) {
    val buttonWidth = (LocalConfiguration.current.screenWidthDp/2) - 32
    Surface(
        Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
       ConstraintLayout(
           Modifier.fillMaxSize()
       ) {
           val (bg, signIn, signUp, motto, bottomShade) = createRefs()
           Image(
               painter = painterResource(id = R.drawable.auth_bg),
               contentDescription = "",
               contentScale = ContentScale.FillBounds,
               modifier = Modifier
                   .fillMaxWidth()
                   .height(360.dp)
                   .constrainAs(bg) {
                       centerHorizontallyTo(parent)
                       top.linkTo(parent.top)
                   })
           Text(
               text = stringResource(id = R.string.splash_text),
               modifier = Modifier
                   .padding(end = 16.dp)
                   .constrainAs(motto) {
                       start.linkTo(parent.start, margin = 16.dp)
                       centerVerticallyTo(parent)
                       //bottom.linkTo(signUp.top, margin = 32.dp)
                   },
               fontSize = TextUnit(32.0f, TextUnitType.Sp),
               color = MaterialTheme.colorScheme.primary,
               textAlign = TextAlign.Start,
               fontWeight = FontWeight.SemiBold,
               lineHeight = TextUnit(36.0f, TextUnitType.Sp)

           )
           Button(
               onClick = {
                   navController.navigate("signin") {
                       popUpTo("splash") {
                           inclusive = true }
                   }
                         },
               Modifier
                   .height(55.dp)
                   .fillMaxWidth()
                   .padding(horizontal = 16.dp)
                   .constrainAs(signIn) {
                       bottom.linkTo(signUp.top, margin = 20.dp)
                   },
               colors = ButtonDefaults.buttonColors(
                   containerColor = MaterialTheme.colorScheme.onTertiary,
                   contentColor = MaterialTheme.colorScheme.onBackground),
               shape = MaterialTheme.shapes.medium,
               elevation = ButtonDefaults.buttonElevation(
                   defaultElevation = dimensionResource(id = R.dimen.default_elevation),
                   pressedElevation = dimensionResource(id = R.dimen.low_elevation)
               )
           ) {
               Text(text = stringResource(id = R.string.signin))
           }

           Button(
               onClick = {
                   navController.navigate("signup") {
                       popUpTo("splash") {
                           inclusive = true
                       }
                   }
               },
               Modifier
                   .height(55.dp)
                   .fillMaxWidth()
                   .padding(horizontal = 16.dp)
                   .constrainAs(signUp) {
                       bottom.linkTo(parent.bottom, margin = 24.dp)
                   },
               colors = ButtonDefaults.buttonColors(
                   containerColor = MaterialTheme.colorScheme.primary,
                   contentColor = MaterialTheme.colorScheme.background
               ),
               border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
               shape = MaterialTheme.shapes.medium,
               elevation = ButtonDefaults.buttonElevation(
                   defaultElevation = dimensionResource(id = R.dimen.default_elevation),
                   pressedElevation = dimensionResource(id = R.dimen.low_elevation)
               )
           ) {
               Text(text = stringResource(id = R.string.signup))
           }

       }
    }
}