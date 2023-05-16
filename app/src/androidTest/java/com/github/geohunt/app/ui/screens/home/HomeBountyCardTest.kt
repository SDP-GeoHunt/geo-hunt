package com.github.geohunt.app.ui.screens.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.geohunt.app.mocks.MockChallenge
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class HomeBountyCardTest {
    @get:Rule
    val c = createComposeRule()

    @Test
    fun nbChallengesTextShowsLoadingIfNotReadyYet() {
        c.setContent {
            HomeBountyCard(
                name = "test",
                author = flowOf(null),
                expiresIn = LocalDateTime.MAX,
                challengesFlows = flowOf(null),
                nbMembersFlow = flowOf(null),
                isInside = false,
                onClick = {})
        }

        c.onNodeWithTag("challenges-#").assertTextEquals("…")
    }

    @Test
    fun showsCorrectNbChallengesIfReady() {
        c.setContent {
            HomeBountyCard(
                name = "test",
                author = flowOf(null),
                expiresIn = LocalDateTime.MAX,
                challengesFlows = flowOf(listOf(MockChallenge(), MockChallenge(), MockChallenge())), // three challenges
                nbMembersFlow = flowOf(null),
                isInside = false,
                onClick = {})
        }

        c.onNodeWithTag("challenges-#").assertTextEquals("3")
    }

    @Test
    fun nbMembersTextShowsLoadingIfNotReadyYet() {
        c.setContent {
            HomeBountyCard(
                name = "test",
                isInside = false,
                author = flowOf(null),
                expiresIn = LocalDateTime.MAX,
                challengesFlows = flowOf(null),
                nbMembersFlow = flowOf(null),
                onClick = {})
        }

        c.onNodeWithTag("members-#").assertTextEquals("…")
    }

    @Test
    fun showsCorrectNbMembersIfReady() {
        c.setContent {
            HomeBountyCard(
                name = "test",
                isInside = false,
                author = flowOf(null),
                expiresIn = LocalDateTime.MAX,
                challengesFlows = flowOf(null), // three challenges
                nbMembersFlow = flowOf(421),
                onClick = {})
        }

        c.onNodeWithTag("members-#").assertTextEquals("421")
    }

    @Test
    fun clickingOnJoinButtonTriggersCallback() {
        val cf = CompletableFuture<Void?>()
        c.setContent {
            HomeBountyCard(
                isInside = false,
                name = "test",
                author = flowOf(null),
                expiresIn = LocalDateTime.MAX,
                challengesFlows = flowOf(null), // three challenges
                nbMembersFlow = flowOf(421),
                onClick = { cf.complete(null) })
        }
        c.onNodeWithTag("join-btn").performClick()
        cf.get(2, TimeUnit.SECONDS)
    }

    @Test
    fun showsName() {
        c.setContent {
            HomeBountyCard(
                isInside = false,
                name = "Bounty name",
                author = flowOf(null),
                expiresIn = LocalDateTime.MAX,
                challengesFlows = flowOf(null), // three challenges
                nbMembersFlow = flowOf(421),
                onClick = { })
        }
        c.onNodeWithText("Bounty name").assertIsDisplayed()
    }

    @Test
    fun showsSeeButtonInsteadOfJoinIfInside() {
        c.setContent {
            HomeBountyCard(
                isInside = true,
                name = "Bounty name",
                author = flowOf(null),
                expiresIn = LocalDateTime.MAX,
                challengesFlows = flowOf(null), // three challenges
                nbMembersFlow = flowOf(421),
                onClick = { })
        }
        c.onNodeWithText("See").assertIsDisplayed()
    }
}