package com.github.geohunt.app.ui.components.buttons

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.github.geohunt.app.R
import com.github.geohunt.app.ui.theme.geoHuntRed

/**
 * Creates the "Hunt" button.
 *
 * @param onClick Click handler.
 */
@Composable
fun HuntButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        colors = ButtonDefaults.buttonColors(
            containerColor = geoHuntRed
        )
    ) {
        Icon(
            painter = painterResource(id = R.drawable.target_arrow),
            contentDescription = "Hunt",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(stringResource(id = R.string.join_hunt))
    }
}