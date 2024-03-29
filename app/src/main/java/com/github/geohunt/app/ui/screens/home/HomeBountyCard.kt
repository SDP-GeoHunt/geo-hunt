package com.github.geohunt.app.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.geohunt.app.R
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.User
import com.github.geohunt.app.ui.screens.bounty_team_select.BountyDetailsHeader
import com.github.geohunt.app.ui.screens.bounty_team_select.challengesImageSlider
import com.google.accompanist.pager.HorizontalPagerIndicator
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeBountyCard(
    author: Flow<User?>,
    onUserClick: (User) -> Unit,
    name: String,
    expiresIn: LocalDateTime,
    challengesFlows: Flow<List<Challenge>?>,
    nbMembersFlow: Flow<Int?>,
    isInside: Boolean,
    onClick: () -> Unit
) {

    val challenges by challengesFlows.collectAsStateWithLifecycle(initialValue = null)
    val nbMembers by nbMembersFlow.collectAsStateWithLifecycle(initialValue = null)

    Card(modifier = Modifier.padding(16.dp).testTag("bounty-card")) {
        Column {

            BountyDetailsHeader(author, expiresIn, name, onUserClick = onUserClick)

            val pagerStatus = challengesImageSlider(challenges)

            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Number of challenges
                Icon(
                    painterResource(
                        id = R.drawable.target_arrow
                    ), null
                )
                Text((challenges?.size ?: "…").toString(), modifier = Modifier.testTag("challenges-#"))

                InlineSeparator()

                Icon(Icons.Filled.Person, null)
                Text((nbMembers ?: "…").toString(), modifier = Modifier.testTag("members-#"))

                pagerStatus?.let {
                    val (nb_elements, state) = it

                    Spacer(Modifier.weight(1f))
                    HorizontalPagerIndicator(state, nb_elements)
                }

                Spacer(Modifier.weight(1f))
                Button(onClick = { onClick() }, modifier = Modifier.testTag("join-btn")) {
                    Text(stringResource(id = if (isInside) R.string.see else R.string.join))
                }
            }
        }
    }
}

@Composable
internal fun InlineSeparator() {
    Text("•", modifier = Modifier.padding(horizontal = 8.dp))
}

