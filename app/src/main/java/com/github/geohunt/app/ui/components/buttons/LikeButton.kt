package com.github.geohunt.app.ui.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import com.github.geohunt.app.ui.theme.geoHuntRed

/**
 * Creates a fire icon button indicating a like, which is updated according to the current liking
 * state.
 *
 * @param isLiked Whether the button should indicate a "liked" state.
 * @param onLikeChanged Callback used whenever the user clicks the icon.
 */
@Composable
fun LikeButton(
    isLiked: Boolean,
    onLikeChanged: (Boolean) -> Unit
) {
    IconToggleButton(
        checked = isLiked,
        colors = IconButtonDefaults.iconToggleButtonColors(checkedContentColor = geoHuntRed),
        onCheckedChange = onLikeChanged
    ) {
        if (isLiked) {
            Icon(
                Icons.Filled.LocalFireDepartment,
                contentDescription = "Like this challenge"
            )
        } else {
            Icon(
                Icons.Outlined.LocalFireDepartment,
                contentDescription = "Unlike this challenge"
            )
        }
    }
}