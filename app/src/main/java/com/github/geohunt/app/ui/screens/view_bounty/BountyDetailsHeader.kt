package com.github.geohunt.app.ui.screens.view_bounty

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    ends: LocalDateTime
) {
    val author by authorFlow.collectAsState(initial = null)

    Row {
        author?.let { author ->

            ProfileIcon(user = author)
            Spacer(Modifier.width(4.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = author.name)

                val endsIn = DateFormatUtils.getElapsedTimeString(
                    dateTime = ends,
                    formattingStringId = R.string.ends_in
                )
                Text(endsIn)
            }
        }
    }
}