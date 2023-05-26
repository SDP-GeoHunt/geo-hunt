package com.github.geohunt.app.ui.components

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GeoHuntTabsTest {
    @get:Rule
    val testRule = createComposeRule()

    @Test
    fun firstTabIsSelectedByDefault() {
        val tabs = List(3) {
            TabData("Tab ${it + 1}", onClick = {})
        }

        testRule.setContent {
            GeoHuntTabs(tabs)
        }

        testRule.onNodeWithTag("tab-0").assertIsSelected()
    }

    @Test
    fun tabIsSelectedUponClick() {
        val tabs = List(3) {
            TabData("Tab ${it + 1}", onClick = {})
        }

        testRule.setContent {
            GeoHuntTabs(tabs)
        }

        // Use reversed order to also test the first tab, which is selected by default
        for (i in tabs.indices.reversed()) {
            val tab = testRule.onNodeWithTag("tab-$i")
            tab.assertHasClickAction()
            tab.performClick()
            tab.assertIsSelected()
        }
    }

    @Test
    fun tabDataOnClickIsCalledOnClick() {
        var lastClickIndex = -1

        val tabs = List(3) {
            TabData("Tab ${it + 1}", onClick = { lastClickIndex = it })
        }

        testRule.setContent {
            GeoHuntTabs(tabs)
        }

        for (i in tabs.indices) {
            val tab = testRule.onNodeWithTag("tab-$i")
            tab.performClick()
            assertEquals(lastClickIndex, i)
        }
    }
}