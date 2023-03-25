package com.github.geohunt.app.ui.components.activehunts

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.ui.theme.GeoHuntTheme
import org.junit.Rule
import org.junit.Test

class ActiveHuntsTest {
    @get:Rule
    val testRule = createComposeRule()

    private fun setupComposable(challenges: List<LazyRef<Challenge>>) {
        testRule.setContent {
            GeoHuntTheme {
                ActiveHunts(challenges = challenges)
            }
        }
    }

    @Test
    fun titleTextIsDisplayed() {
        setupComposable(listOf())

        testRule.onNodeWithText("Active hunts").assertIsDisplayed()
    }

    @Test
    fun textIsDisplayedOnEmptyChallengeList() {
        setupComposable(listOf())

        testRule.onNodeWithText("No challenges yet", substring = true).assertIsDisplayed()
        testRule.onNodeWithText("Search", substring = true, useUnmergedTree = true).assertIsDisplayed()
    }
}