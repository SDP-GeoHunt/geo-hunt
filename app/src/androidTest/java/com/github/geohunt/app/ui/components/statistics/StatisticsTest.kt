package com.github.geohunt.app.ui.components.statistics

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.github.geohunt.app.mocks.MockClaim
import com.github.geohunt.app.model.Claim
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class StatisticsTest {
    @get:Rule
    val testRule = createComposeRule()

    private val now = LocalDateTime.now()
    private val c1 = MockClaim(claimDate = now.minusDays(3), awardedPoints = 100L)
    private val c2 = MockClaim(claimDate = now, awardedPoints = 2023L)
    private val c3 = MockClaim(claimDate = now.minusDays(5), awardedPoints = 4000L)
    private val c4 = MockClaim(claimDate = now.minusDays(2), awardedPoints = 500)

    private fun setupComposable(claims: List<Claim>) {
        testRule.setContent {
            Statistics(claims = claims)
        }
    }

    @Test
    fun baseElementsAreDisplayed() {
        setupComposable(listOf(c1, c2, c3, c4))
        testRule.onNodeWithText("Statistics").assertIsDisplayed()
        testRule.onNodeWithText("week", substring = true, ignoreCase = true, useUnmergedTree = true)
                .assertIsDisplayed()
    }

    @Test
    fun emptyClaimsDisplaysEmptyStatisticsScreen() {
        setupComposable(listOf())
        testRule.onNodeWithText("You haven't claimed enough challenges yet").assertIsDisplayed()
    }

    @Test
    fun singleElementClaimsDisplaysEmptyStatisticsScreen() {
        setupComposable(listOf(c1))
        testRule.onNodeWithText("You haven't claimed enough challenges yet").assertIsDisplayed()
    }
}