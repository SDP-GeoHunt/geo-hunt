package com.github.geohunt.app.ui.screens.bounty_team_select

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.model.User
import com.github.geohunt.app.ui.components.user.ProfileIcon
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Composable
fun BountyDetailsHeader(
    authorFlow: Flow<User?>,
    ends: LocalDateTime,
    name: String
) {
    val author by authorFlow.collectAsState(initial = null)

    Row(modifier = Modifier.padding(4.dp)){
        author?.let { author ->

            ProfileIcon(user = author, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(4.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = author.name)

                val endsIn = DateFormatUtils.getRemainingTimeString(
                    dateTime = ends,
                    formattingStringId = R.string.ends_in,
                    passedFormattingStringId = R.string.expired_since
                )
                Text(endsIn)
            }
        }

        Spacer(Modifier.weight(1f))
        Text(name, textAlign = TextAlign.Right, modifier = Modifier.padding(end = 4.dp))
    }
}