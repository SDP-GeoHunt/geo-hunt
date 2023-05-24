package com.github.geohunt.app.ui.components.activehunts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.ui.theme.geoHuntRed

/**
 * The screen to show when there is nothing to display
 * Contains a main Text and a button
 * @param text The text that will be displayed in the screen
 * @param buttonText The text that will be displayed in the button
 * @param emptyScreenCallback The callback assigned to the button (will typically redirect the user)
 */
@Composable
fun EmptyScreen(text: String, buttonText: String, emptyScreenCallback: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = text,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(10.dp))
        Button(
            onClick = emptyScreenCallback,
            colors = ButtonDefaults.buttonColors(containerColor = geoHuntRed)
        ) {
            Text(
                text = buttonText,
                textAlign = TextAlign.Center
            )
        }
    }

}