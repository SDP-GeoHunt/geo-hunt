package com.github.geohunt.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.ui.rememberLazyRef
import com.github.geohunt.app.utility.DateFormatUtils

/**
 * Composable function that displays a challenge given as an argument
 * Following info is displayed :
 *      - The thumbnail of the challenge
 *      - The creator of the challenge
 *      - The date of the creation of the challenge
 *      - The time remaining to complete the challenge
 *      - The pictures submitted by users who already completed the challenge
 * It also adds a button that allows the user to add a submission to given challenge.
 * @param challenge The challenge to display
 */
@Composable
fun Challenge(challenge: Challenge) {
    val bitmap = rememberLazyRef {
        challenge.thumbnail
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column (horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()) {

            Spacer(modifier = Modifier.size(20.dp))

            if (bitmap.value == null) {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize(0.8F)
                )
            }
            else {
                val painter = remember {
                    BitmapPainter(bitmap.value!!.asImageBitmap())
                }
                Image(painter = painter,
                        contentDescription = "Thumbnail of the challenge",
                        modifier = Modifier
                                .clip(RoundedCornerShape(10.dp)))
            }

            ClaimButton()
        }

        ChallengeInformation(challenge)

        Spacer(modifier = Modifier.size(20.dp))

        ClaimPictures(challenge.claims)
    }
}

@Composable
fun ClaimButton() {
    Button(onClick = { submitPosition() }) {
        Row (verticalAlignment = Alignment.CenterVertically){
            Text(text = stringResource(id = R.string.challenge_claim))
            Icon(painter = painterResource(id = R.drawable.radar_icon),
                    contentDescription = "Radar icon of claim button")
        }
    }
}

@Composable
fun ChallengeInformation(challenge: Challenge) {
    val published = challenge.publishedDate
    val expirationDate = challenge.expirationDate

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp, 0.dp)) {

        Text(text = stringResource(id = R.string.challenge_created_by, challenge.author.id),
                color = Color.Gray)

        Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
            GrayText(stringResource(id = R.string.challenge_published, DateFormatUtils.formatDate(published)))
            GrayText(stringResource(id = R.string.challenge_time_remaining, DateFormatUtils.formatRemainingTime(expirationDate)))
        }
    }
}

@Composable
fun GrayText(text: String) {
    Text(text = text, color = Color.Gray)
}

@Composable
fun ClaimPictures(claims: List<LazyRef<Claim>>) {
    LazyVerticalGrid(columns = GridCells.Fixed(3)) {
        items(claims) {claim ->
            //placeholder, will be replaced by images of claims made to the challenge
            Box(modifier = Modifier
                .border(BorderStroke(1.dp, Color.Red))
                .aspectRatio(1f)) {
                Text(text = claim.id, color = Color.Red, textAlign = TextAlign.Center, modifier = Modifier.fillMaxSize())
            }
        }
    }
}
/**
 * Function called when the claim button is clicked
 */
fun submitPosition() {
    //Todo use utility function to get Localisation and create Claim
    return
}