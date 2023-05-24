package com.github.geohunt.app.ui.components.activehunts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AdsClick
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.i18n.DateFormatUtils.getInTimeRangeString
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Challenge
import java.time.LocalDateTime

/**
 * The preview of a bounty
 * Contains the basic information about a bounty
 * Which are :
 *  The image of one of the challenges contained in the bounty
 *  Information about the date of the bounty (starts in/expires in)
 *  The name of the bounty
 * @param bounty The bounty we take the information from
 * @param firstChallenge The challenge that will be displayed to represent the bounty
 */
@Composable
fun BountyPreview(bounty: Bounty, firstChallenge: Challenge) {
    Column(modifier = Modifier.fillMaxSize()) {
        ChallengeImage(firstChallenge, modifier = Modifier.weight(0.85F))

        Spacer(modifier = Modifier.size(10.dp))

        BountyDescription(bounty, modifier = Modifier.weight(0.15F))
    }
}

@Composable
fun BountyDescription(bounty: Bounty, modifier: Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.SpaceBetween) {
        BountyName(bounty.name)

        BountyDate(bounty.startingDate, bounty.expirationDate)
    }
}

@Composable
fun BountyDate(startingDate: LocalDateTime, expirationDate: LocalDateTime) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Rounded.CalendarMonth, contentDescription = "calendar_icon")

        Spacer(modifier = Modifier.size(10.dp))

        Text(getInTimeRangeString(startingDate, expirationDate,
                R.string.starts_in,
                R.string.ends_in,
                R.string.expired_since))
    }
}

@Composable
fun BountyName(name: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Rounded.AdsClick, contentDescription = "bounty_icon")

        Spacer(modifier = Modifier.size(10.dp))

        Text(name)
    }
}
