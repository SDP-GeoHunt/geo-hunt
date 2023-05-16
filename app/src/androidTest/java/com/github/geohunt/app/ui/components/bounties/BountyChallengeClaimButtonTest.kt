package com.github.geohunt.app.ui.components.bounties

import android.net.Uri
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.geohunt.app.data.local.LocalPicture
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.sensor.LocationRequestState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@ExperimentalCoroutinesApi
class BountyChallengeClaimButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNotClaimedButtonBecomesClaimedAfterClicking() = runTest {
        val testClaimChallengeFunction = mock<ClaimChallengeFunction>()
        val locationRequestState = mock<LocationRequestState> {
            onBlocking { requestLocation() }.thenReturn(CompletableFuture.completedFuture(Location()))
        }
        val photo = LocalPicture(Uri.EMPTY)
        val challenge = Challenge(
            id = "testId",
            authorId = "testAuthorId",
            photoUrl = "testUrl",
            location = Location(),
            publishedDate = LocalDateTime.now(),
            expirationDate = null,
            difficulty = Challenge.Difficulty.EASY,
            description = "Test description"
        )

        composeTestRule.setContent {
            BountyChallengeClaimButton(
                challenge = challenge,
                locationRequestState = locationRequestState,
                photo = photo,
                claimChallengeFunction = testClaimChallengeFunction,
                isClaimed = false
            )
        }

        // Verify initial button state
        composeTestRule
            .onNodeWithText("Claim Challenge")
            .assertExists()
            // Simulate button click
            .performClick()

        // Verify that button went to the claimed state
        composeTestRule
            .onNodeWithText("Challenge Claimed")
            .assertExists()
    }

    @Test
    fun testAlreadyClaimedButtonDoesNotChangeAfterClicking() = runTest {
        val testClaimChallengeFunction = mock<ClaimChallengeFunction>()
        val locationRequestState = mock<LocationRequestState> {
            onBlocking { requestLocation() }.thenReturn(CompletableFuture.completedFuture(Location()))
        }
        val photo = LocalPicture(Uri.EMPTY)
        val challenge = Challenge(
            id = "testId",
            authorId = "testAuthorId",
            photoUrl = "testUrl",
            location = Location(),
            publishedDate = LocalDateTime.now(),
            expirationDate = null,
            difficulty = Challenge.Difficulty.EASY,
            description = "Test description"
        )

        composeTestRule.setContent {
            BountyChallengeClaimButton(
                challenge = challenge,
                locationRequestState = locationRequestState,
                photo = photo,
                claimChallengeFunction = testClaimChallengeFunction,
                isClaimed = true
            )
        }

        // Verify initial button state
        composeTestRule
            .onNodeWithText("Challenge Claimed")
            .assertExists()
            // Simulate button click
            .performClick()

        // Verify that button stayed in the claimed state
        composeTestRule
            .onNodeWithText("Challenge Claimed")
            .assertExists()
    }
}
