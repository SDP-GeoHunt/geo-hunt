package com.github.geohunt.app.ui.components.activehunts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.ui.theme.Lobster

@Composable
fun ActiveHunts(challenges: List<LazyRef<Challenge>>) {
    Column(modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)){
        TitleText()

        Spacer(modifier = Modifier.size(10.dp))

        ActiveHuntsList(challenges = challenges)
    }
}

@Preview
@Composable
fun Test() {
    ActiveHunts(challenges = listOf())
}

@Composable
fun TitleText() {
    Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 40.sp, fontFamily = Lobster)) {

                    withStyle(style = SpanStyle(color = Color.Black)) {
                        append("Active")
                    }

                    append(" ")

                    withStyle(style = SpanStyle(color = Color.Red)) {
                        append("hunts")
                    }
                }
            }
    )
}

@Composable
fun ActiveHuntsList(challenges: List<LazyRef<Challenge>>) {
    //wrapper Box
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        if(challenges.isEmpty()) {
            EmptyChallengesScreen()
        }
        else {
            LazyRow {
                items(challenges) { challenge ->
                    ChallengePreview(challenge = challenge)
                }
            }
        }

    }
}

@Composable
fun EmptyChallengesScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "No challenges yet...\n" +
                "Go pick some challenges to begin your hunt!")
        Spacer(modifier = Modifier.size(10.dp))
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Search nearby challenges")
        }
    }

}