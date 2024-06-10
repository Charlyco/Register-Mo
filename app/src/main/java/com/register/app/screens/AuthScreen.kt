package com.register.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.util.DataStoreManager

@Composable
fun AuthScreen(navController: NavController, dataStoreManager: DataStoreManager) {
    Surface(
        Modifier.fillMaxSize()
    ) {
       ConstraintLayout(
           Modifier.fillMaxSize()
       ) {
           val (bg, signIn, signUp) = createRefs()

           Image(
               painter = painterResource(id = R.drawable.urban),
               contentDescription = "",
               contentScale = ContentScale.FillBounds,
               modifier = Modifier
                   .fillMaxSize()
                   .constrainAs(bg) {
                       centerHorizontallyTo(parent)
                       centerVerticallyTo(parent)
                   })

           Button(
               onClick = {
                   navController.navigate("otp") {
                       popUpTo("splash") {
                           inclusive = true
                       }
                   }
               },
               Modifier
                   .height(50.dp)
                   .width(256.dp)
                   .constrainAs(signUp) {
                       centerHorizontallyTo(parent)
                       centerVerticallyTo(parent)
                   },
               colors = ButtonDefaults.buttonColors(
                   containerColor = MaterialTheme.colorScheme.background,
                   contentColor = MaterialTheme.colorScheme.onBackground
               ),
               border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
           ) {
               Text(text = stringResource(id = R.string.signup))
           }
           Button(
               onClick = {
                         navController.navigate("signin") {
                             popUpTo("splash") {
                                 inclusive = true
                             }
                         }
               },
               Modifier
                   .height(50.dp)
                   .width(256.dp)
                   .constrainAs(signIn) {
                       centerHorizontallyTo(parent)
                       top.linkTo(signUp.bottom, margin = 32.dp)
                   },
               colors = ButtonDefaults.buttonColors(
                   containerColor = MaterialTheme.colorScheme.tertiary,
                   contentColor = MaterialTheme.colorScheme.background
               )
           ) {
               Text(text = stringResource(id = R.string.signin))
           }
       }
    }
}