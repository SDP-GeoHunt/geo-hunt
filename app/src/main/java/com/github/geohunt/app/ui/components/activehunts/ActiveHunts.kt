package com.github.geohunt.app.ui.components.activehunts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.github.geohunt.app.model.database.firebase.FirebaseUserRef
import com.github.geohunt.app.ui.FetchComponent
import com.github.geohunt.app.ui.theme.Lobster
import com.github.geohunt.app.ui.theme.geoHuntRed
import com.github.geohunt.app.utility.findActivity

@Composable
fun ActiveHunts(id: String) {
    ActiveHunts(user = FirebaseUserRef(id, FirebaseDatabase(LocalContext.current.findActivity())))
}

@Composable
fun ActiveHunts(user: LazyRef<User>) {
    Box(modifier = Modifier.fillMaxSize()) {
        FetchComponent(lazyRef = { user }, modifier = Modifier.align(Alignment.Center)) {resolvedUser ->
            ActiveHunts(challenges = resolvedUser.challenges)
        }
    }
}
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

@Composable
fun TitleText() {
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

@Composable
fun ActiveHuntsList(challenges: List<LazyRef<Challenge>>) {
    //wrapper Box
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if(challenges.isEmpty()) {
            EmptyChallengesScreen()
        }
        else {
            LazyRow(modifier = Modifier.testTag("challenge_row"),
                    contentPadding = PaddingValues(30.dp, 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(challenges) { challenge ->
                    Box(modifier = Modifier.size(300.dp, 600.dp)) {
                        ChallengePreview(challenge = challenge)
                    }
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
        Button(onClick = { /*TODO*/ }, colors = ButtonDefaults.buttonColors(backgroundColor = geoHuntRed)) {
            Text(text = "Search nearby challenges")
        }
    }

}