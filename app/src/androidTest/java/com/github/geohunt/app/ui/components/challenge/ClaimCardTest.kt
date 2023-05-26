package com.github.geohunt.app.ui.components.challenge

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.geohunt.app.i18n.toSuffixedString
import com.github.geohunt.app.mocks.MockClaim
import com.github.geohunt.app.mocks.mockUser
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class ClaimCardTest {
    @get:Rule
    val testRule = createComposeRule()

    @Test
    fun userInfoIsDisplayed() {
        val mockUser = mockUser()
        val stateFlow = MutableStateFlow(mockUser)
        testRule.setContent {
            ClaimCard(retrieveUser = { stateFlow }, claim = MockClaim(), fnViewImageCallback = { })
        }

        testRule.onNodeWithText(mockUser.name).assertIsDisplayed()
    }

    @Test
    fun claimDateIsDisplayed() {
        val mockUser = mockUser()
        val stateFlow = MutableStateFlow(mockUser)
        testRule.setContent {
            ClaimCard(retrieveUser = { stateFlow }, claim = MockClaim(claimDate = LocalDateTime.now().minusDays(5).minusHours(12)), fnViewImageCallback = { })
        }

        testRule.onNodeWithText("claimed 5 days ago", substring = true)
    }

    @Test
    fun imageViewCallbackIsCalled() {
        val mockUser = mockUser()
        val stateFlow = MutableStateFlow(mockUser)
        var callbackClicked = ""
        testRule.setContent {
            ClaimCard(retrieveUser = { stateFlow }, claim = MockClaim(photoUrl = "url"), fnViewImageCallback = { callbackClicked = it })
        }

        testRule.onNodeWithContentDescription("Claimed image").assertHasClickAction().performClick()
        Assert.assertEquals("url", callbackClicked)
    }

    @Test
    fun claimDistanceIsShown() {
        val mockUser = mockUser()
        val stateFlow = MutableStateFlow(mockUser)
        testRule.setContent {
            ClaimCard(retrieveUser = { stateFlow }, claim = MockClaim(distance = 100), fnViewImageCallback = { })
        }

        testRule.onNodeWithText(100.toSuffixedString(), substring = true).assertIsDisplayed()
    }
}