package com.github.geohunt.app.ui.components.activehunts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.ui.theme.geoHuntRed
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime

/**
 * The preview of a challenge, contains basic information about the challenge
 * and the picture
 */
@Composable
fun ChallengePreview(challenge: Challenge, getAuthorName: (Challenge) -> StateFlow<String>) {
    Column(modifier = Modifier.fillMaxSize()) {
        ChallengeImage(challenge, modifier = Modifier.weight(0.85F))

        Spacer(modifier = Modifier.size(10.dp))

        ChallengeDescription(challenge, modifier = Modifier.weight(0.15F), getAuthorName = getAuthorName)
    }
}

/**
 * The image of the challenge
 * Note that this method tries to scale the image to the exact size of the box
 * this might be changed in the future as it could distort the image
 */
@Composable
fun ChallengeImage(
    challenge: Challenge,
    modifier: Modifier
) {
    val imageUrl = challenge.photoUrl

    Box(modifier = modifier.fillMaxWidth()) { //image "frame"
        AsyncImage(
            model = imageUrl,
            contentDescription = "Challenge ${challenge.id}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.Center)
                .clip(RoundedCornerShape(20.dp))
                .fillMaxSize()
        )
    }
}

/**
 * A basic description of the challenge,
 * displays the author, the location and the
 * expiration date
 */
@Composable
fun ChallengeDescription(challenge: Challenge, modifier: Modifier, getAuthorName: (Challenge) -> StateFlow<String>) {
    Column(modifier = modifier, verticalArrangement = Arrangement.SpaceBetween) {
        val authorName = getAuthorName(challenge).collectAsStateWithLifecycle()
        ChallengeAuthor(authorName.value)

        ChallengeLocation()

        ChallengeExpirationDate(expirationDate = challenge.expirationDate)
    }
}

@Composable
fun ChallengeAuthor(authorName: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Rounded.Person, contentDescription = "person_icon")

        Spacer(modifier = Modifier.size(10.dp))

        Text(authorName)
    }
}

@Composable
fun ChallengeLocation() {
    // TODO Hardcoded values, should be replaced with a way to find closest city or removed entirely
    val city = "Rome"
    val country = "Italy"

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Rounded.LocationOn, contentDescription = "location_icon")

        Spacer(modifier = Modifier.size(10.dp))

        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(city)
              }
                append(", $country")
            }
        )
    }
}

@Composable
fun ChallengeExpirationDate(expirationDate: LocalDateTime?) {
    val expires = if (expirationDate != null) "Expires in " else "Expires "
    val expirationDateFmt = DateFormatUtils.formatRemainingTime(expirationDate).lowercase()

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Rounded.CalendarMonth,
                contentDescription = "calendar_icon")

        Spacer(modifier = Modifier.size(10.dp))

        Text(buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                append(expires)

                withStyle(style = SpanStyle(color = geoHuntRed)) {
                    append(expirationDateFmt)
                }

                append(" !")
            }

        })
    }
}
