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
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Group
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.i18n.DateFormatUtils.getInTimeRangeString
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Challenge
import java.time.LocalDateTime

@Composable
fun BountyPreview(bounty: Bounty, firstChallenge: Challenge) {
    Column(modifier = Modifier.fillMaxSize()) {
        ChallengeImage(firstChallenge, modifier = Modifier.weight(0.90F))

        Spacer(modifier = Modifier.size(10.dp))

        BountyDescription(bounty, modifier = Modifier.weight(0.10F))
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
    Row(modifier = Modifier.fillMaxWidth()) {
        androidx.compose.material.Icon(Icons.Rounded.CalendarMonth, contentDescription = "calendar_icon")

        Spacer(modifier = Modifier.size(10.dp))

        Text(getInTimeRangeString(startingDate, expirationDate,
                R.string.starts_in,
                R.string.ends_in,
                R.string.expired_since))
    }
}

@Composable
fun BountyName(name: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Rounded.Group, contentDescription = "bounty_icon")

        Spacer(modifier = Modifier.size(10.dp))

        Text(name)
    }
}
