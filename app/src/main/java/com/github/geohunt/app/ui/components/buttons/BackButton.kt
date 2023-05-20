package com.github.geohunt.app.ui.components.buttons

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.github.geohunt.app.R

/**
 * Displays a button for going back.
 *
 * This button should only be displayed in the top left corner of an app bar, on secondary (temporary)
 * screens.
 *
 * @param onClick Callback used whenever the button is pressed.
 * @param tint The color of the button.
 */
@Composable
fun BackButton(
    onClick: () -> Unit,
    tint: Color = Color.Unspecified
) {
    IconButton(onClick = onClick) {
        Icon(
            Icons.Rounded.ArrowBack,
            contentDescription = stringResource(id = R.string.navigation_back),
            tint = tint,
            modifier = Modifier.testTag("backButton")
        )
    }
}
