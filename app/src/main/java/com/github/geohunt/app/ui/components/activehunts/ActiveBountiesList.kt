package com.github.geohunt.app.ui.components.activehunts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Challenge

@Composable
fun ActiveBountiesList(
        bounties: List<Pair<Bounty, Challenge>>,
        openExploreTab: () -> Unit,
        openBountyView: (Bounty) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if(bounties.isEmpty()) {
            EmptyScreen(
                    text = stringResource(id = R.string.active_hunts_empty_bounties),
                    buttonText = stringResource(id = R.string.active_hunts_empty_bounties_button),
                    openExploreTab)
        }
        else {
            LazyRow(
                    modifier = Modifier.testTag("bounty_row"),
                    contentPadding = PaddingValues(30.dp, 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(bounties) { bounty ->
                    Box(modifier = Modifier
                            .size(300.dp, 600.dp)
                            .testTag("challenge-box-${bounty.first.bid}")
                            .clickable { openBountyView(bounty.first) })
                    {
                        BountyPreview(bounty.first, bounty.second)
                    }
                }
            }
        }
    }
}