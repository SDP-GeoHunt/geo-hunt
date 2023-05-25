package com.github.geohunt.app.ui.screens.bounty_team_select

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.geohunt.app.R
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.model.User
import com.github.geohunt.app.ui.components.cards.GeoHuntCardTitle
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Composable
fun BountyDetailsHeader(
    authorFlow: Flow<User?>,
    ends: LocalDateTime,
    name: String,
    onUserClick: (User) -> Unit
) {
    val author by authorFlow.collectAsState(initial = null)

    return GeoHuntCardTitle(
        author = author,
        onUserClick = onUserClick,
        subtitle = {
            val endsIn = DateFormatUtils.getRemainingTimeString(
                dateTime = ends,
                formattingStringId = R.string.ends,
                passedFormattingStringId = R.string.expired_since
            )
            Text("$name • $endsIn", style = MaterialTheme.typography.labelMedium)
        }
    )
}