package com.github.geohunt.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp


/**
 * Composable function that renders an icon and a label next to each other.
 * @param text The label to display next to the icon.
 * @param painter The [Painter] object that represents the icon to display.
 * @param contentDescription The content description to use for the icon for accessibility purposes.
 * @param fontSize The size of the font used to display the label. Defaults to [TextUnit.Unspecified].
 * @param fontStyle The [FontStyle] to apply to the label. Defaults to null.
 * @param fontColor The color to use for the label's text. Defaults to [Color.Unspecified].
 * @param iconSize The size of the icon to display. Defaults to 25dp.
 * @param tint The tint color to apply to the icon. Defaults to [Color.Unspecified].
 * @param modifier Additional [Modifier] objects to apply to the composable.
 */
@Composable
fun LabelledIcon(
    text: String,
    painter: Painter,
    contentDescription: String,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontColor: Color = Color.Unspecified,
    iconSize: Dp = 25.dp,
    tint : Color = Color.Unspecified,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = text,
            color = fontColor,
            fontStyle = fontStyle,
            fontSize = fontSize,
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        Spacer(modifier = Modifier.width(2.dp))

        Icon(
            painter,
            tint = tint.takeOrElse { MaterialTheme.colors.primary },
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize)
        )
    }
}
