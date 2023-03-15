package com.github.geohunt.app.ui.components.leaderboard

import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

enum class LeaderboardTimeFilter(val text: String) {
    ALL("All time"),
    MONTHLY("Monthly"),
    WEEKLY("Weekly")
}

/**
 * Creates the leaderboard time filter, drawn on the top right of the leaderboard.
 */
@Composable
fun LeaderboardTimeFilter() {
    // Whether the dropdown is expanded
    var expanded by remember {
        mutableStateOf(false)
    }

    // The currently chosen dropdown option
    var filterOption by remember {
        mutableStateOf(LeaderboardTimeFilter.ALL)
    }

    @Composable
    fun DropdownOption(option: LeaderboardTimeFilter) {
        DropdownMenuItem(
            onClick = {
                expanded = false
                filterOption = option
            }
        ) {
            Text(option.text)
        }
    }

    Box {
        TextButton(
            onClick = { expanded = !expanded }
        ) {
            Text(filterOption.text, color = Color.Black)
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = filterOption.text,
                tint = Color.Black
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownOption(LeaderboardTimeFilter.ALL)
            DropdownOption(LeaderboardTimeFilter.MONTHLY)
            DropdownOption(LeaderboardTimeFilter.WEEKLY)
        }
    }
}