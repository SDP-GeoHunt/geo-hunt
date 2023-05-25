package com.github.geohunt.app.ui.components.activehunts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.geohunt.app.R
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.model.Challenge
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
 * A basic description of the challenge,
 * displays the author, the location and the
 * expiration date
 */
@Composable
fun ChallengeDescription(challenge: Challenge, modifier: Modifier, getAuthorName: (Challenge) -> StateFlow<String>) {
    Column(modifier = modifier, verticalArrangement = Arrangement.SpaceBetween) {
        val authorName = getAuthorName(challenge).collectAsStateWithLifecycle()
        ChallengeAuthor(authorName.value)

        ChallengeExpirationDate(expirationDate = challenge.expirationDate)
    }
}

@Composable
fun ChallengeAuthor(authorName: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Rounded.Person, contentDescription = "person_icon")

        Spacer(modifier = Modifier.size(10.dp))

        Text(authorName)
    }
}

@Composable
fun ChallengeExpirationDate(expirationDate: LocalDateTime?) {
    val expirationDateFmt = if (expirationDate != null)
        DateFormatUtils.getRemainingTimeString(expirationDate, R.string.ends_in_bang, R.string.expired_since_bang)
        else stringResource(id = R.string.challenge_settings_date_never)


    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Rounded.CalendarMonth,
                contentDescription = "calendar_icon")

        Spacer(modifier = Modifier.size(10.dp))

        Text(expirationDateFmt, fontWeight = FontWeight.SemiBold)
    }
}
