package org.sergey.forecast.presentation.ui.component

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ReloadButton(
    modifier: Modifier = Modifier,
    text: String,
    colors: ButtonColors,
    onClick: () -> Unit,
) {

    Button(
        onClick = onClick,
        modifier = modifier,
        colors = colors
    ) {
        Text(text = text)
    }
}