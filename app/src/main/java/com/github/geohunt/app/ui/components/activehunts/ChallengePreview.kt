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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.FetchComponent
import java.time.LocalDateTime
import com.github.geohunt.app.ui.rememberLazyRef
import com.github.geohunt.app.ui.theme.geoHuntRed
import com.github.geohunt.app.utility.DateFormatUtils


@Composable
fun ChallengePreview(challenge: LazyRef<Challenge>) {

    FetchComponent(lazyRef = { challenge }) { resolvedChallenge ->
        Column(modifier = Modifier.fillMaxSize()) {
            ChallengeImage(challenge = resolvedChallenge, modifier = Modifier.weight(1F))

            ChallengeDescription(challenge = resolvedChallenge)
        }
    }

}

@Composable
fun ChallengeImage(challenge: Challenge, modifier: Modifier) {
    val thumbnail = challenge.thumbnail

    FetchComponent(lazyRef = { thumbnail }) {resolvedThumbnail ->
        Image(painter = BitmapPainter(resolvedThumbnail.asImageBitmap()),
                contentDescription = "Challenge ${challenge.cid}",
                modifier = modifier.clip(RoundedCornerShape(5.dp)))
    }
}

@Composable
fun ChallengeDescription(challenge: Challenge) {
    Column(verticalArrangement = Arrangement.SpaceBetween) {
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
