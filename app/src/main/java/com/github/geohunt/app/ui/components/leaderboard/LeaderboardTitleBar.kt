package com.github.geohunt.app.ui.components.leaderboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.ui.theme.Lobster

enum class LeaderboardTimeFilter(val text: String) {
    ALL("All time"),
    MONTHLY("Monthly"),
    WEEKLY("Weekly")
}

/**
 * Draws the leaderboard title, as well as the time filter on the top-right
 * of the bar.
 */
@Composable
fun LeaderboardTitleBar() {
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

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Leaderboard",
            fontSize = 40. sp,
            fontFamily = Lobster
        )

        Spacer(Modifier.weight(1.0f))

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
}