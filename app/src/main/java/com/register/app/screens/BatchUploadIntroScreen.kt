package com.register.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.register.app.R
import com.register.app.util.BATCH_UPLOAD

@Composable
fun BatchUploadIntroScreen(navController: NavController) {
    val backgroundBrush = Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.primary
        )
    )
    Surface(
        Modifier
            .fillMaxSize()
            .background(brush = backgroundBrush),
        color = Color.Transparent
    ) {
        ConstraintLayout(
            Modifier.fillMaxSize()
        ) {
            val (navIcon, description, icon, nextBtn) = createRefs()

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "",
                modifier = Modifier
                    .clickable {
                        navController.navigateUp()
                    }
                    .constrainAs(navIcon) {
                        top.linkTo(parent.top, margin = 20.dp)
                        start.linkTo(parent.start, margin = 20.dp)
                    })

            Text(
                text = stringResource(id = R.string.batch_upload_intro),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .constrainAs(description) {
                        top.linkTo(navIcon.bottom, margin = 64.dp)
                        centerHorizontallyTo(parent)
                    },
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
                )

            Image(
                painter = painterResource(id = R.drawable.events),
                contentDescription = "",
                modifier = Modifier
                    .size(100.dp)
                    .constrainAs(icon) {
                        centerVerticallyTo(parent)
                        centerHorizontallyTo(parent)
                    }
            )

            Button(
                onClick = {
                    navController.navigate(BATCH_UPLOAD)
                },
                modifier = Modifier
                    .height(dimensionResource(id = R.dimen.button_height))
                    .width(120.dp)
                    .constrainAs(nextBtn) {
                        bottom.linkTo(parent.bottom, margin = 32.dp)
                        centerHorizontallyTo(parent)
                    },
                shape = MaterialTheme.shapes.large,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = dimensionResource(id = R.dimen.default_elevation)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onBackground)
                ) {
                Text(
                    text = stringResource(id = R.string.next),
                    color = MaterialTheme.colorScheme.onBackground
                    )
            }
        }
    }
}