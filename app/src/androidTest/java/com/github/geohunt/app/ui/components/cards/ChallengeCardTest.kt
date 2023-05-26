package com.github.geohunt.app.ui.components.cards

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import com.github.geohunt.app.mocks.mockUser
import com.github.geohunt.app.model.User
import com.github.geohunt.app.utility.quantizeToLong
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class ChallengeCardTest {
    @get:Rule
    val testRule = createComposeRule()

    @Test
    fun challengeCardTitleDisplaysInformation() {
        val dummyDate = LocalDateTime.now().minusDays(12).minusHours(12)
        testRule.setContent {
            ChallengeCardTitle(
                    author = mockUser(displayName = "Dario"),
                    onUserClick = { },
                    distance = { 100.0 },
                    publicationDate = dummyDate,
                    isFollowing = true,
                    onFollow = {_, _ ->  },
                    canLeaveHunt = true,
                    onLeaveHunt = { })
        }

        testRule.onNodeWithText("Dario", useUnmergedTree = true)
                .assertIsDisplayed()

        testRule.onNodeWithText("${100.0.quantizeToLong(0.1) / 10.0}km away", useUnmergedTree = true, substring = true)
                .assertIsDisplayed()

        testRule.onNodeWithText("12 days ago", useUnmergedTree = true, substring = true)
                .assertIsDisplayed()

    }

    @Test
    fun challengeInformationCallbacksAreCalled() {
        val dummyDate = LocalDateTime.now().minusDays(12).minusHours(12)
        var onMouseClicked: User? = null
        var onFollowUser: User? = null
        var onFollowBool = false
        var onLeaveClicked = false
        val mockUser = mockUser(displayName = "Dario")
        testRule.setContent {
            ChallengeCardTitle(
                    author = mockUser,
                    onUserClick = { onMouseClicked = it },
                    distance = { 100.0 },
                    publicationDate = dummyDate,
                    isFollowing = false,
                    onFollow = {u, bool -> onFollowUser = u; onFollowBool = bool },
                    canLeaveHunt = true,
                    onLeaveHunt = { onLeaveClicked = true })
        }

        testRule.waitForIdle()

        testRule.onNodeWithTag("profile", useUnmergedTree = true)
                .assertHasClickAction()
                .performClick()
        Assert.assertEquals(mockUser, onMouseClicked)

        testRule.onNodeWithTag("menu-button", useUnmergedTree = true)
                .assertHasClickAction()
                .performClick()

        testRule.onNodeWithTag("follow-icon", useUnmergedTree = true)
                .onParent()
                .assertHasClickAction()
                .performClick()
        Assert.assertTrue(onFollowBool)
        Assert.assertEquals(mockUser, onFollowUser)

        testRule.onNodeWithTag("menu-button", useUnmergedTree = true)
                .assertHasClickAction()
                .performClick()

        testRule.onNodeWithText("Leave the hunt", useUnmergedTree = true)
                .assertHasClickAction()
                .performClick()
        Assert.assertTrue(onLeaveClicked)
    }

    @Test
    fun challengeInformationCallbacksAreCalled2() {
        val dummyDate = LocalDateTime.now().minusDays(12).minusHours(12)
        var onLeaveClicked = false
        val mockUser = mockUser(displayName = "Dario")
        testRule.setContent {
            ChallengeCardTitle(
                    author = mockUser,
                    onUserClick = { },
                    distance = { 100.0 },
                    publicationDate = dummyDate,
                    isFollowing = false,
                    onFollow = {_, _ -> },
                    canLeaveHunt = true,
                    onLeaveHunt = { onLeaveClicked = true })
        }

        testRule.waitForIdle()

        testRule.onNodeWithTag("menu-button", useUnmergedTree = true)
                .assertHasClickAction()
                .performClick()

        testRule.onNodeWithText("Leave the hunt", useUnmergedTree = true)
                .onParent()
                .assertHasClickAction()
                .performClick()
        Assert.assertTrue(onLeaveClicked)
    }
}