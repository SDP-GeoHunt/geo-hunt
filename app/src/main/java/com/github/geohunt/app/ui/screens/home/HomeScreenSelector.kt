package com.github.geohunt.app.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandCircleDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R

@Composable
internal fun HomeScreenSelector(currentScreen: HomeScreens, onChange: (HomeScreens) -> Unit) {
    var isDropMenuVisible by remember { mutableStateOf(false) }

    @Composable
    fun generateDropDownMenuItem(item: HomeScreens, testTag: String) {
        DropdownMenuItem(
            onClick = { isDropMenuVisible = false; onChange(item) },
            modifier = Modifier.testTag(testTag)
        ) {
            Text(item.title())
        }
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .height(46.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            TextButton(
                onClick = { isDropMenuVisible = true },
                modifier = Modifier.testTag("show-dropdown")
            ) {
                Text(currentScreen.title())
                Spacer(Modifier.width(8.dp))
                Icon(imageVector = Icons.Default.ExpandCircleDown, contentDescription = null)
            }
            Spacer(Modifier.weight(1f))
            Text(stringResource(id = R.string.app_name), color = MaterialTheme.colors.primary)
        }

        DropdownMenu(
            expanded = isDropMenuVisible,
            onDismissRequest = { isDropMenuVisible = false },
            modifier = Modifier.testTag("dropdown")
        ) {
            generateDropDownMenuItem(item = HomeScreens.Feed, "feed-dropdown-item")
            generateDropDownMenuItem(item = HomeScreens.Bounties, "bounties-dropdown-item")
        }
    }

}