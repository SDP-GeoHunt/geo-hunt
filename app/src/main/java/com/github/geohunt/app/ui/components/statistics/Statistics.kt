package com.github.geohunt.app.ui.components.statistics

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
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.R
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.ui.components.utils.ListDropdownMenu
import com.github.geohunt.app.ui.theme.Lobster

@Composable
fun Statistics(claims: List<Claim>) {
    if(claims.size < 2) {
        EmptyStatisticsScreen()
    }
    else {
        DisplayStatistics(claims = claims)
    }
}

@Composable
fun EmptyStatisticsScreen() {
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Text(text = stringResource(id = R.string.statistics_empty_screen_text))
    }
}

@Composable
fun DisplayStatistics(claims: List<Claim>) {
    val dateGranularityState = remember { mutableStateOf(DateGranularity.WEEK) }

    Column(modifier = Modifier.fillMaxSize()) {
        StatisticsTitle()

        ListDropdownMenu(state = dateGranularityState,
                elements = DateGranularity.values().toList(),
                toString = { it.toString() })

        ClaimPointsGraph(claims = claims, dateGranularity = dateGranularityState.value)
    }
}

@Composable
fun StatisticsTitle() {
    Text(text = stringResource(id = R.string.statistics_title),
            fontFamily = Lobster,
            fontSize = 25.sp)
}
