package com.github.geohunt.app.ui.components.activehunts

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.mocks.MockChallenge
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.ui.screens.activehunts.ActiveHuntsScreen
import com.github.geohunt.app.ui.screens.activehunts.ActiveHuntsViewModel
import com.github.geohunt.app.ui.theme.GeoHuntTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class ActiveHuntsTest {
    @get:Rule
    val testRule = createComposeRule()

    private var exploreCallbackCalled = false
    private var exploreChallengeCalled: Challenge? = null

    private fun setupComposable(challenges: List<Challenge>) {
        val activeHuntsStateFlow = MutableStateFlow(challenges).asStateFlow()
        val nameStateFlow = MutableStateFlow("John Wick").asStateFlow()

        val mockViewModel: ActiveHuntsViewModel = mock {
            on { activeHunts } doReturn activeHuntsStateFlow
            on { getAuthorName(any()) } doReturn nameStateFlow
        }

        exploreCallbackCalled = false
        exploreChallengeCalled = null
        testRule.setContent {
            GeoHuntTheme {
                ActiveHuntsScreen(
                    openExploreTab = { exploreCallbackCalled = true },
                    openChallengeView = { exploreChallengeCalled = it },
                    viewModel = mockViewModel
                )
            }
        }
    }

    @Test
    fun titleTextIsDisplayed() {
        setupComposable(listOf())

        testRule.onNodeWithText("Active hunts").assertIsDisplayed()
    }

    @Test
    fun textIsDisplayedOnEmptyChallengeList() {
        setupComposable(listOf())

        testRule.onNodeWithText("No challenges yet", substring = true).assertIsDisplayed()
        testRule.onNodeWithText("Search", substring = true, useUnmergedTree = true).assertIsDisplayed()
    }

    private val dummyChallenge = MockChallenge(id = "testId")

    @Test
    fun atLeastOneChallengesIsDisplayed() {
        val challenges = listOf(dummyChallenge, dummyChallenge, dummyChallenge)
        setupComposable(challenges)

        testRule.onNodeWithTag("challenge_row")
                .onChildren()
                //We use assertAny to make sure there is at least one node filling the condition
                .assertAny(hasContentDescription("Challenge ${dummyChallenge.id}"))
    }

    @Test
    fun callbackIsCalledByButton() {
        setupComposable(listOf())

        testRule.onNodeWithText("Search nearby challenges", useUnmergedTree = true)
                .onParent()
                .assertHasClickAction()
                .performClick()

        assertThat(exploreCallbackCalled, equalTo(true))
    }

    @Test
    fun challengeCallbackIsCalled() {
        val mockChallenge = MockChallenge()
        setupComposable(listOf(mockChallenge))

        testRule.onNodeWithTag("challenge-box-${mockChallenge.id}")
                .assertHasClickAction()
                .performClick()

        assertThat(exploreChallengeCalled, equalTo(mockChallenge))
    }
}