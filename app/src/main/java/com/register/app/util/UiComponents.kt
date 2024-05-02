package com.register.app.util

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
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