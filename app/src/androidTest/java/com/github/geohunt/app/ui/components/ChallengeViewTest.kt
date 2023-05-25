package com.github.geohunt.app.ui.components

import android.app.Application
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.geohunt.app.data.repository.*
import com.github.geohunt.app.mocks.*
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.ui.components.challenge.ChallengeView
import com.github.geohunt.app.ui.components.challenge.ChallengeViewModel
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class ChallengeViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var appContainer : AppContainer

    private fun createViewModel(challenge: ChallengeRepositoryInterface,
                                claimRepository: ClaimRepositoryInterface = MockClaimRepository(),
                                followRepository: FollowRepositoryInterface = MockFollowRepository(),
                                userRepository: UserRepositoryInterface = MockUserRepository(),
                                activeHuntsRepository: ActiveHuntsRepositoryInterface = MockActiveHuntRepository(),
                                auth: AuthRepositoryInterface = MockAuthRepository()): ChallengeViewModel {
        return ChallengeViewModel(
            challengeRepository = challenge,
            claimRepository = claimRepository,
            userRepository = userRepository,
            authRepository = auth,
            followRepository = followRepository,
            activeHuntsRepository = activeHuntsRepository,
        )
    }

    @Before
    fun setup() {
        appContainer = AppContainer.getEmulatedFirebaseInstance(
            androidx.test.core.app.ApplicationProvider.getApplicationContext() as Application
        )
    }

    @Test
    fun testChallenge1() {
        var route = ""
        val challenge = MockChallenge(
            photoUrl = "http://10.0.2.2:9199/geohunt-1.appspot.com/images/challenges-images.png",
            authorId = "2",
            id = "163f921c-ML2eCQ52mAQlvCEQZ2n",
            description = "Voilà! In view, a humble vaudevillian veteran cast vicariously as both victim" +
                    " and villain by the vicissitudes of Fate. This visage, no mere veneer of vanity, is" +
                    " a vestige of the vox populi, now vacant, vanished. However, this valourous visitation " +
                    "of a bygone vexation stands vivified and has vowed to vanquish these venal and virulent" +
                    " vermin vanguarding vice and vouchsafing the violently vicious and voracious violation " +
                    "of volition! The only verdict is vengeance; a vendetta held as a votive, not in vain, " +
                    "for the value and veracity of such shall one day vindicate the vigilant and the virtuous. " +
                    "Verily, this vichyssoise of verbiage veers most verbose, so let me simply add that it’s" +
                    " my very good honour to meet you and you may call me V.",
        )
        val vm = createViewModel(
            MockChallengeRepository(challenge),
            MockClaimRepository(
                listOf(
                    Claim("13",
                        "2",
                        "1",
                        "http://10.0.2.2:9199/geohunt-1.appspot.com/images/challenges-images.png",
                        LocalDateTime.now(),
                        1000,
                    100)
                )
            )
        )

        composeTestRule.setContent {
            ChallengeView(
                cid = "163f921c-ML2eCQ52mAQlvCEQZ2n",
                fnViewImageCallback = { route = "image-view/$it" },
                fnClaimHuntCallback = { route = "claim/$it" } ,
                onBack = { route = ".." },
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

        composeTestRule.onNodeWithText("Hunt")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule.onNodeWithTag("description-more-btn")
            .performScrollTo()
            .assertIsDisplayed()
            .assert(hasText("more…"))
            .performClick()
    }

}