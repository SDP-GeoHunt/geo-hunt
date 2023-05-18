package com.github.geohunt.app.ui.screens.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class HomeScreenSelectorTest {
    @get:Rule
    val c = createComposeRule()

    @Test
    fun clickingOnButtonShowsDropdown() {
        c.setContent {
            HomeScreenSelector(currentScreen = HomeScreens.Feed, onChange = {})
        }
        c.onNodeWithTag("dropdown").assertDoesNotExist()
        c.onNodeWithTag("show-dropdown").performClick()
        c.onNodeWithTag("dropdown").assertIsDisplayed()
    }

    @Test
    fun showsCorrectInitialCurrentScreen_Feed() {
        c.setContent {
            HomeScreenSelector(currentScreen = HomeScreens.Feed, onChange = {})
        }
        c.onNodeWithTag("show-dropdown").assertTextContains("Feed")
    }

    @Test
    fun showsCorrectInitialCurrentScreen_Bounties() {
        c.setContent {
            HomeScreenSelector(currentScreen = HomeScreens.Bounties, onChange = {})
        }
        c.onNodeWithTag("show-dropdown").assertTextContains("Bounties")
    }

    @Test
    fun clickingOnDropdownItemTriggersCallback_Feed() {
        val cf = CompletableFuture<HomeScreens>()
        c.setContent {
            HomeScreenSelector(currentScreen = HomeScreens.Feed, onChange = { cf.complete(it) })
        }
        c.onNodeWithTag("show-dropdown").performClick()
        c.onNodeWithTag("feed-dropdown-item").performClick()
        assert(cf.get(2, TimeUnit.SECONDS) == HomeScreens.Feed)
    }

    @Test
    fun clickingOnDropdownItemTriggersCallback_Bounties() {
        val cf = CompletableFuture<HomeScreens>()
        c.setContent {
            HomeScreenSelector(currentScreen = HomeScreens.Bounties, onChange = { cf.complete(it) })
        }
        c.onNodeWithTag("show-dropdown").performClick()
        c.onNodeWithTag("bounties-dropdown-item").performClick()
        assert(cf.get(2, TimeUnit.SECONDS) == HomeScreens.Bounties)
    }
}