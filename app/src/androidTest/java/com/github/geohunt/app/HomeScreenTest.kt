package com.github.geohunt.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.github.geohunt.app.ui.homescreen.HomeScreen
import com.github.geohunt.app.ui.homescreen.MockChallenge
import com.github.geohunt.app.ui.theme.GeoHuntTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

class HomeScreenTest {

    private val mockChallenges = List(100) {
        val challengeId = UUID.randomUUID().toString()
        val profilePhoto = R.drawable.mock_user
        val username = "John Smith"
        val challengeImg = R.drawable.mock_image_1
        val likes = 57
        MockChallenge(challengeId, challengeImg, username, profilePhoto, likes)
    }

    @get:Rule
    val testRule = createComposeRule()

    @Before
    fun setupComposable() {
        testRule.setContent {
            GeoHuntTheme {
                HomeScreen(challenges = mockChallenges)
            }
        }
    }

    @Test
    fun imagesVisible() {
        testRule.onNodeWithTag(R.drawable.header.toString())
            .assertIsDisplayed()
        testRule.onNodeWithTag(R.drawable.mock_image_1.toString())
            .assertIsDisplayed()
        testRule.onNodeWithTag(R.drawable.mock_user.toString())
            .assertIsDisplayed()
        testRule.onNodeWithTag(R.drawable.likes.toString())
            .assertIsDisplayed()
    }

    @Test
    fun textsVisible() {
        testRule.onNodeWithText("John Smith", substring = true).assertExists()
        testRule.onNodeWithText("57", substring = true).assertExists()
    }
}