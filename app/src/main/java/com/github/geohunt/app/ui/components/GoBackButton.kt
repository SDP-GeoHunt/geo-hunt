package com.github.geohunt.app.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R

/**
 * Display a button for going back (should be displayed in the top left corner)
 *
 * @param fnGoBackCallback Callback called whenever the user press on the button
 */
@Composable
fun GoBackBtn(
    fnGoBackCallback: () -> Unit,
    tint: Color = Color.Unspecified
)
{
    val color =
        if (tint == Color.Unspecified) colorResource(id = R.color.md_theme_light_onBackground)
        else tint

    IconButton(
        modifier = Modifier
            .size(48.dp)
            .padding(10.dp)
            .testTag("btn-go-back"),
        onClick = fnGoBackCallback) {
        Icon(
            Icons.Rounded.ArrowBack,
            contentDescription = "Go back",
            tint = color
        )
    }
}
