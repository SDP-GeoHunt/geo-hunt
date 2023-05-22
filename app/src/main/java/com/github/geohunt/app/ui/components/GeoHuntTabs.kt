package com.github.geohunt.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * Represents tab metadata used to construct [GeoHuntTabs], including a title and a click listener.
 *
 * @param title The title of the tab shown in the [TabRow]
 * @param onClick Callback used when the tab is clicked. This will be called even if the tab is
 *                already selected, so that custom actions can be taken (e.g. scroll to top)
 */
data class TabData(
    val title: String,
    val onClick: () -> Unit
)

/**
 * Creates a small indicator for the selected tab.
 *
 * Follows the Material Design 3 ["Primary tabs" specs](https://m3.material.io/components/tabs/specs#9d787b9b-604e-4bdb-981e-eefb276a7821).
 *
 * Sample output:
 * ![Tabs image](https://lh3.googleusercontent.com/XdkkXl-v-Juf0wnaE8bF58iQ8-iMBOeD4b009XNUwERhCJrpCHr2I4JfsLIWvGsg_U1nds-UEUfwtpMjLkDfgsl23u6P5EMLd7X0C0NJuw0=s0)
 *
 * @param tabPosition The tab position used to correctly position the indicator in the [TabRow].
 */
@Composable
fun GeoHuntTabIndicator(tabPosition: TabPosition) {
    val indicatorHeight = 3.dp // per the specs

    Box(
        Modifier
            .tabIndicatorOffset(currentTabPosition = tabPosition)
            .padding(horizontal = tabPosition.width / 3)
            .height(indicatorHeight)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(
                    topStart = indicatorHeight,
                    topEnd = indicatorHeight,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                )
            )
    )
}

/**
 * Creates a custom [TabRow] following Material Design 3 guidelines, and in particular the
 * ["primary" Tabs specifications](https://m3.material.io/components/tabs/specs).
 *
 * The current tab has a small indicator below it to indicate selection.
 */
@Composable
fun GeoHuntTabs(
    tabs: List<TabData>
) {
    val selectedTabIndex = remember { mutableStateOf(0) }

    TabRow(
        selectedTabIndex = selectedTabIndex.value,
        containerColor = Color.Transparent, // to comply with dark theme
        indicator = { tabPositions ->
            GeoHuntTabIndicator(tabPosition = tabPositions[selectedTabIndex.value])
        },
        divider = {} // Remove the default divider
    ) {
        tabs.forEachIndexed { index, tabData ->
            Tab(
                selected = selectedTabIndex.value == index,
                text = { Text(tabData.title) },
                onClick = {
                    selectedTabIndex.value = index
                    tabData.onClick()
                },
                modifier = Modifier.testTag("tab-$index")
            )
        }
    }
}