package com.github.geohunt.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun LabelledCheckBox(
        checked: Boolean,
        onCheckedChange: ((Boolean) -> Unit),
        label: String,
        modifier: Modifier = Modifier
) {
    Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable(
                            indication = rememberRipple(color = MaterialTheme.colors.primary),
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { onCheckedChange(!checked) }
                    )
                    .requiredHeight(ButtonDefaults.MinHeight)
                    .padding(4.dp)
    ) {
        Checkbox(
                checked = checked,
                onCheckedChange = null
        )

        Spacer(Modifier.size(6.dp))

        Text(
                text = label
        )
    }
}

@Composable
fun LabelledSwitch(
        checked: Boolean,
        onCheckedChange: ((Boolean) -> Unit),
        label: String,
        modifier: Modifier = Modifier
) {
    Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable(
                            indication = rememberRipple(color = MaterialTheme.colors.primary),
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { onCheckedChange(!checked) }
                    )
                    .requiredHeight(ButtonDefaults.MinHeight)
                    .padding(4.dp)
    ) {
        Switch(
                checked = checked,
                onCheckedChange = { onCheckedChange(!checked) }
        )

        Spacer(Modifier.size(6.dp))

        Text(
                text = label
        )
    }
}
