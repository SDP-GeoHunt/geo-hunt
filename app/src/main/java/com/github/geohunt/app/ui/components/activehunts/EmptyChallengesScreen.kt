package com.github.geohunt.app.ui.components.activehunts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.ui.theme.geoHuntRed

/**
 * The screen to show when they are no challenges to display
 * Simply displays a text saying no challenges have been selected yet
 * and a button redirecting to the screen to select new challenges
 */
@Composable
fun EmptyChallengesScreen(emptyScreenCallback: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "No challenges yet...\n" +
                    "Go pick some challenges to begin your hunt!"
        )
        Spacer(modifier = Modifier.size(10.dp))
        Button(
            onClick = emptyScreenCallback,
            colors = ButtonDefaults.buttonColors(backgroundColor = geoHuntRed)
        ) {
            Text(text = "Search nearby challenges")
        }
    }

}