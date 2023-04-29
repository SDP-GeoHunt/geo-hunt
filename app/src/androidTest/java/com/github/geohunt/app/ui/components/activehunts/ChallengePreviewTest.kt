package com.github.geohunt.app.ui.components.activehunts

import androidx.compose.ui.test.assertContentDescriptionContains
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.ui.theme.GeoHuntTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class ChallengePreviewTest {
    @get:Rule
    val testRule = createComposeRule()

    private val challengeId = "98d755ad-NRDJLd1aM1I2QXK4qjD"

    private val dummyChallenge = Challenge(
        id = challengeId,
        authorId = "user",
        photoUrl = "",
        location = Location(0.0, 0.0),
        publishedDate = LocalDateTime.of(10, 10, 10, 10, 10),
        expirationDate = null,
        difficulty = Challenge.Difficulty.MEDIUM,
        description = null
    )

    @Before
    fun setupComposable() {
        testRule.setContent {
            GeoHuntTheme {
                ChallengePreview(challenge = dummyChallenge, getAuthorName = {
                    MutableStateFlow("John Wick").asStateFlow()
                })
            }
        }
    }

    @Test
    fun challengeInformationAreDisplayed() {
        testRule.onNodeWithText("Expires", substring = true).assertIsDisplayed().assertTextContains("never", substring = true)
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