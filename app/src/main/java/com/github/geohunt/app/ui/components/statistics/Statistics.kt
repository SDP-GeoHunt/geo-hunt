package com.github.geohunt.app.ui.components.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.R
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.ui.components.utils.ListDropdownMenu
import com.github.geohunt.app.ui.theme.Lobster

/**
 * A composable made to show statistics of a user,
 * for now it shows the following information :
 *  - The evolution of the users points in time,
 *  also allowing the user to select if they want the graph on last week/month/year
 *
 * @param claims The full list of claims of a user, only the part the user wants to see is displayed
 */
@Composable
fun Statistics(claims: List<Claim>) {
    Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
        StatisticsTitle()

        if (claims.size < 2) {
            EmptyStatisticsScreen()
        } else {
            DisplayStatistics(claims = claims)
        }
    }
}

/**
 * Screen shown when there aren't enough claims to build interesting statistics
 * (1 claim or less)
 */
@Composable
fun EmptyStatisticsScreen() {
    Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center) {
        Text(text = stringResource(id = R.string.statistics_empty_screen_text),
                fontSize = 18.sp)
    }
}

/**
 * Screen containing the Graph and a box to select what dates we want to see
 */
@Composable
fun DisplayStatistics(claims: List<Claim>) {
    val dateGranularityState = remember { mutableStateOf(DateGranularity.WEEK) }

    Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
        ListDropdownMenu(state = dateGranularityState,
                update = { },
                elements = DateGranularity.values().toList())
                { it.toString() }

        ClaimPointsGraph(claims = claims, dateGranularity = dateGranularityState.value)
    }
}

@Composable
fun StatisticsTitle() {
    Text(text = stringResource(id = R.string.statistics_title),
            fontFamily = Lobster,
            fontSize = 40.sp)
}
