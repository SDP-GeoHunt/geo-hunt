package com.github.geohunt.app

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.PictureImage
import com.github.geohunt.app.ui.theme.GeoHuntTheme
import com.github.geohunt.app.utility.DateFormatUtils
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@Composable
fun Challenge(challenge: Challenge) {
    val published = challenge.published
    val expirationDate = challenge.expirationDate

    Column(modifier = Modifier.fillMaxSize()) {
        Column (horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()) {

            Spacer(modifier = Modifier.size(20.dp))

            Image(painter = painterResource(id = R.drawable.eiffel),
                    contentDescription = "Thumbnail of the challenge",
                    modifier = Modifier
                            .clip(RoundedCornerShape(10.dp)))

            Button(onClick = { submitPosition() }) {
                Row (verticalAlignment = Alignment.CenterVertically){
                    Text(text = stringResource(id = R.string.challenge_claim))
                    Icon(painter = painterResource(id = R.drawable.radar_icon), contentDescription = "radar icon of claim button")
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {

            Text(text = stringResource(id = R.string.challenge_created_by, challenge.uid),
                    color = Color.Gray)

            Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = stringResource(id = R.string.challenge_published, DateFormatUtils.formatDate(published)),
                    color = Color.Gray)
                Text(text = stringResource(id = R.string.challenge_time_remaining, DateFormatUtils.formatRemainingTime(expirationDate)),
                    color = Color.Gray)
            }
        }
    }
}

fun submitPosition() {
    //Todo use utility fct to get Localisation and create Claim
    return
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    val dummyChallenge = object : Challenge {
        override val cid: String
            get() = "1234"
        override val uid: String
            get() = "5678"
        override val published: LocalDateTime
            get() = LocalDateTime.of(2010, 7, 24, 20, 54)
        override val expirationDate: LocalDateTime?
            get() = LocalDateTime.of(2024, 3, 8, 18, 12)
        override val thumbnail: PictureImage
            get() = object : PictureImage {
                override val iid: String
                    get() = "4321"
                override val bitmap: Bitmap?
                    get() = null

                override fun load(): CompletableFuture<Bitmap> {
                    return CompletableFuture.completedFuture(bitmap)
                }
                override fun save(): CompletableFuture<Void> {
                    TODO("Not yet implemented")
                }
            }
        override val coarseLocation: Location
            get() = TODO("Not yet implemented")
        override val correctLocation: Location
            get() = TODO("Not yet implemented")
        override val claims: List<String>
            get() = TODO("Not yet implemented")
    }
    GeoHuntTheme {
        Challenge(challenge = dummyChallenge)
    }
}