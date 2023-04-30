package com.github.geohunt.app.ui.components.tutorial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        // The welcome text of the application
        Text(
            text = "GeoHunt",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 10.dp, end = 10.dp)
                .testTag("Welcome Label"),
            fontSize = 80.sp,
            fontStyle = FontStyle.Italic,
            fontFamily = FontFamily.Cursive,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            letterSpacing = 6.15.sp,
            lineHeight = 70.sp,
            softWrap = true,
            color = Color.Red,
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Description of the application
        Text(
            text = "The fun, interactive, engaging and challenging treasure hunt game!",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp)
                .fillMaxWidth()
                .testTag("Welcome Description"),
            fontSize = 25.sp,
            overflow = TextOverflow.Ellipsis,
            letterSpacing = 5.sp,
            softWrap = true,

            )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Button to start the tutorial
        Button(
            onClick = { shouldShowTutorial.value = true },
            contentPadding = PaddingValues(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent
            ),
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .padding(bottom = 35.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(colors = listOf(Color(0xFF774387), Color(0xFFDC2431))),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .align(Alignment.Bottom)
                    .padding(horizontal = 25.dp, vertical = 15.dp),

                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "GET STARTED",
                    fontSize = 36.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}