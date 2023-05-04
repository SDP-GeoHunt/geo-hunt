package com.github.geohunt.app.ui.components.activehunts

import androidx.compose.ui.test.assertContentDescriptionContains
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.github.geohunt.app.mocks.MockChallenge
import com.github.geohunt.app.ui.theme.GeoHuntTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChallengePreviewTest {
    @get:Rule
    val testRule = createComposeRule()

    private val challengeId = "98d755ad-NRDJLd1aM1I2QXK4qjD"

    private val dummyChallenge = MockChallenge(
        id = challengeId,
        authorId = "user"
    )

    @Before
    fun setupComposable() {
        testRule.setContent {
            GeoHuntTheme {
                ChallengePreview(challenge = dummyChallenge, getAuthorName = {
                    MutableStateFlow("Debug User").asStateFlow()
                })
            }
        }
    }

    @Test
    fun challengeInformationAreDisplayed() {
        testRule.onNodeWithText("Debug User", substring = true).assertIsDisplayed()
        testRule.onNodeWithText("Italy", substring = true).assertIsDisplayed()
    }

    @Test
    fun iconsAreDisplayed() {
        testRule.onAllNodesWithContentDescription("icon", substring = true).assertCountEquals(3)
        testRule.onNodeWithContentDescription("person", substring = true).assertIsDisplayed()
        testRule.onNodeWithContentDescription("location", substring = true).assertIsDisplayed()
        testRule.onNodeWithContentDescription("calendar", substring = true).assertIsDisplayed()
    }

    @Test
    fun imageIsDisplayed() {
        testRule.onNodeWithContentDescription("Challenge", substring = true).assertIsDisplayed().assertContentDescriptionContains(challengeId, substring = true)
    }
}