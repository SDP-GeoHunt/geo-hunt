package com.github.geohunt.app.ui.components.activehunts

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.ui.theme.Lobster
import com.github.geohunt.app.ui.theme.geoHuntRed

/**
 * The title of the [ActiveHuntsScreen].
 *
 * Displays "Active Hunts" using the correct colors and font.
 */
@Composable
fun ActiveHuntsTitle() {
    Text(
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 40.sp, fontFamily = Lobster)) {
                withStyle(style = SpanStyle(color = Color.Black)) {
                    append("Active")
                }

                append(" ")

                withStyle(style = SpanStyle(color = geoHuntRed)) {
                    append("hunts")
                }
            }
        }
    )
}