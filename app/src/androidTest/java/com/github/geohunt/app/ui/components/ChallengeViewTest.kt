@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.geohunt.app.ui.components

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.geohunt.app.R
import com.github.geohunt.app.data.repository.*
import com.github.geohunt.app.mocks.*
import com.github.geohunt.app.ui.components.challenge.ChallengeView
import com.github.geohunt.app.ui.components.challenge.ChallengeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChallengeViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var appContainer : AppContainer

    private fun createViewModel(auth: AuthRepositoryInterface): ChallengeViewModel {
        return ChallengeViewModel(
            challengeRepository = appContainer.challenges,
            claimRepository = appContainer.claims,
            userRepository = appContainer.user,
            authRepository = auth,
            followRepository = appContainer.follow,
            activeHuntsRepository = appContainer.activeHunts
        )
    }

    @Before
    fun setup() {
        appContainer = AppContainer.getEmulatedFirebaseInstance(
            androidx.test.core.app.ApplicationProvider.getApplicationContext() as Application
        )
    }

    private fun createTestBitmap(context: Context) : Bitmap {
        return ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)?.toBitmap()!!
    }
    
    @Test
    fun testChallenge1() {
        var route = ""
        val auth = MockAuthRepository()
        val vm = createViewModel(auth)

        composeTestRule.setContent {
            ChallengeView(
                cid = "163f921c-ML2eCQ52mAQlvCEQZ2n",
                fnViewImageCallback = { route = "image-view/$it" },
                fnClaimHuntCallback = { route = "claim/$it" } ,
                fnGoBackBtn = { route = ".." },
                viewModel = vm)
        }

        composeTestRule.waitUntil(2000) {
            composeTestRule.onAllNodesWithTag("challenge-main-image")
                .fetchSemanticsNodes()
                .size == 1
        }

        composeTestRule.onNodeWithContentDescription("Challenge Image")
            .assertIsDisplayed()
            .performClick()
        assertThat(route, equalTo("image-view/http://10.0.2.2:9199/geohunt-1.appspot.com/images/challenges-images.png"))

        composeTestRule.onNodeWithText("dn2")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Follow")
            .performScrollTo()
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule.onNodeWithText("Join Hunt")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule.onNodeWithTag("description-more-btn")
            .performScrollTo()
            .assertIsDisplayed()
            .assert(hasText("more…"))
            .performClick()
    }

}