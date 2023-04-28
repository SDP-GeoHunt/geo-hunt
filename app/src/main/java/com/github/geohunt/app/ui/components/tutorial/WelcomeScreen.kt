package com.github.geohunt.app.ui.components.tutorial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The welcome displayed when the user opens the application
 * for the first time
 *
 * @param shouldShowTutorial The state of whether the tutorial should be shown
 */
@Composable
fun WelcomeScreen(shouldShowTutorial: MutableState<Boolean>){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // The welcome text of the application
        Text(
            text = "Welcome to GeoHunt!",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                .testTag("WelcomeText"),
            fontSize = 90.sp,
            fontStyle = FontStyle.Italic,
            fontFamily = FontFamily.Cursive,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            letterSpacing = 6.15.sp,
            lineHeight = 70.sp,
            softWrap = true,
            color = Color.Red,
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Description of the application
        Text(
            text = "The fun, interactive, engaging and challenging treasure hunt game!",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(start = 25.dp, end = 25.dp)
                .fillMaxWidth()
                .testTag("WelcomeDescription"),
            fontSize = 30.sp,
            overflow = TextOverflow.Ellipsis,
            letterSpacing = 5.sp,
            softWrap = true,

            )

        Spacer(modifier = Modifier.height(55.dp))

        // Button to start the tutorial
        Button(
            onClick = { shouldShowTutorial.value = true },
            contentPadding = PaddingValues(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent
            ),
            shape = RoundedCornerShape(50.dp)

        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(colors = listOf(Color(0xFF774387), Color(0xFFDC2431))),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 25.dp, vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "GET STARTED",
                    fontSize = 40.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}