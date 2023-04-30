package com.github.geohunt.app.ui.components.statistics

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.github.geohunt.app.mocks.MockClaim
import com.github.geohunt.app.model.database.api.Claim
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class StatisticsTest {
    @get:Rule
    val testRule = createComposeRule()

    private val now = LocalDateTime.now()
    private val c1 = MockClaim(time = now.minusDays(3), awardedPoints = 100L)
    private val c2 = MockClaim(time = now, awardedPoints = 2023L)

    private fun setupComposable(claims: List<Claim>) {
        testRule.setContent {
            Statistics(claims = claims)
        }
    }

    @Test
    fun baseElementsAreDisplayed() {
        setupComposable(listOf(c1, c2))
        testRule.onNodeWithText("Statistics").assertIsDisplayed()
        testRule.onNodeWithText("week", substring = true, ignoreCase = true, useUnmergedTree = true)
                .assertIsDisplayed()
    }
}