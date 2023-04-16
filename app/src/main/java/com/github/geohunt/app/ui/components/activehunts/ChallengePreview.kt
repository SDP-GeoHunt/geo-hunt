package com.github.geohunt.app.ui.components.activehunts

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.FetchComponent
import com.github.geohunt.app.ui.rememberLazyRef
import com.github.geohunt.app.ui.theme.geoHuntRed
import java.time.LocalDateTime

/**
 * The preview of a challenge, contains basic information about the challenge
 * and the picture
 */
@Composable
fun ChallengePreview(challenge: LazyRef<Challenge>, fnViewChallengeCallback: (String) -> Unit) {
    FetchComponent(lazyRef = { challenge }, modifier = Modifier.fillMaxSize()) { resolvedChallenge ->
        Column(modifier = Modifier.fillMaxSize()) {
            ChallengeImage(challenge = resolvedChallenge, modifier = Modifier.weight(0.85F))

            Spacer(modifier = Modifier.size(10.dp))

            ChallengeDescription(challenge = resolvedChallenge, modifier = Modifier.weight(0.15F))
        }
    }
}

/**
 * The image of the challenge
 * Note that this method tries to scale the image to the exact size of the box
 * this might be changed in the future as it could distort the image
 */
@Composable
fun ChallengeImage(challenge: Challenge, modifier: Modifier) {
    val thumbnail = challenge.thumbnail

    Box(modifier = modifier.fillMaxWidth()) { //image "frame"
        FetchComponent(lazyRef = { thumbnail }) {resolvedThumbnail ->
            Image(painter = BitmapPainter(resolvedThumbnail.asImageBitmap()),
                    contentDescription = "Challenge ${challenge.cid}",
                    modifier = Modifier.clip(RoundedCornerShape(20.dp)).fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center)
        }
    }
}

/**
 * A basic description of the challenge,
 * displays the author, the location and the
 * expiration date
 */
@Composable
fun ChallengeDescription(challenge: Challenge, modifier: Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.SpaceBetween) {
        AuthorName(challenge.author)

        Location()

        ExpirationTime(expirationDate = challenge.expirationDate)
    }
}

@Composable
fun AuthorName(author: LazyRef<User>) {
    val waitingAuthor = rememberLazyRef { author }
    Row(modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Rounded.Person, contentDescription = "person_icon")

        Spacer(modifier = Modifier.size(10.dp))

        if(waitingAuthor.value != null) Text(text = author.value!!.name)
        else Text(text = "...")
    }
}

@Composable
fun Location(){
    //TODO : Hardcoded values, should be replaced with a way to find closest city
    //or removed entirely
    val city = "Rome"
    val country = "Italy"

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Rounded.LocationOn,
        contentDescription = "location_icon")

        Spacer(modifier = Modifier.size(10.dp))

        Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append(city)
                    }
                    append(", $country")
                })

    }
}

@Composable
fun ExpirationTime(expirationDate: LocalDateTime?) {
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
