package com.github.geohunt.app.ui.components.tutorial

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.R

/**
 * The welcome displayed when the user opens the application
 * for the first time
 *
 * @param showTutorial The callback when the button "Get started" is clicked
 */
@Composable
fun WelcomeScreen(showTutorial: () -> Unit){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        // The welcome text of the application
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = stringResource(id = R.string.app_name),
            modifier = Modifier
                .fillMaxWidth().fillMaxHeight(.3f)
                .padding(32.dp)
        )


        // Description of the application
        Text(
            text = stringResource(id = R.string.welcome_screen_detail),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp)
                .testTag("Welcome Description"),
            softWrap = true,
            style = MaterialTheme.typography.body1,
            fontSize = 18.sp
        )

        Button(
            onClick = { showTutorial() },
            modifier = Modifier.padding(bottom = 35.dp)
        ) {
            Text(
                text = stringResource(id = R.string.tutorial_get_started),
                fontWeight = FontWeight.Bold
            )
        }
    }
}