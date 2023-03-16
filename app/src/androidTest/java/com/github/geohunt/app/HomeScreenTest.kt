package com.github.geohunt.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
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
    fun imagesVisibleHeader() {
        testRule.onNodeWithTag(R.drawable.header.toString())
            .assertIsDisplayed()
//        testRule.onNodeWithTag(R.drawable.mock_image_1.toString())
//            .assertIsDisplayed()
//        testRule.onNodeWithTag(R.drawable.mock_user.toString())
//            .assertIsDisplayed()
//        testRule.onNodeWithTag(R.drawable.likes.toString())
//            .assertIsDisplayed()
    }

    @Test
    fun imageVisibleThumbnail() {
        testRule.onNodeWithTag(R.drawable.mock_image_1.toString())
            .assertIsDisplayed()
    }

    @Test
    fun imageVisibleUserImage() {
        testRule.onNodeWithTag(R.drawable.mock_user.toString())
            .assertIsDisplayed()
    }

    @Test
    fun imageVisibleLikeImage() {
        testRule.onNodeWithTag(R.drawable.likes.toString())
            .assertIsDisplayed()
    }

    @Test
    fun textsVisible() {
        testRule.onAllNodesWithText("John Smith").onFirst().assertExists()
        testRule.onAllNodesWithText("57").onFirst().assertExists()
    }
}